package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Student;
import com.example.lab_attendance_app.models.entities.Student_Enrolled_ClassGroup;
import com.example.lab_attendance_app.models.entities.embedded.StudentEnrolledClassGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClassGroupEnrolledStudentsRepository extends JpaRepository<Student_Enrolled_ClassGroup, StudentEnrolledClassGroupId> {
    @Query("SELECT s.student FROM Student_Enrolled_ClassGroup s WHERE s.id.classGroupId = :classGroupId AND s.id.moduleCode = :moduleCode AND s.id.semester_ID = :semesterId")
    List<Student> findStudentsByClassGroup(@Param("classGroupId") String classGroupId, @Param("moduleCode") String moduleCode, @Param("semesterId") String semesterId);

    @Query("DELETE FROM Student_Enrolled_ClassGroup secg WHERE secg.id.semester_ID = :semesterID")
    @Modifying
    @Transactional
    void deleteBySemesterID(@Param("semesterID") String semesterID);

    @Query("DELETE FROM Student_Enrolled_ClassGroup secg WHERE secg.student.Student_ID = :studentId AND secg.id.moduleCode = :moduleCode AND secg.id.classGroupId = :classGroupId AND secg.id.semester_ID = :semesterId")
    @Modifying
    @Transactional
    void deleteByStudentIDAndModuleCodeAndClassGroupIDAndSemesterID(String studentId, String moduleCode, String classGroupId, String semesterId);
}
