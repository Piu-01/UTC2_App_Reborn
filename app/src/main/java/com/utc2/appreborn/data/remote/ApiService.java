package com.utc2.appreborn.data.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ApiService — UPDATED
 * ──────────────────────────────────────────────────────────────
 * Thêm getRawPosts() trả về ResponseBody thay vì NewsResponse.
 *
 * Lý do: Khi Gson parse thất bại (field name sai), ta cần
 * đọc raw JSON string để parse thủ công qua
 * NewsResponse.parseFromRawJson().
 *
 * Package: com.utc2.appreborn.data.remote
 */
public interface ApiService {

    String FILTER_STUDENT_NEWS   = "type==STUDENT_ANNOUNCEMENT,display==true";
    String SORT_FIELD_CREATED_AT = "created_at";
    String SORT_ORDER_DESC       = "DESC";

    /**
     * Trả về ResponseBody (raw JSON) để repository tự parse.
     * Dùng thay cho getPosts() khi cần xem cấu trúc JSON thực tế.
     */
    @GET("post")
    Call<ResponseBody> getRawPosts(
            @Query("currentPage")  int    currentPage,
            @Query("pageSize")     int    pageSize,
            @Query("sortField")    String sortField,
            @Query("sortOrder")    String sortOrder,
            @Query("filters")      String filters,
            @Query("subCategorys") String subCategorys
    );

    /**
     * Phiên bản cũ giữ lại để không break các class khác
     * import ApiService.
     */
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