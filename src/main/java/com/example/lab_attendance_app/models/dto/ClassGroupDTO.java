package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.ClassGroup;

import java.io.Serializable;

public class ClassGroupDTO implements Serializable {
    private ClassGroupIdDTO classGroupId;

    public ClassGroupDTO() {
    }

    public ClassGroupDTO(ClassGroupIdDTO classGroupId) {
        this.classGroupId = classGroupId;
    }

    public ClassGroup toEntity() {
        return new ClassGroup()
                .setClassGroupId(classGroupId.toEntity());
    }

    public ClassGroupIdDTO getClassGroupId() {
        return classGroupId;
    }

    public ClassGroupDTO setClassGroupId(ClassGroupIdDTO classGroupId) {
        this.classGroupId = classGroupId;
        return this;
    }
}
