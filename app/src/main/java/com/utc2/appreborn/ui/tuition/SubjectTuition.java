package com.utc2.appreborn.ui.tuition;

public class SubjectTuition {
    private String name;
    private String details;
    private String amount;
    private String status;

    public SubjectTuition(String name, String details, String amount, String status) {
        this.name = name;
        this.details = details;
        this.amount = amount;
        this.status = status;
    }

    // Getters
    public String getName() { return name; }
    public String getDetails() { return details; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
}