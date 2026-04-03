package com.utc2.appreborn.ui.profile;

public class Subject {

    private String code;
    private String name;
    private String credit;
    private String score;

    public Subject(String code, String name, String credit, String score) {
        this.code = code;
        this.name = name;
        this.credit = credit;
        this.score = score;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCredit() {
        return credit;
    }

    public String getScore() {
        return score;
    }
}