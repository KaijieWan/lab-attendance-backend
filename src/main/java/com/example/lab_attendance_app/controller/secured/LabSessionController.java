package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.*;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Lab;
import com.example.lab_attendance_app.models.entities.LabSession;
import com.example.lab_attendance_app.models.entities.Module;
import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import com.example.lab_attendance_app.models.entities.embedded.LabId;
import com.example.lab_attendance_app.models.repositories.AttendanceRepository;
import com.example.lab_attendance_app.models.repositories.ClassGroupRepository;
import com.example.lab_attendance_app.models.repositories.LabRepository;
import com.example.lab_attendance_app.models.repositories.LabSessionRepository;
import com.example.lab_attendance_app.services.LabSessionService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/labSession")
public class LabSessionController {
    private static final Logger logger = LogManager.getLogger(LabSessionController.class);
    private final LabSessionRepository labSessionRepository;
    private final ClassGroupRepository classGroupRepository;
    private final LabRepository labRepository;
    private final AttendanceRepository attendanceRepository;
    private final LabSessionService labSessionService;

    public LabSessionController(LabSessionRepository labSessionRepository, ClassGroupRepository classGroupRepository, LabRepository labRepository,
                                AttendanceRepository attendanceRepository, LabSessionService labSessionService) {
        this.labSessionRepository = labSessionRepository;
        this.classGroupRepository = classGroupRepository;
        this.labRepository = labRepository;
        this.attendanceRepository = attendanceRepository;
        this.labSessionService = labSessionService;
    }

    /**
     * Retrieves all lab sessions.
     *
     * @return A list of all lab sessions from the repository.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllLabSessions() {
        logger.info("Retrieving all modules:");
        List<LabSession> labSessions = labSessionService.getAllLabSessions();
        if (labSessions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No lab sessions in database",
                            ExecutionStatus.INVALID
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                labSessions
        );
    }

    /**
     * Creates a new lab session.
     *
     * @param createLabSessionDTO The request object containing the details of the new lab session.
     * @return ResponseEntity with a success message if the lab session is created,
     * or an error message if the lab session already exists or validation fails.
     */
    @PostMapping("/createNewLabSession")
    public ResponseEntity<MessageResponse> createNewLabSession(
            @RequestBody CreateLabSessionDTO createLabSessionDTO) {
        logger.info("Lab session creation request is{}", createLabSessionDTO);

        return switch(labSessionService.createLabSession(createLabSessionDTO)){
            case SUCCESS ->
                    ResponseEntity.ok(new MessageResponse(
                            "LabSession created successfully",
                            ExecutionStatus.SUCCESS));
            case FAILED ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                                    "Lab not found, unable to create LabSession",
                            ExecutionStatus.NOT_FOUND));
            case NOT_FOUND ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                            "ClassGroup not found, unable to create LabSession",
                            ExecutionStatus.NOT_FOUND));
            case VALIDATION_ERROR->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(
                            "LabSession already exists, unable to create LabSession",
                            ExecutionStatus.VALIDATION_ERROR));
            case INVALID ->
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(
                            "Invalid time format, unable to create LabSession",
                            ExecutionStatus.INVALID));
            default -> null;
        };
    }

    /**
     * Retrieves a list of distinct modules by semester.
     *
     * @param semester The semester ID.
     * @return ResponseEntity containing a list of distinct module codes for the given semester.
     */
    @GetMapping("/distinctModules")
    public ResponseEntity<?> getDistinctModulesBySemester(@RequestParam String semester) {
        logger.info("Retrieving all distinct modules based on semester:");
        List<String> modules = labSessionService.getDistinctModulesBySemester(semester);

        if (modules.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No modules in database",
                            ExecutionStatus.INVALID
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                modules
        );
    }

    /**
     * Updates lab sessions based on the provided semester, module, and class group, assigning them to a new lab location.
     *
     * @param updateLabSessionDTO The request object containing the updated lab session details.
     * @return ResponseEntity with a success message if the lab sessions are updated,
     * or an error message if no lab sessions or the new lab location is not found.
     */
    @PutMapping("/updateLabSessions")
    public ResponseEntity<MessageResponse> updateLabSessions(@RequestBody UpdateLabSessionDTO updateLabSessionDTO) {
        logger.info("Lab session update request is{}", updateLabSessionDTO);

        return switch(labSessionService.updateLabSessions(updateLabSessionDTO)){
            case SUCCESS ->
                    ResponseEntity.ok(new MessageResponse(
                            "LabSessions updated successfully",
                            ExecutionStatus.SUCCESS));
            case FAILED ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                            "No lab sessions found for the given criteria",
                            ExecutionStatus.NOT_FOUND));
            case NOT_FOUND ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                            "New lab location not found, unable to update LabSessions",
                            ExecutionStatus.NOT_FOUND));
            default -> null;
        };
    }

    /**
     * Deletes a specific lab session and its associated attendance records.
     *
     * @param labSessionID The ID of the lab session to be deleted.
     * @return ResponseEntity with a success message if the lab session is deleted,
     * or a not found message if the lab session is not found.
     */
    @DeleteMapping("/deleteLabSession")
    @Transactional
    public ResponseEntity<MessageResponse> deleteLabSession(@RequestParam String labSessionID) {
        logger.info("Attempting to delete lab session {}", labSessionID);

        return switch(labSessionService.deleteLabSession(labSessionID)){
            case SUCCESS ->
                    ResponseEntity.ok(new MessageResponse(
                            "Lab session and associated attendances deleted successfully",
                            ExecutionStatus.SUCCESS));
            case NOT_FOUND ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                            "LabSession not found, unable to delete",
                            ExecutionStatus.NOT_FOUND));
            default -> null;
        };
    }

    /**
     * Retrieves a specific lab session by its ID.
     *
     * @param labSessionId The ID of the lab session to retrieve.
     * @return ResponseEntity containing the lab session if found, or a not found message if the lab session is not found.
     */
    @GetMapping("/getByLabSessionId")
    public ResponseEntity<?> getLabSessionById(@RequestParam String labSessionId) {
        LabSession labSession = labSessionService.getLabSessionById(labSessionId);
        if(labSession==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                    "Lab session with the particular id cannot be found}",
                    ExecutionStatus.NOT_FOUND));
        }
        else{
            return ResponseEntity.ok(labSession);
        }
    }

    /**
     * Retrieves lab schedules for a specific lab, room, and semester, along with the number of students.
     *
     * @param lab      The name of the lab.
     * @param room     The room number.
     * @param semester The semester ID.
     * @return ResponseEntity containing a list of lab sessions and their student counts, or a not found message if no sessions are found.
     */
    @GetMapping("/getLabSessionsByLabAndRoomAndSemester")
    public ResponseEntity<?> getLabSchedules(@RequestParam String lab, @RequestParam int room, @RequestParam String semester) {
        List<Object[]> labSessions = labSessionService.getLabSchedules(lab, room, semester);
        if (labSessions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                    "No lab sessions with this name, room and semester found",
                    ExecutionStatus.NOT_FOUND
            ));
        } else {
            return ResponseEntity.ok(labSessions);
        }
    }

    /**
     * Retrieves lab sessions for a specific module within a date range, with the remaining capacity for each session.
     *
     * @param makeUpLabSessionDTO The request object containing the date range and module code.
     * @return ResponseEntity containing a list of lab sessions and their remaining capacities.
     */
    @PutMapping("/getLabSessionsByDateRangeAndModuleCode")
    public ResponseEntity<List<LabSessionWithRemainingCapacityDTO>> getLabSessionsByDateRangeAndModuleCode(
            @RequestBody MakeUpLabSessionDTO makeUpLabSessionDTO) {
        return ResponseEntity.ok(labSessionService.getLabSessionsByDateRangeAndModuleCode(makeUpLabSessionDTO));
    }

    /**
     * Retrieves a list of all labs.
     *
     * @return ResponseEntity containing a list of all labs.
     */
    @GetMapping("/allLabs")
    public ResponseEntity<?> getAllLabs() {
        List<Lab> labs = labSessionService.getAllLabs();

        if (labs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                    "No lab sessions found in database",
                    ExecutionStatus.NOT_FOUND
            ));
        } else {
            return ResponseEntity.ok(labs);
        }
    }

    @GetMapping("/specificLabSessions")
    public ResponseEntity<?> getSpecificLabSessions(@RequestParam String classGroupId, @RequestParam String moduleCode, @RequestParam String semesterId) {
        List<LabSession> labSessions = labSessionService.getSpecificLabSessions(classGroupId, moduleCode, semesterId);

        if (labSessions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                    "No lab sessions with the particular parameters found in database",
                    ExecutionStatus.NOT_FOUND
            ));
        } else {
            return ResponseEntity.ok(labSessions);
        }
    }

    /**
     * Updates the capacity of a specific lab.
     *
     * @param labName     The name of the lab.
     * @param room        The room number.
     * @param newCapacity The new capacity to be set.
     * @return ResponseEntity with a success message if the lab capacity is updated, or a not found message if the lab is not found.
     */
    @PutMapping("/updateLabCapacity")
    public ResponseEntity<MessageResponse> updateLabCapacity(@RequestParam String labName, @RequestParam int room, @RequestParam int newCapacity) {
        return switch (labSessionService.updateLabCapacity(labName, room, newCapacity)){
            case NOT_FOUND ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                            "Lab not found, unable to update capacity", ExecutionStatus.NOT_FOUND));
            case SUCCESS ->
                ResponseEntity.ok(new MessageResponse(
                        "Lab capacity updated successfully", ExecutionStatus.SUCCESS));
            default -> null;
        };

    }
}

