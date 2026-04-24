package com.utc2.appreborn.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.remote.ApiService;
import com.utc2.appreborn.data.remote.NewsResponse;
import com.utc2.appreborn.data.remote.RetrofitClient;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.utils.MockHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * NewsRepository
 * ──────────────────────────────────────────────────────────────
 * Single source of truth for news data.
 *
 * Strategy:
 *  1. Immediately emits mock data so the UI always has something
 *     to display while the network call is in-flight.
 *  2. On a successful API response, replaces mock data with
 *     real items.
 *  3. On failure, keeps mock data and logs the error.
 *
 * Exposes {@link LiveData} so the ViewModel/UI can observe
 * without holding a reference to this class.
 *
 * Package: com.utc2.appreborn.data.repository
 */
public class NewsRepository {

    private static final String TAG = "NewsRepository";

    // ── Singleton ─────────────────────────────────────────────
    private static NewsRepository instance;

    public static NewsRepository getInstance() {
        if (instance == null) {
            instance = new NewsRepository();
        }
        return instance;
    }

    private NewsRepository() {}

    // ── LiveData exposed to ViewModel ─────────────────────────
    private final MutableLiveData<List<NewsItem>> newsLiveData =
            new MutableLiveData<>(MockHelper.getMockNewsList());

    private final MutableLiveData<Boolean> isLoadingLiveData =
            new MutableLiveData<>(false);

    // Active Retrofit call — kept so callers can cancel it
    private Call<NewsResponse> activeCall;

    // ═══════════════════════════════════════════════════════════
    //  Public API
    // ═══════════════════════════════════════════════════════════

    /**
     * Observable list of {@link NewsItem}s.
     *
     * Starts with mock data; updates to real data once the
     * network call completes successfully.
     */
    public LiveData<List<NewsItem>> getNewsLiveData() {
        return newsLiveData;
    }

    /** True while the API call is in-flight. */
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    /**
     * Triggers a fresh API fetch.
     * Safe to call multiple times — cancels any in-flight call first.
     */
    public void fetchNews() {
        // Cancel previous call if still running
        if (activeCall != null && !activeCall.isCanceled()) {
            activeCall.cancel();
        }

        isLoadingLiveData.postValue(true);

        activeCall = RetrofitClient.api().getPosts(
                /* currentPage */ 1,
                /* pageSize    */ 10,
                /* sortField   */ ApiService.SORT_FIELD_CREATED_AT,
                /* sortOrder   */ ApiService.SORT_ORDER_DESC,
                /* filters     */ ApiService.FILTER_STUDENT_NEWS
        );

        activeCall.enqueue(new Callback<NewsResponse>() {

            @Override
            public void onResponse(@NonNull Call<NewsResponse> call,
                                   @NonNull Response<NewsResponse> response) {
                isLoadingLiveData.postValue(false);

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getData() != null
                        && !response.body().getData().isEmpty()) {

                    List<NewsItem> realNews = mapToNewsItems(response.body().getData());
                    newsLiveData.postValue(realNews);

                } else {
                    // Server returned 2xx but empty / null body — keep mock
                    Log.w(TAG, "API returned empty body (code=" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled()) return; // intentional cancel — ignore
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "News API failed: " + t.getMessage(), t);
                // Mock data is already set — no need to update LiveData
            }
        });
    }

    /** Cancels any active call (call from ViewModel's onCleared). */
    public void cancelActiveCall() {
        if (activeCall != null) {
            activeCall.cancel();
            activeCall = null;
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  Mapping helpers
    // ═══════════════════════════════════════════════════════════

    private List<NewsItem> mapToNewsItems(List<NewsResponse.PostDto> posts) {
        List<NewsItem> result = new ArrayList<>(posts.size());
        for (NewsResponse.PostDto dto : posts) {
            result.add(new NewsItem(
                    dto.getId(),
                    dto.getTitle(),
                    dto.getDisplayDate(),
                    dto.getSummary() != null ? dto.getSummary() : ""
            ));
        }
        return result;
    }
}