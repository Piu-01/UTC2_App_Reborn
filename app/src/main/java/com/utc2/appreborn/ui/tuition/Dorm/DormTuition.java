package com.utc2.appreborn.ui.tuition.Dorm;

public class DormTuition {
    private String roomName;
    private String details;
    private long amount; // Đổi sang long để tính toán và format tiền tệ
    private int status;  // 0: Chưa đóng, 1: Đã đóng

    // Cập nhật Constructor để nhận đủ thông tin cần thiết
    public DormTuition(String roomName, String details, long amount, int status) {
        this.roomName = roomName;
        this.details = details;
        this.amount = amount;
        this.status = status;
    }

    // Getters
    public String getRoomName() { return roomName; }
    public String getDetails() { return details; }
    public long getAmount() { return amount; }
    public int getStatus() { return status; }

    // Setters (Nếu cần cập nhật sau khi đóng tiền)
    public void setStatus(int status) { this.status = status; }
}