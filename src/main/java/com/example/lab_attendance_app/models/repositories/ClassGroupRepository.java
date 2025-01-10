package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, ClassGroupId> {
    @Query("DELETE FROM ClassGroup cg WHERE cg.classGroupId.semesterID = :semesterID")
    @Modifying
    @Transactional
    void deleteBySemesterID(@Param("semesterID") String semesterID);

    @Query("SELECT cg FROM ClassGroup cg WHERE cg.module.ModuleCode = :moduleCode AND cg.classGroupId.semesterID = :semesterId")
    List<ClassGroup> findByModuleCodeAndSemesterId(@Param("moduleCode") String moduleCode, @Param("semesterId") String semesterId);
    
}
