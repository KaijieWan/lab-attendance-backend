package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.LabSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LabSessionWithRemainingCapacityDTO {
    private LabSession labSession;
    private int remainingCapacity;
}
