package com.example.lab_attendance_app.models.dto;

public class UpdateAdhocSessionDTO {
    private String semesterID;
    private String moduleCode;
    private String classGroupID;
    private String newLabName;
    private String newRoom;
    private String sessionContent;

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

    public String getSessionContent() {
        return sessionContent;
    }

    public void setSessionContent(String sessionContent) {
        this.sessionContent = sessionContent;
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
