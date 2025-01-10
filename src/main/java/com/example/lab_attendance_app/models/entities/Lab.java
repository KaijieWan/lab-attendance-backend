package com.example.lab_attendance_app.models.entities;

import com.example.lab_attendance_app.models.entities.embedded.LabId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lab")
public class Lab {
    @EmbeddedId
    private LabId id;
    
    @Column(name = "capacity")
    private Integer capacity;

}


