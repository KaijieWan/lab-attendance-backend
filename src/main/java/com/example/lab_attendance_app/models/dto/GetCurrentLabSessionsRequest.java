package com.example.lab_attendance_app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCurrentLabSessionsRequest {
    private String labName;
    private int room;
    private LocalTime currentTime;
    private LocalDate currentDate;
}
