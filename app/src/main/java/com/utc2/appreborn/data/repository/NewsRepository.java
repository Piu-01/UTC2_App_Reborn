package com.utc2.appreborn.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utc2.appreborn.data.remote.ApiService;
import com.utc2.appreborn.data.remote.NewsResponse;
import com.utc2.appreborn.data.remote.RetrofitClient;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.utils.MockHelper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * NewsRepository
 * ──────────────────────────────────────────────────────────────
 * Nguồn dữ liệu duy nhất cho danh sách thông báo.
 *
 * Chiến lược cache mỗi ngày:
 *   1. Khởi động app → đọc cache từ SharedPreferences.
 *   2. Nếu cache còn hiệu lực (< 24 giờ) → dùng ngay, không gọi API.
 *   3. Nếu cache hết hạn hoặc không có → gọi API thật.
 *   4. API thành công → lưu cache + timestamp mới.
 *   5. API thất bại  → hiện cache cũ (nếu có) hoặc mock data.
 *
 * Package: com.utc2.appreborn.data.repository
 */
public class NewsRepository {

    private static final String TAG = "NewsRepository";

    // ── Cache config ──────────────────────────────────────────
    private static final String PREFS_NAME       = "utc2_news_cache";
    private static final String KEY_NEWS_JSON    = "cached_news_json";
    private static final String KEY_LAST_FETCH   = "last_fetch_timestamp";
    private static final long   CACHE_TTL_MS     = TimeUnit.HOURS.toMillis(24); // 24 giờ

    // ── Singleton ─────────────────────────────────────────────
    private static NewsRepository instance;

    public static NewsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NewsRepository(context.getApplicationContext());
        }
        return instance;
    }

    // Giữ phương thức cũ để không break code hiện tại
    // (HomeViewModel gọi getInstance() không tham số)
    private static Context appContext;

    public static NewsRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Gọi getInstance(context) ít nhất 1 lần trước (từ HomeViewModel hoặc Application).");
        }
        return instance;
    }

    // ── State ─────────────────────────────────────────────────
    private final SharedPreferences prefs;
    private final Gson              gson;

    private final MutableLiveData<List<NewsItem>> newsLiveData;
    private final MutableLiveData<Boolean>        isLoadingLiveData =
            new MutableLiveData<>(false);

    private Call<NewsResponse> activeCall;

    // ── Constructor ───────────────────────────────────────────
    private NewsRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson  = new Gson();

        // Khởi tạo LiveData với cache (nếu có) hoặc mock
        List<NewsItem> cached = loadFromCache();
        newsLiveData = new MutableLiveData<>(
                cached != null ? cached : MockHelper.getMockNewsList());
    }

    // ═══════════════════════════════════════════════════════════
    //  Public API
    // ═══════════════════════════════════════════════════════════

    public LiveData<List<NewsItem>> getNewsLiveData()      { return newsLiveData;      }
    public LiveData<Boolean>        getIsLoadingLiveData() { return isLoadingLiveData; }

    /**
     * Gọi khi HomeFragment/ViewModel khởi động.
     *
     * Logic:
     *   - Cache còn hiệu lực (< 24h) → không gọi API, dùng cache.
     *   - Cache hết hạn hoặc không có → gọi API mới.
     */
    public void fetchNewsIfNeeded() {
        if (isCacheValid()) {
            Log.d(TAG, "Cache còn hiệu lực — bỏ qua API call.");
            return;
        }
        Log.d(TAG, "Cache hết hạn — gọi API mới.");
        fetchFromApi();
    }

    /** Gọi API ngay lập tức, bỏ qua cache (dùng cho pull-to-refresh). */
    public void forceRefresh() {
        fetchFromApi();
    }

    public void cancelActiveCall() {
        if (activeCall != null) { activeCall.cancel(); activeCall = null; }
    }

    // ═══════════════════════════════════════════════════════════
    //  Network
    // ═══════════════════════════════════════════════════════════

    private void fetchFromApi() {
        if (activeCall != null && !activeCall.isCanceled()) activeCall.cancel();

        isLoadingLiveData.postValue(true);

        activeCall = RetrofitClient.api().getPosts(
                1,
                10,
                ApiService.SORT_FIELD_CREATED_AT,
                ApiService.SORT_ORDER_DESC,
                ApiService.FILTER_STUDENT_NEWS,
                ""
        );

        activeCall.enqueue(new Callback<NewsResponse>() {

            @Override
            public void onResponse(@NonNull Call<NewsResponse> call,
                                   @NonNull Response<NewsResponse> response) {
                isLoadingLiveData.postValue(false);

                // Log để debug response thực tế
                Log.d(TAG, "API response code: " + response.code());

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null
                        && !response.body().getData().isEmpty()) {

                    List<NewsItem> items = mapToNewsItems(response.body().getData());
                    Log.d(TAG, "API trả về " + items.size() + " bài.");

                    newsLiveData.postValue(items);
                    saveToCache(items);           // lưu cache
                    updateLastFetchTime();        // cập nhật timestamp

                } else {
                    Log.w(TAG, "API trả về rỗng (code=" + response.code() + ")");
                    // Giữ dữ liệu đang hiển thị (cache hoặc mock)
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled()) return;
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "API thất bại: " + t.getMessage(), t);
                // Giữ cache cũ hoặc mock — không thay đổi LiveData
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    //  Cache helpers
    // ═══════════════════════════════════════════════════════════

    /** Trả về true nếu lần fetch cuối < 24 giờ trước. */
    private boolean isCacheValid() {
        long lastFetch = prefs.getLong(KEY_LAST_FETCH, 0);
        if (lastFetch == 0) return false;
        boolean valid = (System.currentTimeMillis() - lastFetch) < CACHE_TTL_MS;
        Log.d(TAG, "Cache valid: " + valid
                + " | Còn " + TimeUnit.MILLISECONDS.toMinutes(
                CACHE_TTL_MS - (System.currentTimeMillis() - lastFetch))
                + " phút nữa hết hạn.");
        return valid;
    }

    /** Lưu danh sách tin tức vào SharedPreferences dưới dạng JSON. */
    private void saveToCache(List<NewsItem> items) {
        String json = gson.toJson(items);
        prefs.edit().putString(KEY_NEWS_JSON, json).apply();
        Log.d(TAG, "Đã lưu " + items.size() + " tin vào cache.");
    }

    /** Đọc danh sách từ cache. Trả về null nếu không có. */
    private List<NewsItem> loadFromCache() {
        String json = prefs.getString(KEY_NEWS_JSON, null);
        if (json == null) return null;
        Type type = new TypeToken<List<NewsItem>>() {}.getType();
        List<NewsItem> items = gson.fromJson(json, type);
        Log.d(TAG, "Đọc cache: " + (items != null ? items.size() : 0) + " tin.");
        return items;
    }

    private void updateLastFetchTime() {
        prefs.edit()
                .putLong(KEY_LAST_FETCH, System.currentTimeMillis())
                .apply();
    }

    // ═══════════════════════════════════════════════════════════
    //  Mapping
    // ═══════════════════════════════════════════════════════════

    private List<NewsItem> mapToNewsItems(List<NewsResponse.PostDto> posts) {
        java.util.List<NewsItem> result = new java.util.ArrayList<>(posts.size());
        for (NewsResponse.PostDto dto : posts) {
            result.add(new NewsItem(
                    dto.getId(),
                    dto.getTitle(),
                    dto.getDisplayDate(),
                    dto.getSummary() != null ? dto.getSummary() : "",
                    dto.getContent()   // getContent() tự fallback content→body→description
            ));
        }
        return result;
    }
}