package com.utc2.appreborn.data.remote;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * NewsResponse
 * ──────────────────────────────────────────────────────────────
 * DTO mapping JSON từ:
 *   GET https://utc2.edu.vn/api/v1.0/post
 *
 * Package: com.utc2.appreborn.data.remote
 */
public class NewsResponse {

    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("data")
    private List<PostDto> data;

    public int           getTotal()    { return total;    }
    public int           getPage()     { return page;     }
    public int           getPageSize() { return pageSize; }
    public List<PostDto> getData()     { return data;     }

    // ═══════════════════════════════════════════════════════════
    //  PostDto — một bài đăng
    // ═══════════════════════════════════════════════════════════
    public static class PostDto {

        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("summary")
        private String summary;

        /**
         * Nội dung HTML bài đăng.
         * API UTC2 có thể dùng "content" hoặc "body" tuỳ endpoint.
         * Gson lấy field đầu tiên không null.
         */
        @SerializedName("content")
        private String content;

        @SerializedName("body")
        private String body;

        @SerializedName("description")
        private String description;

        @SerializedName("type")
        private String type;

        @SerializedName("slug")
        private String slug;

        // ── Getters ───────────────────────────────────────────
        public String getId()        { return id;        }
        public String getTitle()     { return title;     }
        public String getCreatedAt() { return createdAt; }
        public String getSummary()   { return summary;   }
        public String getType()      { return type;      }
        public String getSlug()      { return slug;      }

        /**
         * Trả về nội dung HTML từ bất kỳ field nào có dữ liệu.
         * Thứ tự ưu tiên: content → body → description → summary
         */
        public String getContent() {
            if (content     != null && !content.isEmpty())     return content;
            if (body        != null && !body.isEmpty())        return body;
            if (description != null && !description.isEmpty()) return description;
            if (summary     != null && !summary.isEmpty())     return summary;
            return "";
        }

        /**
         * Ngày hiển thị — cắt ISO-8601 thành "yyyy-MM-dd"
         */
        public String getDisplayDate() {
            if (createdAt != null && createdAt.length() >= 10) {
                return createdAt.substring(0, 10);
            }
            return createdAt != null ? createdAt : "";
        }
    }
}