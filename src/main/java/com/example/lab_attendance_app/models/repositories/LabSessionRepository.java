package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.LabSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LabSessionRepository extends JpaRepository<LabSession, String> {
    @Query("SELECT DISTINCT ls.classGroupID.moduleCode FROM LabSession ls WHERE UPPER(ls.classGroupID.semesterID) = UPPER(:semesterId)")
    List<String> findDistinctModulesBySemester(@Param("semesterId") String semesterId);

    @Query("SELECT ls FROM LabSession ls WHERE ls.classGroupID.semesterID = :semesterID AND ls.classGroupID.moduleCode = :moduleCode AND ls.classGroupID.classGroupID = :classGroupID")
    List<LabSession> findLabSessionsBySemesterModuleAndClassGroup(
            @Param("semesterID") String semesterID,
            @Param("moduleCode") String moduleCode,
            @Param("classGroupID") String classGroupID
    );

    @Query("SELECT ls FROM LabSession ls WHERE ls.LabSessionID = :labSessionId")
    Optional<LabSession> findByLabSessionID(@Param("labSessionId") String labSessionId);

    @Query("SELECT ls, COUNT(a) AS studentCount " +
            "FROM LabSession ls LEFT JOIN Attendance a ON ls.LabSessionID = a.Lab_SessionID " +
            "WHERE ls.labID.labName = :labName AND ls.labID.room = :room AND ls.classGroupID.semesterID = :semesterID " +
            "GROUP BY ls")
    List<Object[]> findLabSessionsByLabNameRoomAndSemesterWithStudentCount(
            @Param("labName") String labName,
            @Param("room") int room,
            @Param("semesterID") String semesterID
    );

    @Query("SELECT ls FROM LabSession ls WHERE ls.Date BETWEEN :startDate AND :endDate AND ls.classGroupID.moduleCode = :moduleCode ORDER BY ls.Date ASC, ls.StartTime ASC, ls.labID.labName ASC, ls.labID.room ASC")
    List<LabSession> findByDateRangeAndModuleCode(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("moduleCode") String moduleCode);

    @Query("DELETE FROM LabSession ls WHERE ls.classGroup.classGroupId.semesterID = :semesterID")
    @Modifying
    @Transactional
    void deleteBySemesterID(@Param("semesterID") String semesterID);


    @Query("SELECT DISTINCT ls.classGroupID.moduleCode FROM LabSession ls ORDER BY ls.classGroupID.moduleCode")
    List<String> findModuleCodes();
}
