package com.utc2.appreborn.ui.tuition;

public class DormTuition {
    private String roomName;
    private String details;
    private String amount;

    public DormTuition(String roomName, String details, String amount) {
        this.roomName = roomName;
        this.details = details;
        this.amount = amount;
    }

    public String getRoomName() { return roomName; }
    public String getDetails() { return details; }
    public String getAmount() { return amount; }
}