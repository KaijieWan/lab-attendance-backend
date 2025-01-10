package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.MessageResponse;
import com.example.lab_attendance_app.models.dto.StudentDTO;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Student;
import com.example.lab_attendance_app.models.repositories.StudentRepository;
import com.example.lab_attendance_app.services.StudentService;
import com.example.lab_attendance_app.services.implementations.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/student")
public class StudentController {
    private static final Logger logger = LogManager.getLogger(StudentController.class);
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public StudentController(StudentRepository studentRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    /**
     * Retrieves a list of all students.
     *
     * @return A list of all students from the repository.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllStudents() {
        logger.info("Retrieving all Students:");

        List<Student> students = studentService.getAllStudents();

        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No students in database",
                            ExecutionStatus.INVALID
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                students
        );
    }

    /**
     * Creates a new student if they do not already exist.
     *
     * @param studentDTO The student object to be created.
     * @return ResponseEntity with a success message if the student is created,
     * or a conflict message if the student already exists.
     */
    @PostMapping("/createNewStudent")
    public ResponseEntity<MessageResponse> createNewStudent(
            @RequestBody StudentDTO studentDTO) {
        logger.info(studentDTO);
        logger.info(studentDTO.getStudentID());
        logger.info(studentDTO.toEntity());

        if(studentService.createStudent(studentDTO.toEntity())==null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(
                    "Student already exists",
                    ExecutionStatus.VALIDATION_ERROR));
        }

        return ResponseEntity.ok(new MessageResponse(
                "Student created successfully",
                ExecutionStatus.SUCCESS));
    }

    /**
     * Retrieves a list of students enrolled in a specific semester.
     *
     * @param semesterId The ID of the semester.
     * @return ResponseEntity containing a list of students for the given semester,
     * or a not found status if no students are found.
     */
    @GetMapping("/getStudentsbySemester")
    public ResponseEntity<?> getStudentsBySemester(@RequestParam String semesterId) {
        List<Student> students = studentService.getStudentsBySemester(semesterId);

        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                    "No students from this semester in database",
                    ExecutionStatus.INVALID
            ));
        } else {
            return ResponseEntity.ok(students);
        }
    }

    @GetMapping("/searchByStudentID")
    public ResponseEntity<?> searchStudents(@RequestParam String studentId) {
        // Note ID can be partially completed
        List<Student> students = studentService.getStudentsByID(studentId);

        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                    "No students with this id in database",
                    ExecutionStatus.INVALID
            ));
        } else {
            return ResponseEntity.ok(students);
        }
    }
}
