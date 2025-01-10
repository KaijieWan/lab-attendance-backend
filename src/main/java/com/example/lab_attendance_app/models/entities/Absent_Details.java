package com.example.lab_attendance_app.models.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "absent_details")
public class Absent_Details {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Absent_ID;

    @Nullable
    private String Reason;

    @Nullable
    private String ImageLink;

    private String Status;

    @Nullable
    private Integer MakeUpAttendanceID;

    @Nullable
    private String otherChoosableMakeUpLabSessionsIDs;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("MakeUpAttendanceID")
    @JoinColumn(name = "Attendance_ID")
    private Attendance MakeUpAttendance;

    @OneToMany(mappedBy = "AbsentDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Absent_Logs> logs;
}
