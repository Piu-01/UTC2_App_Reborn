package com.utc2.appreborn.data.remote;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * NewsResponse
 * ──────────────────────────────────────────────────────────────
 * Top-level DTO Gson deserialises from:
 *   GET https://utc2.edu.vn/api/v1.0/post
 *
 * Adjust {@code @SerializedName} values if the real API
 * envelope uses different field names.
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

    /** The list of news posts returned for this page. */
    @SerializedName("data")
    private List<PostDto> data;

    // ── Getters ───────────────────────────────
    public int           getTotal()    { return total;    }
    public int           getPage()     { return page;     }
    public int           getPageSize() { return pageSize; }
    public List<PostDto> getData()     { return data;     }

    // ═══════════════════════════════════════════════════════════
    //  Inner DTO — single post item
    // ═══════════════════════════════════════════════════════════
    public static class PostDto {

        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        /** ISO-8601 timestamp, e.g. "2025-04-20T08:00:00Z". */
        @SerializedName("created_at")
        private String createdAt;

        /** Short excerpt shown in the news feed list. */
        @SerializedName("summary")
        private String summary;

        /** Full article body (used on the detail screen). */
        @SerializedName("content")
        private String content;

        /** Category tag, e.g. "STUDENT_ANNOUNCEMENT". */
        @SerializedName("type")
        private String type;

        // ── Getters ───────────────────────────
        public String getId()        { return id;        }
        public String getTitle()     { return title;     }
        public String getCreatedAt() { return createdAt; }
        public String getSummary()   { return summary;   }
        public String getContent()   { return content;   }
        public String getType()      { return type;      }

        /**
         * Returns a display-ready date string.
         * Trims ISO-8601 to "yyyy-MM-dd", or returns raw value
         * if it's already a short string.
         */
        public String getDisplayDate() {
            if (createdAt != null && createdAt.length() >= 10) {
                return createdAt.substring(0, 10);
            }
            return createdAt != null ? createdAt : "";
        }
    }
}