package com.utc2.appreborn.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ApiService
 * ──────────────────────────────────────────────────────────────
 * BUG FIX: FILTER_STUDENT_NEWS bị thừa dấu phẩy cuối chuỗi
 *   TRƯỚC:  "type==STUDENT_ANNOUNCEMENT,display==true,"  ← lỗi
 *   SAU:    "type==STUDENT_ANNOUNCEMENT,display==true"   ← đúng
 *
 * Dấu phẩy thừa khiến server parse RSQL thất bại → trả về
 * empty data → app fall back xuống mock → content rỗng.
 *
 * Package: com.utc2.appreborn.data.remote
 */
public interface ApiService {

    // ✅ Bỏ dấu phẩy cuối
    String FILTER_STUDENT_NEWS   = "type==STUDENT_ANNOUNCEMENT,display==true";
    String SORT_FIELD_CREATED_AT = "created_at";
    String SORT_ORDER_DESC       = "DESC";

    @GET("post")
    Call<NewsResponse> getPosts(
            @Query("currentPage")  int    currentPage,
            @Query("pageSize")     int    pageSize,
            @Query("sortField")    String sortField,
            @Query("sortOrder")    String sortOrder,
            @Query("filters")      String filters,
            @Query("subCategorys") String subCategorys
    );
}