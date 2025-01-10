package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;

import java.io.Serializable;

public class ClassGroupIdDTO implements Serializable {
    private String classGroupID;
    private String moduleCode;
    private String semesterID;

    public ClassGroupIdDTO() {
    }

    public ClassGroupIdDTO(String classGroupID, String moduleCode, String semester_ID) {
        this.moduleCode = moduleCode;
        this.classGroupID = classGroupID;
        this.semesterID = semester_ID;
    }

    public ClassGroupId toEntity() {
        return new ClassGroupId()
                .setClassGroupID(classGroupID)
                .setModuleCode(moduleCode)
                .setSemesterID(semesterID);
    }

    public String getClassGroupID() {return classGroupID;}

    public ClassGroupIdDTO setClassGroupID(String classGroupID) {
        this.classGroupID = classGroupID;
        return this;
    }

    public String getModuleCode() {return moduleCode;}

    public ClassGroupIdDTO setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }

    public String getSemesterID() {return semesterID;}

    public ClassGroupIdDTO setSemesterID(String semesterID) {
        this.semesterID = semesterID;
        return this;
    }
}
