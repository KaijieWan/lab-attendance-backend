package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Absent_Details;
import com.example.lab_attendance_app.models.entities.Attendance;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    @Query("SELECT a FROM Attendance a WHERE a.Lab_SessionID = :labSessionId AND a.Student_ID = :studentId")
    Optional<Attendance> findByLabSessionIdAndStudentId(@Param("labSessionId") String labSessionId, @Param("studentId") String studentId);

    @Query("SELECT a FROM Attendance a WHERE UPPER(a.Semester_ID) = UPPER(:semesterId) AND UPPER(a.labsession.classGroupID.moduleCode) = UPPER(:moduleCode)")
    List<Attendance> findAttendanceByModuleAndSemesterGroupByClassGroupAndDate(@Param("moduleCode") String moduleCode, @Param("semesterId") String semesterId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Attendance a WHERE a.Lab_SessionID = :labSessionID")
    void deleteAttendancesByLabSessionID(@Param("labSessionID") String labSessionID);

    @Query("SELECT a FROM Attendance a WHERE a.Lab_SessionID = :labSessionId ORDER BY a.student.fullName ASC")
    List<Attendance> findAllByLabSessionId(@Param("labSessionId") String labSessionId);

    @Query("SELECT a FROM Attendance a WHERE a.Student_ID = :studentID AND a.Semester_ID = :semesterID")
    List<Attendance> findAttendancesByStudentIdAndSemester(@Param("studentID") String studentID, @Param("semesterID") String semesterID);

    @Query("SELECT a FROM Attendance a " +
            "LEFT JOIN FETCH a.student s " +
            "LEFT JOIN FETCH a.labsession ls " +
            "LEFT JOIN FETCH a.absentDetails ad " +
            "LEFT JOIN FETCH ad.logs l " +
            "LEFT JOIN FETCH a.semester se " +
            "WHERE a.Semester_ID = :semesterID AND a.absentDetails IS NOT NULL")
    List<Attendance> findAllAbsencesBySemester(@Param("semesterID") String semesterID);

    @Query("SELECT a FROM Attendance a " +
            "LEFT JOIN FETCH a.student s " +
            "LEFT JOIN FETCH a.labsession ls " +
            "LEFT JOIN FETCH a.absentDetails ad " +
            "LEFT JOIN FETCH a.semester se " +
            "WHERE a.Absent_ID = :absenceID AND a.absentDetails IS NOT NULL")
    Optional<Attendance> findAbsentByAbsentID(@Param("absenceID") UUID absenceID);

    // Method to update absence image and reason details
    @Modifying
    @Query("UPDATE Absent_Details a SET a.Reason = :reason, a.ImageLink = :imageLink,a.Status = 'Awaiting Review' WHERE a.Absent_ID = :absentID")
    void updateAbsentDetails(@Param("absentID") UUID absentID, @Param("reason") String reason, @Param("imageLink") String imageLink);

//    @Modifying
//    @Query("UPDATE Absent_Details a SET a.Remarks = :remarks,a.RemarksBy = :remarksBy, a.LastModified = CURRENT_TIMESTAMP WHERE a.Absent_ID = :absentID")
//    void updateAbsentRemarksDetails(@Param("absentID") UUID absentID, @Param("remarks") String remarks, @Param("remarksBy") String remarksBy);

    @Modifying
    @Query("UPDATE Absent_Details a SET a.Status = :approvalStatus WHERE a.Absent_ID = :absentID")
    void approveAbsent(@Param("absentID") UUID absentID, @Param("approvalStatus") String approvalStatus);

    @Query("SELECT a.absentDetails FROM Attendance a WHERE a.absentDetails.Absent_ID = :absentID")
    Optional<Absent_Details> getAbsentDetails(@Param("absentID") UUID absentID);

    @Query("SELECT a FROM Attendance a " +
            "JOIN a.labsession ls " +
            "WHERE ls.labID.labName = :labName " +
            "AND ls.labID.room = :room " +
            "AND ls.Date = :date " +
            "AND ls.StartTime <= :currTime " +
            "AND :currTime <= ls.EndTime ORDER BY a.student.fullName ASC")
    List<Attendance> findAttendancesByLabNameAndRoomAndDateAndTimeRange(@Param("labName") String labName,
                                                                        @Param("room") int room,
                                                                        @Param("date") LocalDate date,
                                                                        @Param("currTime") LocalTime currentTime);

    @Query("SELECT ls, COUNT(a) FROM Attendance a " +
            "JOIN a.labsession ls " +
            "WHERE ls.labID.labName = :labName " +
            "AND ls.labID.room = :room " +
            "AND ls.Date = :date " +
            "AND (ls.StartTime < :endTime AND ls.EndTime > :startTime) " +
            "GROUP BY ls")
    List<Object[]> findOverlappingLabSessionsWithStudentCounts(@Param("labName") String labName,
                                                               @Param("room") int room,
                                                               @Param("date") LocalDate date,
                                                               @Param("startTime") LocalTime startTime,
                                                               @Param("endTime") LocalTime endTime);

    @Query("SELECT a FROM Attendance a " +
            "JOIN a.labsession ls " +
            "WHERE ls.labID.labName = :labName " +
            "AND ls.labID.room = :room " +
            "AND ls.Date = :date " +
            "AND ls.StartTime > :currTime " +
            "AND ls.EndTime <= :endOfDay ORDER BY a.student.fullName ASC")
    List<Attendance> findAttendancesByLabNameAndRoomAndDateAndTimeRangeToEndOfDay(@Param("labName") String labName,
                                                                                  @Param("room") int room,
                                                                                  @Param("date") LocalDate date,
                                                                                  @Param("currTime") LocalTime currentTime,
                                                                                  @Param("endOfDay") LocalTime endOfDay);

    @Query("SELECT a FROM Attendance a WHERE a.Lab_SessionID = :labSessionID AND a.Student_ID = :studentID")
    Optional<Attendance> findAttendanceByLabSessionIdAndSemesterIdAndStudentId(@Param("labSessionID") String labSessionID, @Param("studentID") String studentID);

    @Query("SELECT a FROM Attendance a WHERE (a.labsession.Date < :currentDate OR (a.labsession.Date = :currentDate AND a.labsession.EndTime <= :currentTime)) AND a.Status = 'Pending' AND a.Absent_ID IS NULL")
    List<Attendance> findPendingAbsences(LocalDate currentDate, LocalTime currentTime);

    @Query("SELECT a FROM Attendance a WHERE (a.labsession.Date = :currentDate AND a.labsession.EndTime <= :currentTime) AND a.Status = 'Pending' AND a.Absent_ID IS NULL")
    List<Attendance> findPendingAbsencesForToday(LocalDate currentDate, LocalTime currentTime);

    @Query("DELETE FROM Attendance a WHERE a.labsession.classGroup.classGroupId.semesterID = :semesterID")
    @Transactional
    @Modifying
    void deleteNormalAttendancesBySemesterID(@Param("semesterID") String semesterID);

    @Query("DELETE FROM Attendance a WHERE a.labsession.classGroup.classGroupId.semesterID = :semesterID AND a.Student_ID = :studentID AND a.Lab_SessionID = :labSessionID")
    @Transactional
    @Modifying
    void deleteNormalAttendancesBySemesterIDAndStudentIDAndLabSessionID(@Param("semesterID") String semesterID, @Param("studentID") String studentID, @Param("labSessionID") String labSessionID);

    @Query("DELETE FROM Attendance a WHERE a.labsession.classGroup.classGroupId.semesterID = :semesterID AND a.Absent_ID IS NOT NULL")
    @Transactional
    @Modifying
    void deleteAttendancesWithAbsencesBySemesterID(@Param("semesterID") String semesterID);

    @Query("DELETE FROM Attendance a WHERE a.labsession.classGroup.classGroupId.semesterID = :semesterID AND a.labsession.LabSessionID = :labSessionID AND a.student.Student_ID = :studentID AND a.Absent_ID IS NOT NULL")
    @Transactional
    @Modifying
    void deleteAttendancesWithAbsencesBySemesterIDAndStudentIDAndLabSessionID(@Param("semesterID") String semesterID, @Param("studentID") String studentID, @Param("labSessionID") String labSessionID);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Semester_ID = :semesterId AND a.labsession.Date < :date")
    Long countTotalAttendancesBySemesterIdAndDateBefore(@Param("semesterId") String semesterId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Semester_ID  = :semesterId AND a.labsession.Date < :date AND (a.Status = 'Present' OR a.Status = 'Excused' OR a.Status = 'Late')")
    Long countValidAttendancesBySemesterIdAndDateBefore(@Param("semesterId") String semesterId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Semester_ID = :semesterId AND a.labsession.classGroupID.moduleCode = :moduleId AND a.labsession.Date < :date")
    Long countTotalAttendancesBySemesterIdAndModuleIdAndDateBefore(@Param("semesterId") String semesterId, @Param("moduleId") String moduleId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Semester_ID = :semesterId AND a.labsession.classGroupID.moduleCode = :moduleId AND a.labsession.Date < :date AND a.Status IN ('Present', 'Excused', 'Late')")
    Long countValidAttendancesBySemesterIdAndModuleIdAndDateBefore(@Param("semesterId") String semesterId, @Param("moduleId") String moduleId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Semester_ID = :semesterId AND a.Status = 'Absent'")
    Long countTotalAbsencesBySemesterId(@Param("semesterId") String semesterId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Semester_ID = :semesterId AND a.labsession.classGroupID.moduleCode = :moduleId AND a.Status = 'Absent'")
    Long countTotalAbsencesBySemesterIdAndModuleId(@Param("semesterId") String semesterId, @Param("moduleId") String moduleId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Student_ID = :studentId AND a.Semester_ID = :semesterId AND a.labsession.Date < :date")
    Long countTotalAttendancesByStudentIdAndSemesterIdAndDateBefore(@Param("studentId") String studentId, @Param("semesterId") String semesterId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Student_ID = :studentId AND a.Semester_ID = :semesterId AND a.labsession.Date < :date AND (a.Status = 'Present' OR a.Status = 'Excused' OR a.Status = 'Late')")
    Long countValidAttendancesByStudentIdAndSemesterIdAndDateBefore(@Param("studentId") String studentId, @Param("semesterId") String semesterId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Student_ID = :studentId AND a.Semester_ID = :semesterId AND a.labsession.classGroupID.moduleCode = :moduleId AND a.labsession.Date < :date")
    Long countTotalAttendancesByStudentIdAndSemesterIdAndModuleIdAndDateBefore(@Param("studentId") String studentId, @Param("semesterId") String semesterId, @Param("moduleId") String moduleId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.Student_ID = :studentId AND a.Semester_ID = :semesterId AND a.labsession.classGroupID.moduleCode = :moduleId AND a.labsession.Date < :date AND a.Status IN ('Present', 'Excused', 'Late')")
    Long countValidAttendancesByStudentIdAndSemesterIdAndModuleIdAndDateBefore(@Param("studentId") String studentId, @Param("semesterId") String semesterId, @Param("moduleId") String moduleId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.Semester_ID = :semesterId")
    List<Attendance> findAllBySemesterId(@Param("semesterId") String semesterId);

    @Query("SELECT a FROM Attendance a WHERE a.Semester_ID = :semesterId AND a.labsession.classGroupID.moduleCode = :moduleId")
    List<Attendance> findAllBySemesterIdAndModuleId(@Param("semesterId") String semesterId, @Param("moduleId") String moduleId);
}

