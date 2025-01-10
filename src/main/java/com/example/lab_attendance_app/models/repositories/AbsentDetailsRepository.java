package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Absent_Details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface AbsentDetailsRepository extends JpaRepository<Absent_Details, UUID> {
    @Query("SELECT a FROM Absent_Details a WHERE a.MakeUpAttendanceID = :makeupAttendanceID")
    Optional<Absent_Details> getAbsentDetailsByMakeUpAttendanceID(@Param("makeupAttendanceID") Integer makeupAttendanceID);

    @Query("SELECT a FROM Absent_Details a WHERE a.Absent_ID = :absentId")
    Optional<Absent_Details> findAbsentDetailsById(@Param("absentId") UUID absentId);

    @Query("DELETE FROM Absent_Details ad WHERE ad.Absent_ID IN (SELECT a.Absent_ID FROM Attendance a WHERE a.semester.semester_ID = :semesterID)")
    @Transactional
    @Modifying
    void deleteNormalAbsencesBySemesterID(@Param("semesterID") String semesterID);

    @Query("DELETE FROM Absent_Details ad WHERE ad.Absent_ID IN (SELECT a.Absent_ID FROM Attendance a WHERE a.semester.semester_ID = :semesterID AND a.Student_ID = :studentID AND a.Lab_SessionID = :labSessionID)")
    @Transactional
    @Modifying
    void deleteNormalAbsencesBySemesterIDAndStudentIDAndLabSessionID(@Param("semesterID") String semesterID, @Param("studentID") String studentID, @Param("labSessionID") String labSessionID);

    @Query("DELETE FROM Absent_Details ad WHERE ad.MakeUpAttendanceID IN (SELECT a.Attendance_ID FROM Attendance a WHERE a.semester.semester_ID = :semesterID)")
    @Transactional
    @Modifying
    void deleteMakeUpAbsencesBySemesterID(@Param("semesterID") String semesterID);

    @Query("DELETE FROM Absent_Details ad WHERE ad.MakeUpAttendanceID IN (SELECT a.Attendance_ID FROM Attendance a WHERE a.semester.semester_ID = :semesterID AND a.Lab_SessionID = :labSessionID AND a.Student_ID = :studentID)")
    @Transactional
    @Modifying
    void deleteMakeUpAbsencesBySemesterIDAndStudentIDAndLabSessionID(@Param("semesterID") String semesterID, @Param("studentID") String studentID, @Param("labSessionID") String labSessionID);

    @Query("DELETE FROM Absent_Details ad WHERE ad.Absent_ID NOT IN (SELECT a.Absent_ID FROM Attendance a)")
    @Transactional
    @Modifying
    void deleteAbsencesWithNoParentAttendanceBySemesterID();

    @Query("SELECT COUNT(ad) FROM Absent_Details ad WHERE ad.Status = 'Awaiting Review' AND ad.Absent_ID IN (SELECT a.Absent_ID FROM Attendance a WHERE a.Semester_ID = :semesterId)")
    Integer countPendingAbsencesBySemesterId(@Param("semesterId") String semesterId);

    @Query("SELECT COUNT(ad) FROM Absent_Details ad WHERE ad.Status = 'Awaiting Review' AND ad.Absent_ID IN (SELECT a.Absent_ID FROM Attendance a WHERE a.Semester_ID = :semesterId AND a.labsession.classGroupID.moduleCode = :moduleCode)")
    Integer countPendingAbsencesBySemesterIdAndModule(@Param("semesterId") String semesterId, @Param("moduleCode") String moduleCode);

    @Query("SELECT COUNT(ad) FROM Absent_Details ad WHERE ad.Status = 'Awaiting Make Up' AND ad.Absent_ID IN (SELECT a.Absent_ID FROM Attendance a WHERE a.Semester_ID = :semesterId)")
    Integer countAwaitingMakeUpAbsencesBySemesterId(@Param("semesterId") String semesterId);
}