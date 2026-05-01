package com.utc2.appreborn.ui.Info.model;

public class InfoItem {
    private String birthPlace;
    private String cccd;
    private String permanentAddress;
    private String tempAddress;
    private String currentAddress;
    private String studentCardUrl; // Link ảnh thẻ sinh viên trên server

    public InfoItem(String birthPlace, String cccd, String permanentAddress, String tempAddress, String currentAddress, String studentCardUrl) {
        this.birthPlace = birthPlace;
        this.cccd = cccd;
        this.permanentAddress = permanentAddress;
        this.tempAddress = tempAddress;
        this.currentAddress = currentAddress;
        this.studentCardUrl = studentCardUrl;
    }


    // Getters
    public String getBirthPlace() { return birthPlace; }
    public String getCccd() { return cccd; }
    public String getPermanentAddress() { return permanentAddress; }
    public String getTempAddress() { return tempAddress; }
    public String getCurrentAddress() { return currentAddress; }
    public String getStudentCardUrl() { return studentCardUrl; }
}