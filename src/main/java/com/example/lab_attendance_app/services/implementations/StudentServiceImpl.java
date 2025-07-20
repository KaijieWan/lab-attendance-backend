package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.models.entities.Student;
import com.example.lab_attendance_app.models.repositories.ModuleRepository;
import com.example.lab_attendance_app.models.repositories.StudentRepository;
import com.example.lab_attendance_app.services.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LogManager.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository, ModuleRepository moduleRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    public Student createStudent(Student student){
        Optional<Student> existingStudent = studentRepository.findById(student.getStudent_ID());

        if (existingStudent.isPresent()) {
            return null;
        }

        return studentRepository.save(student);
    }

    public List<Student> getStudentsBySemester(String semesterId){
        return studentRepository.findStudentsBySemester(semesterId);
    }

    public List<Student> getStudentsByID(String studentId){
        return studentRepository.searchByStudentId(studentId);
    }

}
