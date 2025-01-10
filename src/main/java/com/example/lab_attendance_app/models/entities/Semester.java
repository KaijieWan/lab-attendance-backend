package com.example.lab_attendance_app.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "semester")
public class Semester {
    @Id
    @Column(name = "semester_id", nullable = false, unique = true)
    private String semester_ID;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @Column(name = "annual_year", nullable = false)
    private String annualYear;

    @Column(name = "week1_startdate", nullable = false)
    private LocalDate week1StartDate;

    public String getSemesterID() {
        return semester_ID;
    }

    public Semester setSemesterID(String semesterID) {
        this.semester_ID = semesterID;
        return this;
    }

    public Integer getSemester() {
        return semester;
    }

    public Semester setSemester(Integer semester) {
        this.semester = semester;
        return this;
    }

    public String getAnnualYear() {
        return annualYear;
    }

    public Semester setAnnualYear(String annualYear) {
        this.annualYear = annualYear;
        return this;
    }

    public LocalDate getWeek1StartDate() {
        return week1StartDate;
    }

    public Semester setWeek1StartDate(LocalDate week1StartDate) {
        this.week1StartDate = week1StartDate;
        return this;
    }


}
