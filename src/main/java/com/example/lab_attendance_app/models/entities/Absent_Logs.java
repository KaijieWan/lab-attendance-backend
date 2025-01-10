package com.example.lab_attendance_app.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "absent_logs")
public class Absent_Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID LogID;

    private UUID AbsentID;

    private Date Timestamp;
    private String Updater;
    private String Remarks;

    @Nullable
    private String Labels;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("AbsentID")
    @JoinColumn(name = "Absent_ID")
    @JsonIgnore
    private Absent_Details AbsentDetails;

    public Absent_Logs(UUID AbsentID, String Updater, String Remarks, String Labels) {
        this.AbsentID = AbsentID;
        this.Timestamp = new Date();
        this.Updater = Updater;
        this.Remarks = Remarks;
        this.Labels = Labels;
    }
}

