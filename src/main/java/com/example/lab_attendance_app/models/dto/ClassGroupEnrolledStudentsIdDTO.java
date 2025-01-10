package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.embedded.StudentEnrolledClassGroupId;

import java.io.Serializable;

public class ClassGroupEnrolledStudentsIdDTO implements Serializable {
    private String studentId;
    private String classGroupId;
    private String moduleCode;
    private String semesterID;

    public ClassGroupEnrolledStudentsIdDTO() {
    }

    public ClassGroupEnrolledStudentsIdDTO(String studentId, String classGroupId, String moduleCode, String semester_ID) {
        this.studentId = studentId;
        this.moduleCode = moduleCode;
        this.classGroupId = classGroupId;
        this.semesterID = semester_ID;
    }

    public StudentEnrolledClassGroupId toEntity() {
        return new StudentEnrolledClassGroupId()
                .setStudentId(studentId)
                .setClassGroupId(classGroupId)
                .setModuleCode(moduleCode)
                .setSemesterID(semesterID);
    }

    public String getStudentId() {return studentId;}

    public ClassGroupEnrolledStudentsIdDTO setStudentId(String studentID) {
        this.studentId = studentID;
        return this;
    }

    public String getClassGroupId() {return classGroupId;}

    public ClassGroupEnrolledStudentsIdDTO setClassGroupId(String classGroupId) {
        this.classGroupId = classGroupId;
        return this;
    }

    public String getSemesterID() {return semesterID;}

    public ClassGroupEnrolledStudentsIdDTO setSemesterID(String semesterID) {
        this.semesterID = semesterID;
        return this;
    }

    public String getModuleCode() {return moduleCode;}

    public ClassGroupEnrolledStudentsIdDTO setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }


}
