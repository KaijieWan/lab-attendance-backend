package com.example.lab_attendance_app.models.entities;

import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import com.example.lab_attendance_app.models.entities.embedded.LabId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lab_session", indexes = {
    @Index(name = "idx_lab_session_class_group", columnList = "class_group_id, module_code, semester_id")
})
public class LabSession {
    @Id
    private String LabSessionID;
    private LocalDate Date;
    private LocalTime StartTime;
    private LocalTime EndTime;
    private Boolean isMakeUpLabSession;
    private ClassGroupId classGroupID;
    private LabId labID;

    public LabSession(String LabSessionID, LocalDate Date, LocalTime StartTime, LocalTime EndTime, Boolean isMakeUpLabSession, ClassGroupId classGroupID, LabId labID) {
        this.LabSessionID = LabSessionID;
        this.Date = Date;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.classGroupID = classGroupID;
        this.labID = labID;
        this.isMakeUpLabSession = isMakeUpLabSession;
    }

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "class_group_id", insertable = false, updatable = false),
            @JoinColumn(name = "module_code", insertable = false, updatable = false),
            @JoinColumn(name = "semester_id", insertable = false, updatable = false)
    })
    private ClassGroup classGroup;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "labName", insertable = false, updatable = false),
            @JoinColumn(name = "room", insertable = false, updatable = false)
    })
    private Lab lab;
}



