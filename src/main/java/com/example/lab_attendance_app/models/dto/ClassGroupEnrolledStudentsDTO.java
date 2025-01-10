package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Student_Enrolled_ClassGroup;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.Instant;

public class ClassGroupEnrolledStudentsDTO implements Serializable {
    private ClassGroupEnrolledStudentsIdDTO classGroupEnrolledStudentsId;

    public ClassGroupEnrolledStudentsDTO() {
    }

    public ClassGroupEnrolledStudentsDTO(ClassGroupEnrolledStudentsIdDTO classGroupEnrolledStudentsId) {
        this.classGroupEnrolledStudentsId = classGroupEnrolledStudentsId;
    }

    public Student_Enrolled_ClassGroup toEntity() {
        return new Student_Enrolled_ClassGroup()
                .setId(classGroupEnrolledStudentsId.toEntity());
    }

    public ClassGroupEnrolledStudentsIdDTO getClassGroupEnrolledStudentsId() {
        return classGroupEnrolledStudentsId;
    }

    public ClassGroupEnrolledStudentsDTO setClassGroupEnrolledStudentsId(ClassGroupEnrolledStudentsIdDTO classGroupEnrolledStudentsId) {
        this.classGroupEnrolledStudentsId = classGroupEnrolledStudentsId;
        return this;
    }
}
