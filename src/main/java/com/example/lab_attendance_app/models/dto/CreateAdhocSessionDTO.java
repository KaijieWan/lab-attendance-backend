package com.example.lab_attendance_app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAdhocSessionDTO {
    private String class_group_id;
    private String module_code;
    private String lab_name;
    private Integer room;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String adhocSessionID;
    private String semesterID;
    private String sessionContent;
} 
