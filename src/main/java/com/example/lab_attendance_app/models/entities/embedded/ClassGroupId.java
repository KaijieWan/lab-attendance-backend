package com.example.lab_attendance_app.models.entities.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ClassGroupId implements Serializable {
    @Column(name = "class_group_id")
    private String classGroupID;
    @Column(name = "module_code")
    private String moduleCode;

    @Column(name = "semester_id")
    private String semesterID;

    public ClassGroupId() {
    }

    public ClassGroupId(String classGroupID, String moduleCode, String semesterID) {
        this.classGroupID = classGroupID;
        this.moduleCode = moduleCode;
        this.semesterID = semesterID;
    }

    public String getClassGroupID() {
        return classGroupID;
    }

    public ClassGroupId setClassGroupID(String classGroupID) {
        this.classGroupID = classGroupID;
        return this;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public ClassGroupId setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }

    public String getSemesterID() {
        return semesterID;
    }

    public ClassGroupId setSemesterID(String semesterID) {
        this.semesterID = semesterID;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassGroupId that = (ClassGroupId) o;
        return Objects.equals(classGroupID, that.classGroupID) &&
                Objects.equals(moduleCode, that.moduleCode) &&
                Objects.equals(semesterID, that.semesterID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classGroupID, moduleCode, semesterID);
    }
}
