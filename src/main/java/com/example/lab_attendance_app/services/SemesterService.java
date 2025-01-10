package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.Semester;

public interface SemesterService {

    public ExecutionStatus createSemester(Semester semester);
}
