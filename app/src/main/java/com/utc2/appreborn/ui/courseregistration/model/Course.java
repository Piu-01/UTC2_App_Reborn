package com.utc2.appreborn.ui.courseregistration.model;

/**
 * Lớp đại diện cho một học phần.
 *
 * [Chương 3 - OOP]
 *  - Kế thừa: extends CourseItem
 *  - Bao đóng: tất cả field đều private, truy cập qua getters
 *  - Override: getDisplayInfo() từ lớp cha
 */
public class Course extends CourseItem {

    // ── Private fields (Bao đóng) ──────────────────────────────────────────
    private final String courseCode;     // Mã môn: VD: IT0588485
    private final int    credits;        // Số tín chỉ
    private final String lecturer;       // Giảng viên
    private final String schedule;       // Thời gian: VD: T2, T4 (7:00-9:30)
    private final String room;           // Phòng: VD: A201
    private final int    maxStudents;    // Sĩ số tối đa
    private       int    currentStudents;// Sĩ số hiện tại
    private final String semester;       // Học kỳ: VD: HK2
    private final String faculty;        // Khoa: VD: CNTT
    private final String major;          // Ngành: VD: CNTT
    private final String khoaHoc;        // Khóa học: VD: K65
    private final String ngayBatDau;     // Ngày bắt đầu: VD: 27/09/2026
    private final String ngayKetThuc;    // Ngày kết thúc: VD: 23/12/2026
    private final int    soTiet;         // Số tiết học

    public Course(String id, String name, String courseCode, int credits,
                  String lecturer, String schedule, String room,
                  int maxStudents, int currentStudents,
                  String semester, String faculty, String major, String khoaHoc,
                  String ngayBatDau, String ngayKetThuc, int soTiet) {
        super(id, name);
        this.courseCode      = courseCode;
        this.credits         = credits;
        this.lecturer        = lecturer;
        this.schedule        = schedule;
        this.room            = room;
        this.maxStudents     = maxStudents;
        this.currentStudents = currentStudents;
        this.semester        = semester;
        this.faculty         = faculty;
        this.major           = major;
        this.khoaHoc         = khoaHoc;
        this.ngayBatDau      = ngayBatDau;
        this.ngayKetThuc     = ngayKetThuc;
        this.soTiet          = soTiet;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String getCourseCode()      { return courseCode; }
    public int    getCredits()         { return credits; }
    public String getLecturer()        { return lecturer; }
    public String getSchedule()        { return schedule; }
    public String getRoom()            { return room; }
    public int    getMaxStudents()     { return maxStudents; }
    public int    getCurrentStudents() { return currentStudents; }
    public String getSemester()        { return semester; }
    public String getFaculty()         { return faculty; }
    public String getMajor()           { return major; }
    public String getKhoaHoc()         { return khoaHoc; }
    public String getNgayBatDau()      { return ngayBatDau; }
    public String getNgayKetThuc()     { return ngayKetThuc; }
    public int    getSoTiet()          { return soTiet; }

    public boolean isAvailable() {
        return currentStudents < maxStudents;
    }

    public void incrementStudents() {
        currentStudents++;
    }

    /** Chuỗi hiển thị chi tiết trong card môn học (theo ảnh mẫu). */
    @Override
    public String getDisplayInfo() {
        return "Mã môn: " + courseCode
                + "\nSố tín chỉ: " + credits
                + "\nGiảng viên: " + lecturer
                + "\nThời gian: " + schedule
                + "\nPhòng: " + room
                + "\nBắt đầu: " + ngayBatDau + " - " + ngayKetThuc
                + "\nSố tiết: " + soTiet;
    }
}