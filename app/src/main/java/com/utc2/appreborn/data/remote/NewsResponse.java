package com.utc2.appreborn.data.remote;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * NewsResponse — FLEXIBLE VERSION
 * ──────────────────────────────────────────────────────────────
 * Vấn đề:
 *   @SerializedName("data") không match → list trả về null.
 *   Server UTC2 có thể dùng tên field khác như:
 *     "content", "items", "result", "posts", "records", ...
 *
 * Giải pháp:
 *   - Giữ nguyên các @SerializedName để Gson tự map nếu đúng tên.
 *   - Thêm parseFromJson(String) — thử tất cả tên field phổ biến.
 *   - NewsRepository gọi parseFromJson() với raw body từ interceptor.
 *
 * Package: com.utc2.appreborn.data.remote
 */
public class NewsResponse {

    private static final String LOG_TAG = "UTC2_PARSE";

    // ── Các tên field phổ biến của "mảng bài đăng" ────────────
    // Gson sẽ dùng cái đầu tiên match với JSON thực tế
    @SerializedName("data")
    private List<PostDto> data;

    @SerializedName("content")
    private List<PostDto> content;

    @SerializedName("items")
    private List<PostDto> items;

    @SerializedName("result")
    private List<PostDto> result;

    @SerializedName("posts")
    private List<PostDto> posts;

    @SerializedName("records")
    private List<PostDto> records;

    @SerializedName("total")
    private int total;

    @SerializedName("totalElements")
    private int totalElements;

    @SerializedName("page")
    private int page;

    @SerializedName("pageSize")
    private int pageSize;

    // ── Getter trả về list từ bất kỳ field nào có dữ liệu ────
    public List<PostDto> getData() {
        if (data     != null && !data.isEmpty())    return data;
        if (content  != null && !content.isEmpty()) return content;
        if (items    != null && !items.isEmpty())   return items;
        if (result   != null && !result.isEmpty())  return result;
        if (posts    != null && !posts.isEmpty())   return posts;
        if (records  != null && !records.isEmpty()) return records;
        return null;
    }

    public int getTotal() {
        return total > 0 ? total : totalElements;
    }

    // ══════════════════════════════════════════════════════════
    //  Fallback parser — dùng khi getData() vẫn null
    //  Bóc tách thủ công JsonObject để tìm mảng bài đăng
    // ══════════════════════════════════════════════════════════

    /**
     * Parse raw JSON string thủ công, tìm mảng PostDto ở bất kỳ
     * field nào trong root object.
     *
     * @param rawJson  chuỗi JSON thô từ server
     * @return list PostDto hoặc empty list nếu không tìm thấy
     */
    public static List<PostDto> parseFromRawJson(String rawJson) {
        if (rawJson == null || rawJson.isEmpty()) return new ArrayList<>();

        try {
            JsonElement root = JsonParser.parseString(rawJson);

            // Case 1: root là mảng trực tiếp  →  [{...}, {...}]
            if (root.isJsonArray()) {
                Log.d(LOG_TAG, "Root is JsonArray — parsing directly");
                return parseArray(root.getAsJsonArray());
            }

            if (!root.isJsonObject()) {
                Log.w(LOG_TAG, "Root is neither object nor array");
                return new ArrayList<>();
            }

            JsonObject obj = root.getAsJsonObject();

            // Log tất cả key ở root để biết tên field thực tế
            Log.d(LOG_TAG, "Root keys: " + obj.keySet());

            // Case 2: tìm field là JsonArray trong root
            String[] candidates = {
                    "data", "content", "items", "result", "posts",
                    "records", "list", "news", "announcements",
                    "thongBao", "notifications", "response"
            };

            for (String key : candidates) {
                if (obj.has(key) && obj.get(key).isJsonArray()) {
                    JsonArray arr = obj.getAsJsonArray(key);
                    if (arr.size() > 0) {
                        Log.d(LOG_TAG, "Found array at key '" + key
                                + "' with " + arr.size() + " items");
                        return parseArray(arr);
                    }
                }
            }

            // Case 3: tìm field là JsonObject lồng nhau (nested)
            for (String key : obj.keySet()) {
                JsonElement el = obj.get(key);
                if (el.isJsonObject()) {
                    JsonObject nested = el.getAsJsonObject();
                    Log.d(LOG_TAG, "Checking nested object at key '" + key
                            + "': " + nested.keySet());

                    for (String innerKey : candidates) {
                        if (nested.has(innerKey)
                                && nested.get(innerKey).isJsonArray()) {
                            JsonArray arr = nested.getAsJsonArray(innerKey);
                            if (arr.size() > 0) {
                                Log.d(LOG_TAG, "Found array at '"
                                        + key + "." + innerKey + "'");
                                return parseArray(arr);
                            }
                        }
                    }
                }
            }

            Log.w(LOG_TAG, "No array found in JSON. Full structure:\n"
                    + rawJson.substring(0, Math.min(500, rawJson.length())));

        } catch (Exception e) {
            Log.e(LOG_TAG, "Parse error: " + e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    private static List<PostDto> parseArray(JsonArray arr) {
        Gson gson = new Gson();
        List<PostDto> result = new ArrayList<>(arr.size());

        for (JsonElement el : arr) {
            try {
                PostDto dto = gson.fromJson(el, PostDto.class);
                if (dto != null && dto.getTitle() != null) {
                    result.add(dto);
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "Skip malformed item: " + e.getMessage());
            }
        }

        Log.d(LOG_TAG, "Parsed " + result.size() + "/" + arr.size() + " items");
        return result;
    }

    // ══════════════════════════════════════════════════════════
    //  PostDto
    // ══════════════════════════════════════════════════════════
    public static class PostDto {

        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        // Thử tất cả các tên field ngày phổ biến
        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("createdAt")
        private String createdAt2;

        @SerializedName("date")
        private String date;

        @SerializedName("publishedAt")
        private String publishedAt;

        @SerializedName("summary")
        private String summary;

        @SerializedName("description")
        private String description;

        @SerializedName("excerpt")
        private String excerpt;

        // Nội dung HTML — thử nhiều tên
        @SerializedName("content")
        private String content;

        @SerializedName("body")
        private String body;

        @SerializedName("html")
        private String html;

        @SerializedName("type")
        private String type;

        @SerializedName("slug")
        private String slug;

        // ── Getters ───────────────────────────────────────────

        public String getId()    { return id;    }
        public String getTitle() { return title; }
        public String getType()  { return type;  }
        public String getSlug()  { return slug;  }

        /** Ngày đăng từ bất kỳ field nào có dữ liệu */
        public String getDisplayDate() {
            String raw = firstNonEmpty(createdAt, createdAt2, publishedAt, date);
            if (raw == null) return "";
            // Cắt ISO-8601: "2025-04-20T..." → "2025-04-20"
            return raw.length() >= 10 ? raw.substring(0, 10) : raw;
        }

        /** Summary/excerpt từ bất kỳ field nào có dữ liệu */
        public String getSummary() {
            return firstNonEmpty(summary, description, excerpt, "");
        }

        /** HTML content từ bất kỳ field nào có dữ liệu */
        public String getContent() {
            return firstNonEmpty(content, body, html, description, summary, "");
        }

        private String firstNonEmpty(String... values) {
            for (String v : values) {
                if (v != null && !v.isEmpty()) return v;
            }
            return null;
        }
    }
}