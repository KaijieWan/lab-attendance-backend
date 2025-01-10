package com.example.lab_attendance_app.models.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attendance", indexes = {
    @Index(name = "idx_attendance_lab_session_student", columnList = "Lab_SessionID, Student_ID"),
    @Index(name = "idx_attendance_semester_status", columnList = "Semester_ID, Status")
})
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Attendance_ID;
    private String Student_ID;
    private String Lab_SessionID;
    private String Status;
    @Nullable
    private UUID Absent_ID;
    private Boolean isMakeUpSession;
    private String Semester_ID;
    private String Remarks;

    @ManyToOne
    @MapsId("Student_ID")
    @JoinColumn(name = "Student_ID")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "Lab_SessionID", insertable = false, updatable = false)
    private LabSession labsession;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "Absent_ID", insertable = false, updatable = false)
    private Absent_Details absentDetails;

    @ManyToOne
    @JoinColumn(name = "Semester_ID", insertable = false, updatable = false)
    private Semester semester;
}
