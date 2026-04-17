package com.utc2.appreborn.ui.tuition.Subject;

public class SubjectTuition {
    private int id; // Tham số thứ 1
    private String name; // Tham số thứ 2
    private String details; // Tham số thứ 3
    private long amount; // Tham số thứ 4 (Kiểu long để tính toán)
    private int status; // Tham số thứ 5 (0: Chưa đóng, 1: Đã đóng)

    // Constructor phải có đủ 5 tham số này
    public SubjectTuition(int id, String name, String details, long amount, int status) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.amount = amount;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDetails() { return details; }
    public long getAmount() { return amount; }
    public int getStatus() { return status; }
}