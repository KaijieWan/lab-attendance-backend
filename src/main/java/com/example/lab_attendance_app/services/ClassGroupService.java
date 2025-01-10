package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.ClassGroup;

import java.util.List;

public interface ClassGroupService {

    public List<ClassGroup> getAllClassGroups();
    public ExecutionStatus createClassGroup(ClassGroup classGroup);
}
