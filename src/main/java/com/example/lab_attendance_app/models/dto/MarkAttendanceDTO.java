package com.example.lab_attendance_app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkAttendanceDTO {
    private Integer attendanceID;
    private String status;
    private String approverUsername;
}