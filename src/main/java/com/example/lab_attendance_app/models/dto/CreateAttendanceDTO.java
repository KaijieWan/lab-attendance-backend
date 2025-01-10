package com.example.lab_attendance_app.models.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAttendanceDTO {
    @Nullable
    private UUID absentID;
    private String labSessionID;
    private String remarks;
    private String semesterID;
    private String status;
    private Boolean isMakeUpSession;
    private String studentID;
}
