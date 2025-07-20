package com.example.lab_attendance_app.models.dto;


import com.example.lab_attendance_app.models.entities.Student;

import java.io.Serializable;

/**
 * DTO for {@link Student}
 */
public class StudentDTO implements Serializable {
    private String studentID;
    private String fullName;

    public StudentDTO() {
    }

    public StudentDTO(String studentID, String fullName) {
        this.studentID = studentID;
        this.fullName = fullName;
    }

    public Student toEntity() {
        return new Student()
                .setStudent_ID(studentID)
                .setFullName(fullName);
    }

    public String getStudentID() {
        return studentID;
    }

    public StudentDTO setStudentID(String studentID) {
        this.studentID = studentID;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public StudentDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
}
