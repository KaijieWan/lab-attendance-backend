package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.AdhocSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdhocSessionRepository extends JpaRepository<AdhocSession, String> {
    @Query("SELECT DISTINCT ls.classGroupID.moduleCode FROM AdhocSession ls WHERE UPPER(ls.classGroupID.semesterID) = UPPER(:semesterId)")
    List<String> findDistinctModulesBySemester(@Param("semesterId") String semesterId);

    @Query("SELECT ls FROM AdhocSession ls WHERE ls.classGroupID.semesterID = :semesterID AND ls.classGroupID.moduleCode = :moduleCode AND ls.classGroupID.classGroupID = :classGroupID AND ls.sessionContent = :sessionContent")
    List<AdhocSession> findAdhocSessionsBySemesterModuleAndClassGroupAndContent(
            @Param("semesterID") String semesterID,
            @Param("moduleCode") String moduleCode,
            @Param("classGroupID") String classGroupID,
            @Param("sessionContent") String sessionContent
    );

    @Query("SELECT ls FROM AdhocSession ls WHERE ls.AdhocSessionID = :adhocSessionId")
    Optional<AdhocSession> findByAdhocSessionID(@Param("adhocSessionId") String adhocSessionId);

    @Query("SELECT ls, COUNT(a) AS studentCount " +
            "FROM AdhocSession ls LEFT JOIN Attendance a ON ls.AdhocSessionID = a.Lab_SessionID " +
            "WHERE ls.labID.labName = :labName AND ls.labID.room = :room AND ls.classGroupID.semesterID = :semesterID " +
            "GROUP BY ls")
    List<Object[]> findAdhocSessionsByLabNameRoomAndSemesterWithStudentCount(
            @Param("labName") String labName,
            @Param("room") int room,
                @Param("semesterID") String semesterID
    );

    @Query("SELECT ls FROM AdhocSession ls WHERE ls.Date BETWEEN :startDate AND :endDate AND ls.classGroupID.moduleCode = :moduleCode ORDER BY ls.Date ASC, ls.StartTime ASC, ls.labID.labName ASC, ls.labID.room ASC")
    List<AdhocSession> findByDateRangeAndModuleCode(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("moduleCode") String moduleCode);

    @Query("DELETE FROM AdhocSession ls WHERE ls.classGroup.classGroupId.semesterID = :semesterID")
    @Modifying
    @Transactional
    void deleteBySemesterID(@Param("semesterID") String semesterID);


    @Query("SELECT DISTINCT ls.classGroupID.moduleCode FROM AdhocSession ls ORDER BY ls.classGroupID.moduleCode")
    List<String> findModuleCodes();
}
