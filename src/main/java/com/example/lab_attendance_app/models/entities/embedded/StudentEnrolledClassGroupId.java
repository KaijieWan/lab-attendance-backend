package com.example.lab_attendance_app.models.entities.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StudentEnrolledClassGroupId implements Serializable {
    @Column(name = "student_id")
    private String student_Id;

    @Column(name = "class_group_id")
    private String classGroupId;

    @Column(name = "module_code")
    private String moduleCode;

    @Column(name = "semester_id")
    private String semester_ID;

    public StudentEnrolledClassGroupId() {
    }

    public StudentEnrolledClassGroupId(String studentId, String classGroupId, String moduleCode, String semesterID) {
        this.student_Id = studentId;
        this.classGroupId = classGroupId;
        this.moduleCode = moduleCode;
        this.semester_ID = semesterID;
    }

    public String getStudentId() {
        return student_Id;
    }

    public StudentEnrolledClassGroupId setStudentId(String studentId) {
        this.student_Id = studentId;
        return this;
    }

    public String getClassGroupId() {
        return classGroupId;
    }

    public StudentEnrolledClassGroupId setClassGroupId(String classGroupId) {
        this.classGroupId = classGroupId;
        return this;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public StudentEnrolledClassGroupId setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }

    public String getSemesterID() {
        return semester_ID;
    }

    public StudentEnrolledClassGroupId setSemesterID(String semesterID) {
        this.semester_ID = semesterID;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentEnrolledClassGroupId that = (StudentEnrolledClassGroupId) o;
        return Objects.equals(student_Id, that.student_Id) &&
                Objects.equals(classGroupId, that.classGroupId) &&
                Objects.equals(moduleCode, that.moduleCode) &&
                Objects.equals(semester_ID, that.semester_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student_Id, classGroupId, moduleCode, semester_ID);
    }
}
