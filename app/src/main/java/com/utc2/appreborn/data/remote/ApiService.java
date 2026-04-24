package com.utc2.appreborn.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ApiService
 * ──────────────────────────────────────────────────────────────
 * Retrofit 2 interface for the UTC2 REST API.
 *
 * Base URL (set in RetrofitClient): https://utc2.edu.vn/api/v1.0/
 *
 * Package: com.utc2.appreborn.data.remote
 */
public interface ApiService {

    // ── Filter / sort constants ───────────────────────────────

    /** RSQL filter for visible student announcements. */
    String FILTER_STUDENT_NEWS     = "type==STUDENT_ANNOUNCEMENT,display==true";
    String SORT_FIELD_CREATED_AT   = "created_at";
    String SORT_ORDER_DESC         = "DESC";

    // ═══════════════════════════════════════════════════════════
    //  Endpoints
    // ═══════════════════════════════════════════════════════════

    /**
     * Fetches a paginated, filtered list of news posts.
     *
     * Full example URL:
     *   /post?currentPage=1&pageSize=10
     *         &sortField=created_at&sortOrder=DESC
     *         &filters=type==STUDENT_ANNOUNCEMENT,display==true
     *
     * @param currentPage  1-based page index
     * @param pageSize     items per page
     * @param sortField    field name to sort by
     * @param sortOrder    "ASC" or "DESC"
     * @param filters      comma-separated RSQL predicate string
     */
    @GET("post")
    Call<NewsResponse> getPosts(
            @Query("currentPage") int    currentPage,
            @Query("pageSize")    int    pageSize,
            @Query("sortField")   String sortField,
            @Query("sortOrder")   String sortOrder,
            @Query("filters")     String filters
    );
}