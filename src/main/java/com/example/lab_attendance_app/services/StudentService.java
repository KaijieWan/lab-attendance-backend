package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.models.entities.Student;

import java.util.List;

public interface StudentService {
    public List<Student> getAllStudents();
    public Student createStudent(Student student);
    public List<Student> getStudentsBySemester(String semesterId);
    public List<Student> getStudentsByID(String studentId);
}
