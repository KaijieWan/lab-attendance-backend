package com.example.lab_attendance_app.models.dto;

public class UpdateLabSessionDTO {
    private String semesterID;
    private String moduleCode;
    private String classGroupID;
    private String newLabName;
    private String newRoom;

    // Getters and setters
    public String getSemesterID() {
        return semesterID;
    }

    public void setSemesterID(String semesterID) {
        this.semesterID = semesterID;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getClassGroupID() {
        return classGroupID;
    }

    public void setClassGroupID(String classGroupID) {
        this.classGroupID = classGroupID;
    }

    public String getNewLabName() {
        return newLabName;
    }

    public void setNewLabName(String newLabName) {
        this.newLabName = newLabName;
    }

    public String getNewRoom() {
        return newRoom;
    }

    public void setNewRoom(String newRoom) {
        this.newRoom = newRoom;
    }
}
