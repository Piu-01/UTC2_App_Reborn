package com.utc2.appreborn.data.remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient
 * ──────────────────────────────────────────────────────────────
 * Thread-safe singleton that owns the single {@link Retrofit}
 * instance for the whole application.
 *
 * Required dependencies (add to build.gradle – app module):
 *   implementation 'com.squareup.retrofit2:retrofit:2.11.0'
 *   implementation 'com.squareup.retrofit2:converter-gson:2.11.0'
 *   implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
 *
 * Package: com.utc2.appreborn.data.remote
 */
public final class RetrofitClient {

    // ── Config ────────────────────────────────────────────────
    public static final String BASE_URL       = "https://utc2.edu.vn/api/v1.0/";
    private static final int   TIMEOUT_SEC    = 30;

    // ── Singleton (double-checked locking) ────────────────────
    private static volatile Retrofit retrofitInstance;

    private RetrofitClient() {}

    /**
     * Returns the shared {@link Retrofit} instance,
     * lazily initialised on first call.
     */
    public static Retrofit getInstance() {
        if (retrofitInstance == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitInstance == null) {
                    retrofitInstance = buildRetrofit();
                }
            }
        }
        return retrofitInstance;
    }

    /**
     * Convenience method — creates a typed {@link ApiService}
     * without the caller needing to call {@code .create()} manually.
     *
     * Usage:
     *   Call<NewsResponse> call = RetrofitClient.api().getPosts(...);
     */
    public static ApiService api() {
        return getInstance().create(ApiService.class);
    }

    // ── Builder ───────────────────────────────────────────────

    private static Retrofit buildRetrofit() {
        // Logging interceptor — change to NONE for release builds
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SEC,    TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SEC,   TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}