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
@Table(name = "adhoc_session", indexes = {
    @Index(name = "idx_adhoc_session_class_group", columnList = "class_group_id, module_code, semester_id")
})
public class AdhocSession {
    @Id
    private String AdhocSessionID;
    private LocalDate Date;
    private LocalTime StartTime;
    private LocalTime EndTime;
    private ClassGroupId classGroupID;
    private String sessionContent;
    private LabId labID;

    public AdhocSession(String AdhocSessionID, LocalDate Date, LocalTime StartTime, LocalTime EndTime, ClassGroupId classGroupID, String sessionContent, LabId labID) {
        this.AdhocSessionID = AdhocSessionID;
        this.Date = Date;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.classGroupID = classGroupID;
        this.sessionContent = sessionContent;
        this.labID = labID;
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



