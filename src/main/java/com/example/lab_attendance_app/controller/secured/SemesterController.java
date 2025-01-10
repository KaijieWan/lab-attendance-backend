package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.MessageResponse;
import com.example.lab_attendance_app.models.dto.SemesterDTO;
import com.example.lab_attendance_app.models.entities.Semester;
import com.example.lab_attendance_app.models.repositories.*;
import com.example.lab_attendance_app.services.SemesterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/semester")
public class SemesterController {
    private static final Logger logger = LogManager.getLogger(SemesterController.class);
    private final SemesterRepository semesterRepository;
    private final ClassGroupRepository classGroupRepository;
    private final ClassGroupEnrolledStudentsRepository classGroupStudentRepository;
    private final LabSessionRepository labSessionRepository;
    private final AttendanceRepository attendanceRepository;
    private final AbsentDetailsRepository absentDetailsRepository;
    private final AbsentLogsRepository absentLogsRepository;

    private final SemesterService semesterService;

    public SemesterController(SemesterRepository semesterRepository,
                              ClassGroupRepository classGroupRepository,
                              ClassGroupEnrolledStudentsRepository classGroupStudentRepository,
                              LabSessionRepository labSessionRepository,
                              AttendanceRepository attendanceRepository,
                              AbsentDetailsRepository absentDetailsRepository, AbsentLogsRepository absentLogsRepository,
                              SemesterService semesterService) {
        this.semesterRepository = semesterRepository;
        this.classGroupRepository = classGroupRepository;
        this.classGroupStudentRepository = classGroupStudentRepository;
        this.labSessionRepository = labSessionRepository;
        this.attendanceRepository = attendanceRepository;
        this.absentDetailsRepository = absentDetailsRepository;
        this.absentLogsRepository = absentLogsRepository;
        this.semesterService = semesterService;
    }

    /**
     * Retrieves a list of all semesters.
     *
     * @return A list of all semesters from the repository.
     */
    @GetMapping("")
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    /**
     * Creates a new semester if it does not already exist.
     *
     * @param semesterDTO The semester object to be created.
     * @return ResponseEntity with a success message if the semester is created,
     * or a conflict message if the semester already exists.
     */
    @PostMapping("/createNewSemester")
    public ResponseEntity<MessageResponse> createNewSemester(
            @RequestBody SemesterDTO semesterDTO) {
        logger.info(semesterDTO);

        return switch (semesterService.createSemester(semesterDTO.toEntity())){
            case VALIDATION_ERROR ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(
                            "Semester already exists, unable to create semester", ExecutionStatus.VALIDATION_ERROR));
            case SUCCESS ->
                    ResponseEntity.ok(new MessageResponse(
                            "Semester created successfully", ExecutionStatus.SUCCESS));
            default -> null;
        };
    }

    /**
     * Retrieves a semester by its ID.
     *
     * @param semesterId The ID of the semester to retrieve.
     * @return ResponseEntity containing the semester if found,
     * or a not found status if the semester is not found.
     */
    @GetMapping("/getSemesterByID")
    public ResponseEntity<Semester> getSemesterById(@RequestParam String semesterId) {
        Optional<Semester> semesterOptional = semesterRepository.findById(semesterId);
        if (semesterOptional.isPresent()) {
            return ResponseEntity.ok(semesterOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes a semester and all associated records from related tables.
     *
     * @param semesterID The ID of the semester to delete.
     * @return ResponseEntity with a success message if the semester is deleted,
     * or a not found message if the semester does not exist.
     */
    @DeleteMapping("/deleteSemester")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteSemester(@RequestParam String semesterID) {
        Optional<Semester> semesterOptional = semesterRepository.findById(semesterID);
        if (semesterOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Semester not found"));
        }

        // Delete records from all related tables
        absentLogsRepository.deleteBySemesterID(semesterID);
        attendanceRepository.deleteAttendancesWithAbsencesBySemesterID(semesterID);
        absentDetailsRepository.deleteMakeUpAbsencesBySemesterID(semesterID);
        absentDetailsRepository.deleteNormalAbsencesBySemesterID(semesterID);
        attendanceRepository.deleteNormalAttendancesBySemesterID(semesterID);
        labSessionRepository.deleteBySemesterID(semesterID);
        classGroupStudentRepository.deleteBySemesterID(semesterID);
        classGroupRepository.deleteBySemesterID(semesterID);
        semesterRepository.deleteBySemesterID(semesterID);
        absentDetailsRepository.deleteAbsencesWithNoParentAttendanceBySemesterID();

        return ResponseEntity.ok(Map.of("message", "Semester and associated records deleted successfully"));
    }
}
