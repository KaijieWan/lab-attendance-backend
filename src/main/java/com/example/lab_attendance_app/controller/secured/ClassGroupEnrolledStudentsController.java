package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.ClassGroupEnrolledStudentsDTO;
import com.example.lab_attendance_app.models.dto.MessageResponse;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Module;
import com.example.lab_attendance_app.models.entities.Student;
import com.example.lab_attendance_app.models.entities.Student_Enrolled_ClassGroup;
import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import com.example.lab_attendance_app.models.repositories.ClassGroupEnrolledStudentsRepository;
import com.example.lab_attendance_app.models.repositories.ClassGroupRepository;
import com.example.lab_attendance_app.models.repositories.StudentRepository;
import com.example.lab_attendance_app.services.ClassGroupEnrolledStudentsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/classGroupEnrolledStudents")
public class ClassGroupEnrolledStudentsController {
    private static final Logger logger = LogManager.getLogger(ClassGroupEnrolledStudentsController.class);
    private final ClassGroupEnrolledStudentsRepository classGroupEnrolledStudentsRepository;
    private final ClassGroupEnrolledStudentsService classGroupEnrolledStudentsService;
    private final ClassGroupRepository classGroupRepository;
    private final StudentRepository studentRepository;

    public ClassGroupEnrolledStudentsController(
            ClassGroupEnrolledStudentsRepository classGroupEnrolledStudentsRepository,
            ClassGroupEnrolledStudentsService classGroupEnrolledStudentsService,
            ClassGroupRepository classGroupRepository,
            StudentRepository studentRepository) {
        this.classGroupEnrolledStudentsRepository = classGroupEnrolledStudentsRepository;
        this.classGroupEnrolledStudentsService = classGroupEnrolledStudentsService;
        this.classGroupRepository = classGroupRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieves all records of students enrolled in class groups.
     *
     * @return A list of all students enrolled in class groups from the repository.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllClassGroupEnrolledStudents() {


        List<Student_Enrolled_ClassGroup> studentEnrolledClassGroups = classGroupEnrolledStudentsService.getAllClassGroupEnrolledStudents();

        if (studentEnrolledClassGroups.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No class groups with students enrolled in database",
                            ExecutionStatus.INVALID
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                studentEnrolledClassGroups
        );
    }

    /**
     * Creates a new record of students enrolled in a class group.
     *
     * @param classGroupEnrolledStudentsDTO The student-class group enrollment details to be created.
     * @return ResponseEntity with a success message if the record is created,
     * or a conflict message if the record already exists,
     * or a not found message if the student or class group is not found.
     */
    @PostMapping("/createNewClassGroupEnrolledStudents")
    public ResponseEntity<?> createNewClassGroupEnrolledStudents(
            @RequestBody ClassGroupEnrolledStudentsDTO classGroupEnrolledStudentsDTO) {
        logger.info(classGroupEnrolledStudentsDTO);

        return switch(classGroupEnrolledStudentsService.createNewClassGroupEnrolledStudents(classGroupEnrolledStudentsDTO.toEntity())){
            case SUCCESS ->
                ResponseEntity.ok(
                        new MessageResponse(
                                "ClassGroupEnrolledStudents created successfully",
                                ExecutionStatus.SUCCESS));
            case VALIDATION_ERROR ->
                ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new MessageResponse(
                                "ClassGroupEnrolledStudents already exists",
                                ExecutionStatus.VALIDATION_ERROR));
            case NOT_FOUND ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new MessageResponse(
                                "Student not found",
                                ExecutionStatus.VALIDATION_ERROR));
            case INVALID ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                        "ClassGroup not found",
                        ExecutionStatus.VALIDATION_ERROR));
            default ->
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        };
    }

    /**
     * Retrieves a list of students enrolled in class groups for a specific module and semester.
     *
     * @param moduleCode The code of the module.
     * @param semesterId The ID of the semester.
     * @return ResponseEntity containing a map where the key is the class group ID and the value is a list of students,
     * or a not found message if the class groups or students are not found.
     */
    @GetMapping("/getStudentsByModuleAndSemester")
    public ResponseEntity<?> getStudentsByModuleAndSemester(
            @RequestParam String moduleCode,
            @RequestParam String semesterId) {

        Map<String, List<Student>> classGroupToStudentsMap = classGroupEnrolledStudentsService.getStudentsByModuleAndSemester(moduleCode, semesterId);

        if(classGroupToStudentsMap.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No class groups with students under this module/semester in database",
                            ExecutionStatus.INVALID
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                classGroupToStudentsMap
        );

    }


}
