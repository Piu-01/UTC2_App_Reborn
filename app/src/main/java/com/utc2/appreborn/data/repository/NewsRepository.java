package com.utc2.appreborn.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.utc2.appreborn.data.remote.ApiService;
import com.utc2.appreborn.data.remote.NewsResponse;
import com.utc2.appreborn.data.remote.RetrofitClient;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.utils.MockHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    private static final String TAG        = "UTC2_REPO";
    private static final String PREFS_NAME = "utc2_news_cache_v3";
    private static final String KEY_JSON   = "news_json";
    private static final String KEY_LAST   = "last_fetch_ms";
    private static final long   CACHE_TTL  = TimeUnit.HOURS.toMillis(24);

    private static NewsRepository instance;

    public static NewsRepository getInstance(Context context) {
        if (instance == null)
            instance = new NewsRepository(context.getApplicationContext());
        return instance;
    }

    public static NewsRepository getInstance() {
        if (instance == null)
            throw new IllegalStateException("Call getInstance(context) first.");
        return instance;
    }

    private final SharedPreferences             prefs;
    private final Gson                          gson = new Gson();
    private final MutableLiveData<List<NewsItem>> newsLiveData;
    private final MutableLiveData<Boolean>      isLoadingLiveData =
            new MutableLiveData<>(false);
    private Call<ResponseBody> activeCall;

    private NewsRepository(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<NewsItem> cached = loadFromCache();
        newsLiveData = new MutableLiveData<>(
                cached != null ? cached : MockHelper.getMockNewsList());
    }

    public LiveData<List<NewsItem>> getNewsLiveData()      { return newsLiveData;      }
    public LiveData<Boolean>        getIsLoadingLiveData() { return isLoadingLiveData; }

    public void fetchNewsIfNeeded() {
        if (isCacheValid()) { Log.d(TAG, "Cache OK — skip API"); return; }
        fetchFromApi();
    }

    public void forceRefresh() { fetchFromApi(); }

    public void cancelActiveCall() {
        if (activeCall != null) { activeCall.cancel(); activeCall = null; }
    }

    private void fetchFromApi() {
        if (activeCall != null && !activeCall.isCanceled()) activeCall.cancel();
        isLoadingLiveData.postValue(true);
        Log.d(TAG, "Fetching from API...");

        activeCall = RetrofitClient.api().getRawPosts(
                1, 10,
                ApiService.SORT_FIELD_CREATED_AT,
                ApiService.SORT_ORDER_DESC,
                ApiService.FILTER_STUDENT_NEWS,
                "");

        activeCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                isLoadingLiveData.postValue(false);
                Log.d(TAG, "HTTP " + response.code());

                if (!response.isSuccessful()) {
                    try {
                        String err = response.errorBody() != null
                                ? response.errorBody().string() : "(null)";
                        Log.e(TAG, "Error: " + err);
                    } catch (IOException ignored) {}
                    return;
                }

                if (response.body() == null) {
                    Log.e(TAG, "Body null");
                    return;
                }

                String rawJson;
                try { rawJson = response.body().string(); }
                catch (IOException e) { Log.e(TAG, "Read error: " + e.getMessage()); return; }

                Log.d(TAG, "JSON length: " + rawJson.length());

                // Step 1: standard Gson
                List<NewsResponse.PostDto> posts = null;
                try {
                    NewsResponse parsed = gson.fromJson(rawJson, NewsResponse.class);
                    if (parsed != null) posts = parsed.getData();
                    Log.d(TAG, "Gson getData() = " + (posts == null ? "null" : posts.size()));
                } catch (JsonSyntaxException e) {
                    Log.w(TAG, "Gson failed: " + e.getMessage());
                }

                // Step 2: flexible fallback parser
                if (posts == null || posts.isEmpty()) {
                    Log.w(TAG, "Using flexible parser...");
                    posts = NewsResponse.parseFromRawJson(rawJson);
                }

                if (posts == null || posts.isEmpty()) {
                    Log.e(TAG, "No posts found. Check UTC2_HTTP tag for raw body.");
                    return;
                }

                List<NewsItem> items = map(posts);
                newsLiveData.postValue(items);
                saveToCache(items);
                updateLastFetchTime();
                Log.d(TAG, "Loaded " + items.size() + " items successfully");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "Network error: " + t.getClass().getSimpleName() + " - " + t.getMessage());
                if (t.getCause() != null) Log.e(TAG, "Cause: " + t.getCause().getMessage());
            }
        });
    }

    private boolean isCacheValid() {
        long last = prefs.getLong(KEY_LAST, 0);
        return last != 0 && (System.currentTimeMillis() - last) < CACHE_TTL;
    }

    private void saveToCache(List<NewsItem> items) {
        prefs.edit().putString(KEY_JSON, gson.toJson(items)).apply();
    }

    private List<NewsItem> loadFromCache() {
        String json = prefs.getString(KEY_JSON, null);
        if (json == null) return null;
        try {
            Type type = new TypeToken<List<NewsItem>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            prefs.edit().remove(KEY_JSON).remove(KEY_LAST).apply();
            return null;
        }
    }

    private void updateLastFetchTime() {
        prefs.edit().putLong(KEY_LAST, System.currentTimeMillis()).apply();
    }

    private List<NewsItem> map(List<NewsResponse.PostDto> posts) {
        List<NewsItem> r = new ArrayList<>(posts.size());
        for (NewsResponse.PostDto d : posts) {
            r.add(new NewsItem(d.getId(), d.getTitle(), d.getDisplayDate(),
                    d.getSummary(), d.getContent()));
        }
        return r;
    }
}