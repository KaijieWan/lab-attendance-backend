package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.ClassGroupDTO;
import com.example.lab_attendance_app.models.dto.MessageResponse;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Module;
import com.example.lab_attendance_app.models.repositories.*;
import com.example.lab_attendance_app.services.ClassGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/classGroup")
public class ClassGroupController {
    private static final Logger logger = LogManager.getLogger(ClassGroupController.class);
    private final ClassGroupRepository classGroupRepository;
    private final ModuleRepository moduleRepository;
    private final AbsentDetailsRepository absentRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final AbsentLogsRepository absentLogsRepository;
    private final LabSessionRepository labSessionRepository;
    private final SemesterRepository semesterRepository;
    private final ClassGroupEnrolledStudentsRepository classGroupStudentRepository;

    private final ClassGroupService classGroupService;

    public ClassGroupController(ClassGroupRepository classGroupRepository, ModuleRepository moduleRepository, AbsentDetailsRepository absentRepository, AttendanceRepository attendanceRepository, StudentRepository studentRepository, AbsentLogsRepository absentLogsRepository, LabSessionRepository labSessionRepository, SemesterRepository semesterRepository, ClassGroupEnrolledStudentsRepository classGroupStudentRepository, ClassGroupService classGroupService) {
        this.classGroupRepository = classGroupRepository;
        this.moduleRepository = moduleRepository;
        this.absentRepository = absentRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.absentLogsRepository = absentLogsRepository;
        this.labSessionRepository = labSessionRepository;
        this.semesterRepository = semesterRepository;
        this.classGroupStudentRepository = classGroupStudentRepository;
        this.classGroupService = classGroupService;
    }

    /**
     * Retrieves a list of all class groups.
     *
     * @return A list of all class groups from the repository.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllClassGroups() {
        logger.info("Retrieving all ClassGroups:");

        List<ClassGroup> classGroups = classGroupService.getAllClassGroups();

        if (classGroups.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No modules in database",
                            ExecutionStatus.INVALID
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                classGroups
        );
    }

    /**
     * Creates a new class group if it does not already exist and associates it with a module.
     *
     * @param classGroupDTO The class group object to be created.
     * @return ResponseEntity containing a success message if the class group is created,
     * or an error message if the class group already exists or the module is not found.
     */
    @PostMapping("/createNewClassGroup")
    public ResponseEntity<MessageResponse> createNewClassGroup(@RequestBody ClassGroupDTO classGroupDTO) {
        System.out.println(classGroupDTO);
        log.info("Received ClassGroupDTO: {}", classGroupDTO);
        if (classGroupDTO.getClassGroupId() == null) {
            throw new IllegalArgumentException("classGroupId must not be null");
        }

        switch(classGroupService.createClassGroup(classGroupDTO.toEntity())){
            case SUCCESS -> {
                return ResponseEntity.ok(new MessageResponse(
                        "ClassGroup created successfully",
                        ExecutionStatus.SUCCESS));
            }
            case VALIDATION_ERROR -> {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(
                        "ClassGroup already exists",
                        ExecutionStatus.VALIDATION_ERROR));
            }
            case NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                        "Module not found",
                        ExecutionStatus.NOT_FOUND));
            }
            default -> {
                return ResponseEntity.badRequest().body(new MessageResponse(
                        "ERROR",
                        ExecutionStatus.FAILED));
            }
        }
    }

    /**
     * Delete student from class group.
     *
     * @param studentId
     * @param moduleCode
     * @param classGroupId
     * @param semesterId
     * @return ResponseEntity containing a success message if the student is removed from the class group,
     */
    /*@DeleteMapping("/deleteStudentFromClassGroup")
    @Transactional
    public ResponseEntity<String> deleteStudentFromClassGroup(@RequestParam String studentId, @RequestParam String moduleCode, @RequestParam String classGroupId, @RequestParam String semesterId) {
        // Check if the student exists
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        // Create a ClassGroupId object
        ClassGroupId classGroupIdObj = new ClassGroupId(classGroupId, moduleCode, semesterId);

        // Check if the class group exists
        Optional<ClassGroup> classGroupOptional = classGroupRepository.findById(classGroupIdObj);
        if (classGroupOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ClassGroup not found");
        }

        // Retrieve lab sessions of the class group
        List<LabSession> labSessionsInClassGroup = labSessionRepository.findLabSessionsBySemesterModuleAndClassGroup(semesterId, moduleCode, classGroupId);

        // Delete all absent logs of the student in the lab sessions
        for (LabSession labSession : labSessionsInClassGroup) {
            absentLogsRepository.deleteStudentAbsentLogsByLabSessionIDAndStudentIDAndSemesterID(semesterId, studentId, labSession.getLabSessionID());
        }

        // Delete all attendances with absences of the student in the lab sessions
        for (LabSession labSession : labSessionsInClassGroup) {
            attendanceRepository.deleteAttendancesWithAbsencesBySemesterIDAndStudentIDAndLabSessionID(semesterId, studentId, labSession.getLabSessionID());
        }

        // Delete all make up absent details of the student in the lab sessions
        for (LabSession labSession : labSessionsInClassGroup) {
            absentRepository.deleteMakeUpAbsencesBySemesterIDAndStudentIDAndLabSessionID(semesterId, studentId, labSession.getLabSessionID());
        }

        // Delete all make up normal details of the student in the lab sessions
        for (LabSession labSession : labSessionsInClassGroup) {
            absentRepository.deleteNormalAbsencesBySemesterIDAndStudentIDAndLabSessionID(semesterId, studentId, labSession.getLabSessionID());
        }

        // Delete all attendances of the student in the lab sessions
        for (LabSession labSession : labSessionsInClassGroup) {
            attendanceRepository.deleteNormalAttendancesBySemesterIDAndStudentIDAndLabSessionID(semesterId, studentId, labSession.getLabSessionID());
        }


        // Delete the student from the class group
        classGroupStudentRepository.deleteByStudentIDAndModuleCodeAndClassGroupIDAndSemesterID(studentId, moduleCode, classGroupId, semesterId);

        return ResponseEntity.ok("Student successfully removed from class group");
    }*/

    /*@Transactional
    @PostMapping("/addStudentToClassGroup")
    public ResponseEntity<String> addStudentToClassGroup(@RequestParam String studentId, @RequestParam String moduleCode, @RequestParam String classGroupId, @RequestParam String semesterId) {
        // Check if the student exists
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        // Check if semester exists
        Optional<Semester> semesterOptional = semesterRepository.findById(semesterId);
        if (semesterOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Semester not found");
        }

        ClassGroupId classGroupIdObj = new ClassGroupId(classGroupId, moduleCode, semesterId);

        // Check if the class group exists
        Optional<ClassGroup> classGroupOptional = classGroupRepository.findById(classGroupIdObj);
        if (classGroupOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ClassGroup not found");
        }

        StudentEnrolledClassGroupId studentEnrolledClassGroupId = new StudentEnrolledClassGroupId(studentId, classGroupId, moduleCode, semesterId);

        Optional<Student_Enrolled_ClassGroup> existingClassGroupEnrolledStudents = classGroupStudentRepository.findById(studentEnrolledClassGroupId);

        if (existingClassGroupEnrolledStudents.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ClassGroupEnrolledStudents already exists");
        }

        Student_Enrolled_ClassGroup classGroupEnrolledStudents = new Student_Enrolled_ClassGroup();
        classGroupEnrolledStudents.setId(studentEnrolledClassGroupId);
        classGroupEnrolledStudents.setStudent(studentOptional.get());

        // Retrieve and set the ClassGroup entity
        classGroupEnrolledStudents.setClassGroup(classGroupOptional.get());

        // Save the new Student_Enrolled_ClassGroup
        classGroupStudentRepository.save(classGroupEnrolledStudents);

        // Retrieve lab sessions of the class group
        List<LabSession> labSessionsInClassGroup = labSessionRepository.findLabSessionsBySemesterModuleAndClassGroup(semesterId, moduleCode, classGroupId);

        // Add the student to the lab sessions
        for (LabSession labSession : labSessionsInClassGroup) {
            // Create new attendance for the student in the lab session
            Optional<Attendance> existingAttendance = attendanceRepository.findByLabSessionIdAndStudentId(labSession.getLabSessionID(), studentId);

            if (existingAttendance.isPresent()) {
                continue;
            }

            // Create a new Attendance object and set the attributes
            Attendance attendance = new Attendance();
            attendance.setLab_SessionID(labSession.getLabSessionID());
            attendance.setStudent_ID(studentId);

            attendance.setStatus("Pending");
            attendance.setIsMakeUpSession(Boolean.FALSE);
            attendance.setSemester_ID(semesterId);
            attendance.setRemarks("");
            attendance.setStudent(studentOptional.get());
            attendance.setLabsession(labSession);
            attendance.setSemester(semesterOptional.get());

            attendanceRepository.save(attendance);

        }

        return ResponseEntity.ok("Student successfully added to class group");
    }*/
}
