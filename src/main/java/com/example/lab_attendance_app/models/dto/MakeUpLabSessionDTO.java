package com.example.lab_attendance_app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakeUpLabSessionDTO {
    LocalDate startDate;
    LocalDate endDate;
    String moduleCode;
}