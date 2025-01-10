package com.example.lab_attendance_app.models.entities.embedded;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LabId implements Serializable {
    private String labName;
    private Integer room;

    public LabId() {
    }

    public LabId(String labName, Integer room) {
        this.labName = labName;
        this.room = room;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabId labId = (LabId) o;
        return Objects.equals(labName, labId.labName) && Objects.equals(room, labId.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labName, room);
    }
}
