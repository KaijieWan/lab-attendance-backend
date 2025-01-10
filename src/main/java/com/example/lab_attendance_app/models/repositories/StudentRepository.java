package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    @Query("SELECT DISTINCT s FROM Student s JOIN Student_Enrolled_ClassGroup cgs ON s.Student_ID = cgs.id.student_Id WHERE cgs.id.semester_ID = :semesterId")
    List<Student> findStudentsBySemester(@Param("semesterId") String semesterId);

    @Query("SELECT COUNT(DISTINCT se.student.Student_ID) FROM Student_Enrolled_ClassGroup se WHERE se.classGroup.classGroupId.semesterID = :semesterId")
    Integer countStudentsBySemesterId(@Param("semesterId") String semesterId);

    @Query("SELECT COUNT(DISTINCT se.student.Student_ID) FROM Student_Enrolled_ClassGroup se WHERE se.classGroup.classGroupId.semesterID = :semesterId AND se.classGroup.classGroupId.moduleCode = :moduleId")
    Integer countStudentsBySemesterIdAndModule(@Param("semesterId") String semesterId, @Param("moduleId") String moduleId);

    @Query("SELECT DISTINCT s FROM Student s JOIN Student_Enrolled_ClassGroup cgs ON s.Student_ID = cgs.id.student_Id WHERE cgs.id.semester_ID = :semesterId")
    List<Student> findAllBySemesterId(@Param("semesterId") String semesterId);

    @Query("SELECT DISTINCT s FROM Student s JOIN Student_Enrolled_ClassGroup cgs ON s.Student_ID = cgs.id.student_Id WHERE cgs.id.semester_ID = :semesterId AND cgs.id.moduleCode = :moduleId")
    List<Student> findAllBySemesterIdAndModuleId(@Param("semesterId") String semesterId, @Param("moduleId") String moduleId);

    @Query("SELECT s FROM Student s WHERE LOWER(s.Student_ID) LIKE LOWER(CONCAT('%', :studentId, '%'))")
    List<Student> searchByStudentId(@Param("studentId") String studentId);
}
