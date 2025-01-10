package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface SemesterRepository extends JpaRepository<Semester, String> {
    @Query("DELETE FROM Semester s WHERE s.semester_ID = :semesterID")
    @Modifying
    @Transactional
    void deleteBySemesterID(@Param("semesterID") String semesterID);

    @Query("SELECT s.week1StartDate FROM Semester s WHERE s.semester_ID = :semesterId")
    LocalDate findWeek1StartDateBySemesterId(@Param("semesterId") String semesterId);
}
