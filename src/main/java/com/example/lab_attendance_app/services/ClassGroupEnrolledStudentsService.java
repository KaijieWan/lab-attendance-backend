package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Student;
import com.example.lab_attendance_app.models.entities.Student_Enrolled_ClassGroup;

import java.util.List;
import java.util.Map;

public interface ClassGroupEnrolledStudentsService {
    public ExecutionStatus createNewClassGroupEnrolledStudents(Student_Enrolled_ClassGroup classGroupEnrolledStudents);
    public List<Student_Enrolled_ClassGroup> getAllClassGroupEnrolledStudents();
    public Map<String, List<Student>> getStudentsByModuleAndSemester(String moduleCode, String semesterId);
}
