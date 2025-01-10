package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.Semester;
import com.example.lab_attendance_app.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SemesterDTO implements Serializable {
    private String semesterID;
    private Integer semester;
    private String annualYear;
    private LocalDate week1StartDate;

    public Semester toEntity(){
        return new Semester()
                .setSemesterID(this.semesterID)
                .setSemester(this.semester)
                .setAnnualYear(this.annualYear)
                .setWeek1StartDate(this.week1StartDate);
    }

}
