package com.example.lab_attendance_app.models.dto;

public class UpdateRemarksDTO {
    private String attendanceID;
    private String newRemarks;

    // Getters and Setters
    public String getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(String attendanceID) {
        this.attendanceID = attendanceID;
    }

    public String getNewRemarks() {
        return newRemarks;
    }

    public void setNewRemarks(String newRemarks) {
        this.newRemarks = newRemarks;
    }
}
