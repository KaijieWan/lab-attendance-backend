package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Absent_Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AbsentLogsRepository extends JpaRepository<Absent_Logs, UUID> {

    @Query("DELETE FROM Absent_Logs al WHERE al.AbsentID IN (SELECT ad.Absent_ID FROM Absent_Details ad WHERE ad.Absent_ID IN (SELECT a.Absent_ID FROM Attendance a WHERE a.semester.semester_ID = :semesterID))")
    @Transactional
    @Modifying
    void deleteBySemesterID(@Param("semesterID") String semesterID);


    @Query("DELETE FROM Absent_Logs al WHERE al.AbsentID IN (SELECT ad.Absent_ID FROM Absent_Details ad WHERE ad.Absent_ID IN (SELECT a.Absent_ID FROM Attendance a WHERE a.semester.semester_ID = :semesterID AND a.Student_ID = :studentID AND a.Lab_SessionID = :labSessionID))")
    @Transactional
    @Modifying
    void deleteStudentAbsentLogsByLabSessionIDAndStudentIDAndSemesterID(@Param("semesterID") String semesterID, @Param("studentID") String studentID, @Param("labSessionID") String labSessionID);
}
