package com.utc2.appreborn.ui.home.model;

public class NewsItem {
    private final String id;
    private final String title;
    private final String date;
    private final String summary;

    public NewsItem(String id, String title, String date, String summary) {
        this.id      = id;
        this.title   = title;
        this.date    = date;
        this.summary = summary;
    }

    public String getId()      { return id; }
    public String getTitle()   { return title; }
    public String getDate()    { return date; }
    public String getSummary() { return summary; }
}
