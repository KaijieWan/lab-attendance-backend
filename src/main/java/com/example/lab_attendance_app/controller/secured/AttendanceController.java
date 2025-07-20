package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.*;
import com.example.lab_attendance_app.models.entities.*;
import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import com.example.lab_attendance_app.models.entities.embedded.LabId;
import com.example.lab_attendance_app.models.entities.embedded.StudentEnrolledClassGroupId;
import com.example.lab_attendance_app.models.repositories.*;
import com.example.lab_attendance_app.services.AttendanceService;
import com.example.lab_attendance_app.services.implementations.AttendanceServiceImpl;
import com.example.lab_attendance_app.util.Utility;
//import com.ntu.labattendance.service.AbsentService;
//import com.ntu.labattendance.service.EmailService;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.Module;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    private static final Logger logger = LogManager.getLogger(AttendanceController.class);
    private final AttendanceRepository attendanceRepository;
    private final AbsentDetailsRepository absentDetailsRepository;

    private final AbsentLogsRepository absentLogsRepository;

    private final StudentRepository studentRepository;

    private final LabSessionRepository labSessionRepository;

    private final LabRepository labRepository;

    private final ClassGroupRepository classGroupRepository;

    private final ModuleRepository moduleRepository;

    private final ClassGroupEnrolledStudentsRepository classGroupEnrolledStudentsRepository;

    //private final EmailService emailService;

    private final UserRepository userRepository;

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceRepository attendanceRepository, AbsentDetailsRepository absentDetailsRepository, AbsentLogsRepository absentLogsRepository, StudentRepository studentRepository, AttendanceService attendanceService,
                                LabSessionRepository labSessionRepository, LabRepository labRepository, ClassGroupRepository classGroupRepository, ModuleRepository moduleRepository, ClassGroupEnrolledStudentsRepository classGroupEnrolledStudentsRepository, /*EmailService emailService,*/ UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.absentDetailsRepository = absentDetailsRepository;
        this.absentLogsRepository = absentLogsRepository;
        this.studentRepository = studentRepository;
        this.labSessionRepository = labSessionRepository;
        this.labRepository = labRepository;
        this.classGroupRepository = classGroupRepository;
        this.moduleRepository = moduleRepository;
        this.classGroupEnrolledStudentsRepository = classGroupEnrolledStudentsRepository;
        //this.emailService = emailService;
        this.userRepository = userRepository;
        this.attendanceService = attendanceService;
    }

    /**
     * Retrieves a list of all attendance records.
     *
     * @return A list of all attendance records from the database.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllAttendances() {
        logger.info("Retrieving all attendances:");
        List<Attendance> attendances = attendanceService.getAllAttendances();
        if (attendances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No attendances found in database",
                            ExecutionStatus.NOT_FOUND
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                attendances
        );
    }

    /**
     * Creates a new attendance record for a student in a lab session.
     *
     * @param createAttendanceDTO The details of the new attendance to be created.
     * @return ResponseEntity with a success message if the attendance is created,
     * or a conflict message if an attendance record or absence already exists.
     */
    @SuppressWarnings("ExtractMethodRecommender")
    @PostMapping("/createNewAttendance")
    public ResponseEntity<MessageResponse> createNewAttendance(@RequestBody CreateAttendanceDTO createAttendanceDTO) {
        logger.info(createAttendanceDTO);

        return switch(attendanceService.createAttendance(createAttendanceDTO)){
            case SUCCESS ->
                    ResponseEntity.ok(new MessageResponse(
                            "Attendance created successfully", ExecutionStatus.SUCCESS));
            case INVALID ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(
                            "Attendance already exists", ExecutionStatus.VALIDATION_ERROR));
            case VALIDATION_ERROR ->
                    ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(
                            "Absent details already exists", ExecutionStatus.VALIDATION_ERROR));
            default -> null;
        };
    }

    /**
     * Retrieves attendance records for a specific module and semester, grouped by class group and date.
     *
     * @param moduleCode The code of the module.
     * @param semesterId The ID of the semester.
     * @return ResponseEntity containing a list of attendance records for the specified module and semester.
     */
    @GetMapping("/getAttendanceByModuleAndSemesterGroupByClassGroupAndDate")
    public ResponseEntity<?> getAttendanceByModuleAndSemesterGroupByClassGroupAndDate(@RequestParam String moduleCode, @RequestParam String semesterId) {
        List<Attendance> attendances = attendanceService.getAttendanceByModuleAndSemesterGroupByClassGroupAndDate(moduleCode, semesterId);

        if (attendances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No attendances with the module and semester found in database",
                            ExecutionStatus.NOT_FOUND
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                attendances
        );
    }

    /**
     * Retrieves all attendance records for a specific lab session.
     *
     * @param labSessionId The ID of the lab session.
     * @return ResponseEntity containing a list of attendance records for the lab session,
     * or a 404 status if no records are found.
     */
    @GetMapping("/getAttendanceByLabSessionId")
    public ResponseEntity<?> getAllAttendancesByLabSessionId(@RequestParam String labSessionId) {
        List<Attendance> attendances = attendanceService.getAllAttendancesByLabSessionId(labSessionId);
        if (attendances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No attendances with the labSessionId found in database",
                            ExecutionStatus.NOT_FOUND
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                attendances
        );
    }

    /**
     * Updates the remarks of a specific attendance record.
     *
     * @param updateRemarksDTO The details of the attendance record and the new remarks.
     * @return ResponseEntity with a success message if the remarks are updated,
     * or a 404 status if the attendance record is not found.
     */
    @PutMapping("/updateRemarks")
    public ResponseEntity<MessageResponse> updateRemarks(@RequestBody UpdateRemarksDTO updateRemarksDTO) {
        switch(attendanceService.updateAttendanceRemarks(updateRemarksDTO)){
            case SUCCESS -> {
                return ResponseEntity.ok
                        (new MessageResponse
                                ("Remarks updated successfully"
                                        , ExecutionStatus.SUCCESS));
            }
            case NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                        "Attendance ID not found, unable to update remarks", ExecutionStatus.NOT_FOUND));
            }
            default -> {return null;}
        }
    }

    /**
     * Retrieves all attendance records for a student in a specific semester.
     *
     * @param studentID  The ID of the student.
     * @param semesterID The ID of the semester.
     * @return ResponseEntity containing a list of attendance records for the student and semester,
     * or a 404 status if no records are found.
     */
    @GetMapping("/getAttendanceByStudentIdAndSemester")
    public ResponseEntity<List<Attendance>> getAttendanceByStudentIdAndSemester(@RequestParam String studentID, @RequestParam String semesterID) {
        List<Attendance> attendances = attendanceRepository.findAttendancesByStudentIdAndSemester(studentID, semesterID);
        if (attendances.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(attendances);
        }
    }

    /**
     * Retrieves all absence records for a specific semester.
     *
     * @param semesterID The ID of the semester.
     * @return ResponseEntity containing a list of absence records for the semester.
     */
    @GetMapping("/fetchAllAbsencesBySemester")
    public ResponseEntity<List<Attendance>> getAllAbsencesBySemester(@RequestParam String semesterID) {
        List<Attendance> absences = attendanceRepository.findAllAbsencesBySemester(semesterID);
        return ResponseEntity.ok(absences);
    }

    /**
     * Creates a new absence record linked to a specific attendance.
     *
     * @param createAbsenceRequest The details of the absence to be created.
     * @return ResponseEntity with a success message if the absence is created,
     * or a 404 status if the attendance record is not found.
     */
    /*@Transactional
    @PostMapping("/createAbsence")
    public ResponseEntity<Map<String, String>> createAbsence(@RequestBody CreateAbsenceRequest createAbsenceRequest) {

        Optional<Attendance> attendanceOptional = attendanceRepository.findById(createAbsenceRequest.getAttendanceID());
        if (attendanceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Attendance not found, unable to create absence"));
        }

        Attendance attendance = attendanceOptional.get();

        Absent_Details newAbsence = Absent_Details.builder().Status("Pending").build();

        absentDetailsRepository.save(newAbsence);

        attendance.setAbsent_ID(newAbsence.getAbsent_ID());
        attendanceRepository.save(attendance);

        Absent_Logs absentLog = new Absent_Logs(newAbsence.getAbsent_ID(), "System", "Created Absence Record For Student", "System Generated");
        absentLogsRepository.save(absentLog);

        return ResponseEntity.ok(Map.of("message", "Absence created successfully"));
    }*/

    /**
     * Adds custom remarks to an absence record.
     *
     * @param absentID                The ID of the absence record.
     * @param addAbsentRemarksRequest The details of the custom remarks.
     * @return ResponseEntity with a success message, log ID, timestamp, and remarks.
     */
    /*@PutMapping("/addCustomAbsentRemarks")
    @Transactional
    public ResponseEntity<Map<String, Object>> addCustomAbsentRemarks(@RequestParam UUID absentID, @RequestBody AddAbsentRemarksRequest addAbsentRemarksRequest) {
        Optional<Absent_Details> absentOptional = attendanceRepository.getAbsentDetails(absentID);

        if (absentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Absent details not found, unable to update remarks"));
        }

        // Create the new absent log
        Absent_Logs absentLog = new Absent_Logs(absentID, addAbsentRemarksRequest.getRemarksBy(), "<b>Custom Remarks:</b>\n" + addAbsentRemarksRequest.getRemarks(), "Action By Staff");
        absentLogsRepository.save(absentLog);

        // Return the log ID, remarks and timestamp in the response
        return ResponseEntity.ok(Map.of("message", "Custom absent remarks added successfully", "logID", absentLog.getLogID(), "timestamp", absentLog.getTimestamp(), "remarks", absentLog.getRemarks()));
    }*/

    /**
     * Rejects an absence with remarks, optionally allowing the user to resubmit the absence justification.
     *
     * @param absentID             The ID of the absence record.
     * @param rejectRemarksRequest The details of the rejection remarks.
     * @return ResponseEntity with a success message, log ID, timestamp, and remarks,
     * or a 404 status if the attendance or absence details are not found.
     * @throws MessagingException If an error occurs while sending the rejection email.
     */
    /*@PutMapping("/rejectWithRemarks")
    @Transactional
    public ResponseEntity<Map<String, Object>> rejectWithRemarks(@RequestParam UUID absentID, @RequestBody RejectRemarksRequest rejectRemarksRequest) throws MessagingException {
        Optional<Attendance> attendanceOptional = attendanceRepository.findAbsentByAbsentID(absentID);

        if (attendanceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Attendance not found, unable to reject absence"));
        }

        Absent_Details absentDetails = attendanceOptional.get().getAbsentDetails();

        if (absentDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Absent details not found, unable to reject absence"));
        }

        if (rejectRemarksRequest.getResubmitJustification()) {
            absentDetails.setStatus("Pending");
            absentDetailsRepository.save(absentDetails);
            Absent_Logs absentLog = new Absent_Logs(absentID, rejectRemarksRequest.getRemarksBy(), "Absence was rejected and user is requested to resubmit absence justification.\n\n<b>Rejection Reason:</b> " + rejectRemarksRequest.getRemarks() + "\n\nAbsence status updated to [Pending]", "Action By Staff");
            absentLogsRepository.save(absentLog);

            // Send email to user
            emailService.sendRejectionWithResubmissionEmail("zsoh007", AbsentService.generateOneTimeLink(absentID), java.sql.Date.valueOf(attendanceOptional.get().getLabsession().getDate()), attendanceOptional.get().getLabsession().getStartTime(), attendanceOptional.get().getLabsession().getEndTime(), attendanceOptional.get().getStudent().getFullName(), attendanceOptional.get().getLabsession().getClassGroup().getModule().getModuleCode(), attendanceOptional.get().getLabsession().getClassGroupID().getClassGroupID(), attendanceOptional.get().getLabsession().getLabID().getLabName(), rejectRemarksRequest.getRemarks());
            // TODO: change this to send to real user instead of myself


            // Return the log ID, remarks and timestamp in the response
            return ResponseEntity.ok(Map.of("message", "Absence rejected successfully - pending resubmit justification", "logID", absentLog.getLogID(), "timestamp", absentLog.getTimestamp(), "remarks", absentLog.getRemarks()));
        } else {
            absentDetails.setStatus("Rejected");
            absentDetailsRepository.save(absentDetails);
            Absent_Logs absentLog = new Absent_Logs(absentID, rejectRemarksRequest.getRemarksBy(), "Absence was rejected. \n\n<b>Rejection Reason:</b> " + rejectRemarksRequest.getRemarks() + "\n\nAbsence status updated to [Rejected]", "Action By Staff");
            absentLogsRepository.save(absentLog);

            // Send email to user
            emailService.sendRejectionEmail("zsoh007", java.sql.Date.valueOf(attendanceOptional.get().getLabsession().getDate()), attendanceOptional.get().getLabsession().getStartTime(), attendanceOptional.get().getLabsession().getEndTime(), attendanceOptional.get().getStudent().getFullName(), attendanceOptional.get().getLabsession().getClassGroup().getModule().getModuleCode(), attendanceOptional.get().getLabsession().getClassGroupID().getClassGroupID(), attendanceOptional.get().getLabsession().getLabID().getLabName(), rejectRemarksRequest.getRemarks());
            // TODO: change this to send to real user instead of myself

            // Return the log ID, remarks and timestamp in the response
            return ResponseEntity.ok(Map.of("message", "Absence rejected successfully", "logID", absentLog.getLogID(), "timestamp", absentLog.getTimestamp(), "remarks", absentLog.getRemarks()));
        }

    }*/

    /**
     * Reschedules a make-up lab session for an absence.
     *
     * @param absentID                The ID of the absence record.
     * @param rescheduleMakeUpRequest The details of the new make-up lab session.
     * @return ResponseEntity with a success message if the make-up session is rescheduled,
     * or an error message if validation fails.
     * @throws MessagingException If an error occurs while sending the rescheduling email.
     */
    /*@PutMapping("/rescheduleMakeUpSession")
    @Transactional
    public ResponseEntity<Map<String, Object>> approveAbsent(@RequestParam UUID absentID, @RequestBody RescheduleMakeUpRequest rescheduleMakeUpRequest) throws MessagingException {
        // Check and make sure all parameters in the request are present
        if (rescheduleMakeUpRequest.getNewMakeUpLabSessionID() == null || rescheduleMakeUpRequest.getCurrentMakeUpLabSessionID() == null || rescheduleMakeUpRequest.getLabName() == null || rescheduleMakeUpRequest.getLabRoom() == null || rescheduleMakeUpRequest.getLabDate() == null || rescheduleMakeUpRequest.getLabStartTime() == null || rescheduleMakeUpRequest.getLabEndTime() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "newMakeUpLabSessionID, currentMakeUpLabSessionID, labName, labRoom, labDate, labStartTime, labEndTime fields are required for rescheduling make up session"));
        } else if (absentID == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "absentID field is required for rescheduling make up session"));
        }

        // Check if the current make up session ID is the same as the new make up session ID
        if (rescheduleMakeUpRequest.getCurrentMakeUpLabSessionID().equals(rescheduleMakeUpRequest.getNewMakeUpLabSessionID())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Current make up session ID is the same as the new make up session ID, no changes made"));
        }

        Optional<Absent_Details> absentOptional = attendanceRepository.getAbsentDetails(absentID);

        if (absentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Absent details not found, unable to reschedule make up session"));
        }

        Absent_Details absentDetails = absentOptional.get();

        if (absentDetails.getMakeUpAttendanceID() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Make up attendance ID is null, this absence does not have a make up session to reschedule."));
        }

        Optional<Attendance> makeUpAttendanceOptional = attendanceRepository.findById(absentDetails.getMakeUpAttendanceID());

        if (makeUpAttendanceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Make up attendance not found, unable to reschedule make up session"));
        }

        Attendance makeUpAttendance = makeUpAttendanceOptional.get();

        // Check if current make up lab session is valid
        Optional<LabSession> currentMakeUpLabSessionOptional = labSessionRepository.findById(rescheduleMakeUpRequest.getCurrentMakeUpLabSessionID());
        if (currentMakeUpLabSessionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Current make up lab session not found, unable to reschedule make up session"));
        }

        LabSession currentMakeUpLabSession = currentMakeUpLabSessionOptional.get();

        // remove the reference from absent_details
        absentDetails.setMakeUpAttendanceID(null);
        absentDetails.setMakeUpAttendance(null);
        absentDetailsRepository.save(absentDetails);

        // Afterwards, I need to delete the current make up attendance record
        attendanceRepository.delete(makeUpAttendance);

        // I need to also remove the current make up lab session if there are no students enrolled in it
        List<Attendance> makeUpAttendances = attendanceRepository.findAllByLabSessionId(currentMakeUpLabSession.getAdhocSessionID());
        if (makeUpAttendances.isEmpty()) {
            labSessionRepository.delete(currentMakeUpLabSession);
        }

        // Check if new make up lab session is valid aka it has already been created before for another make up lesson, if not create a new one
        Optional<LabSession> newMakeUpLabSessionOptional = labSessionRepository.findById(rescheduleMakeUpRequest.getNewMakeUpLabSessionID());
        LabSession newMakeUpLabSession;

        if (newMakeUpLabSessionOptional.isEmpty()) {
            // Create custom lab session for make up session
            LabId labId = new LabId(rescheduleMakeUpRequest.getLabName(), rescheduleMakeUpRequest.getLabRoom());
            Optional<Lab> labOptional = labRepository.findById(labId);
            if (labOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Lab not found, please input a valid lab and lab room unable to reschedule make up session"));
            }

            newMakeUpLabSession = LabSession.builder().AdhocSessionID(rescheduleMakeUpRequest.getNewMakeUpLabSessionID()).classGroupID(currentMakeUpLabSession.getClassGroupID()).Date(rescheduleMakeUpRequest.getLabDate()).StartTime(rescheduleMakeUpRequest.getLabStartTime()).EndTime(rescheduleMakeUpRequest.getLabEndTime()).isMakeUpLabSession(true).labID(labId).build();

            labSessionRepository.save(newMakeUpLabSession);
        } else {
            newMakeUpLabSession = newMakeUpLabSessionOptional.get();
        }

        // Create a new attendance record for the new make up lab session
        Attendance newMakeUpAttendance = Attendance.builder().Lab_SessionID(newMakeUpLabSession.getAdhocSessionID()).Student_ID(makeUpAttendance.getStudent_ID()).Status("Pending").isMakeUpSession(true).Semester_ID(makeUpAttendance.getSemester_ID()).Remarks("").build();
        attendanceRepository.save(newMakeUpAttendance);

        // Update the absent details to point to the new make up attendance record
        absentDetails.setMakeUpAttendanceID(newMakeUpAttendance.getAttendance_ID());
        absentDetailsRepository.save(absentDetails);

        // Create a new absent log for the rescheduling
        Absent_Logs absentLog = new Absent_Logs(absentID, makeUpAttendance.getStudent_ID(), "Rescheduled Make Up Lesson Session: \n\n" + "Lab Name: " + newMakeUpLabSession.getLabID().getLabName() + "\nRoom: " + newMakeUpLabSession.getLabID().getRoom() + "\nDate: " + newMakeUpLabSession.getDate() + "\nStart Time: " + newMakeUpLabSession.getStartTime() + "\nEnd Time: " + newMakeUpLabSession.getEndTime(), "Action By Student");
        absentLogsRepository.save(absentLog);

        // TODO: update zsoh007 to real student id when production
        // Send email to student to inform them that their make up session has been rescheduled
        emailService.sendRescheduledMakeUpEmail("zsoh007", java.sql.Date.valueOf(newMakeUpLabSession.getDate()), newMakeUpLabSession.getStartTime(), newMakeUpLabSession.getEndTime(), makeUpAttendance.getStudent().getFullName(), newMakeUpLabSession.getClassGroupID().getModuleCode(), newMakeUpLabSession.getClassGroupID().getClassGroupID(), newMakeUpLabSession.getLabID().getLabName(), newMakeUpLabSession.getLabID().getRoom().toString(), AbsentService.generateOneTimeLink(absentID));

        return ResponseEntity.ok(Map.of("message", "Make up session rescheduled successfully"));
    }*/

    /**
     * Approves or rejects an absence, optionally rescheduling a make-up session.
     *
     * @param absentID             The ID of the absence record.
     * @param approveAbsentRequest The details of the approval or rejection.
     * @return ResponseEntity with a success message, log ID, timestamp, and remarks,
     * or an error message if validation fails.
     */
    /*@PutMapping("/approveAbsent")
    @Transactional
    public ResponseEntity<Map<String, Object>> approveAbsent(@RequestParam UUID absentID, @RequestBody ApproveAbsentRequest approveAbsentRequest) {
        System.out.println("approve request: " + approveAbsentRequest);
        boolean rescheduleMakeUp = false;
        if (approveAbsentRequest.getApprovalStatus() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "approvalStatus field is required"));
        }

        Optional<Absent_Details> absentOptional = attendanceRepository.getAbsentDetails(absentID);

        if (absentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Absent details not found, unable to approve"));
        }

        Absent_Details absentDetails = absentOptional.get();

        Optional<User> userOptional = userRepository.findByUsername(approveAbsentRequest.getApprovedByUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User (approver) not found, unable to approve"));
        }

        Absent_Logs absentLog;
        if (approveAbsentRequest.getApprovalStatus().equals("Approved")) {
            attendanceRepository.approveAbsent(absentID, approveAbsentRequest.getApprovalStatus());
            absentLog = new Absent_Logs(absentID, userOptional.get().getName(), "Absence status updated to [Approved]", "Action By Staff");
            absentLogsRepository.save(absentLog);

            if (absentDetails.getMakeUpAttendanceID() == null) {
                // When make up attendance ID is null, this means this absent is not for a makeup session
                // Mark attendance as "Excused" for the student
                Optional<Attendance> attendanceOptional = attendanceRepository.findAbsentByAbsentID(absentDetails.getAbsent_ID());
                if (attendanceOptional.isPresent()) {
                    Attendance attendance = attendanceOptional.get();
                    attendance.setStatus("Excused");
                    attendanceRepository.save(attendance);

                    // TODO: Send email to student to inform them that their absence has been approved
                    // TODO: update student id to real id when production
                    emailService.sendApprovedEmail("zsoh007", java.sql.Date.valueOf(attendance.getLabsession().getDate()), attendance.getLabsession().getStartTime(), attendance.getLabsession().getEndTime(), attendance.getStudent().getFullName(), attendance.getLabsession().getClassGroup().getModule().getModuleCode(), attendance.getLabsession().getClassGroupID().getClassGroupID(), attendance.getLabsession().getLabID().getLabName());

                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Attendance not found, cannot approve absence"));
                }
            } // No else because its auto approved when the user attend make up session and the attendance is marked as present


        } else if (approveAbsentRequest.getApprovalStatus().equals("Awaiting Make Up")) {
            // Check if lab session id exists in parameter
            if (approveAbsentRequest.getNewLabSessionID() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "newLabSessionID field is required for scheduling make up session"));
            }

            Optional<Attendance> attendanceOptional = attendanceRepository.findAbsentByAbsentID(absentDetails.getAbsent_ID());
            if (attendanceOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Attendance not found, unable to approve absence"));
            }

            // Retrieve important details such as studentID, semesterID, moduleCode
            String studentID = attendanceOptional.get().getStudent_ID();
            String semesterID = attendanceOptional.get().getSemester_ID();
            String moduleCode = attendanceOptional.get().getLabsession().getClassGroup().getModule().getModuleCode();

            // Check if the student is already enrolled in an existing make up lab session
            // which implies this is a reschedule of the make up session
            if (absentDetails.getMakeUpAttendanceID() != null) {

                // Retrieve the existing attendance record for the make up session
                Optional<Attendance> existingMakeUpAttendanceOptional = attendanceRepository.findById(absentDetails.getMakeUpAttendanceID());

                if (existingMakeUpAttendanceOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Make up attendance not found, unable to reschedule"));
                }

                Attendance existingMakeUpAttendance = existingMakeUpAttendanceOptional.get();

                // Retrieve the existing lab session record for the make up session
                Optional<LabSession> existingLabSessionOptional = labSessionRepository.findById(existingMakeUpAttendance.getLab_SessionID());

                if (existingLabSessionOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Lab session not found, unable to reschedule"));
                }

                LabSession existingLabSession = existingLabSessionOptional.get();

                // Remove reference from absent details
                absentDetails.setMakeUpAttendanceID(null);
                absentDetails.setMakeUpAttendance(null);
                absentDetails.setStatus("Awaiting Make Up");
                absentDetailsRepository.save(absentDetails);

                List<Attendance> makeUpAttendances = attendanceRepository.findAllByLabSessionId(existingMakeUpAttendance.getLab_SessionID());
                int countOfLabStudents = makeUpAttendances.size();

                // Delete the existing make up attendance record
                attendanceRepository.delete(existingMakeUpAttendance);

                // Delete the existing make up lab session if there are no students enrolled in it
                if (countOfLabStudents == 1) {
                    labSessionRepository.delete(existingLabSession);
                }

                rescheduleMakeUp = true;
            }

            // Create a custom class group for make up sessions only if it does not exist
            ClassGroupId classGroupId = new ClassGroupId("MAKEUP", moduleCode, semesterID);
            Optional<ClassGroup> classGroupOptional = classGroupRepository.findById(classGroupId);
            if (classGroupOptional.isEmpty()) {
                // Retrieve the module object
                Optional<Module> moduleOptional = moduleRepository.findById(moduleCode);
                if (moduleOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Module not found, unable to create class group"));
                }
                Module module = moduleOptional.get();

                // Create and save the class group
                ClassGroup classGroup = ClassGroup.builder().classGroupId(classGroupId).module(module).build();
                classGroupRepository.save(classGroup);
            }

            // Add student to class group if not already added
            StudentEnrolledClassGroupId studentEnrolledClassGroupId = new StudentEnrolledClassGroupId(studentID, classGroupId.getClassGroupID(), moduleCode, semesterID);
            Optional<Student_Enrolled_ClassGroup> studentEnrolledClassGroupOptional = classGroupEnrolledStudentsRepository.findById(studentEnrolledClassGroupId);

            if (studentEnrolledClassGroupOptional.isEmpty()) {
                // Retrieve the student object
                Optional<Student> studentOptional = studentRepository.findById(studentID);
                if (studentOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Student not found, unable to enroll in class group"));
                }
                Student student = studentOptional.get();

                // Create and save the student enrolled class group
                Student_Enrolled_ClassGroup studentEnrolledClassGroup = Student_Enrolled_ClassGroup.builder().id(studentEnrolledClassGroupId).student(student).build();
                classGroupEnrolledStudentsRepository.save(studentEnrolledClassGroup);
            }

            Optional<LabSession> optionalLabSession = labSessionRepository.findById(approveAbsentRequest.getExistingLabSessionID());
            LabSession newLabSession;

            if (optionalLabSession.isEmpty()) {
                if (approveAbsentRequest.getLabDate() == null || approveAbsentRequest.getLabName() == null || approveAbsentRequest.getLabRoom() == null || approveAbsentRequest.getLabStartTime() == null || approveAbsentRequest.getLabEndTime() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "labDate, labName, labRoom, labStartTime, labEndTime fields are required for scheduling custom make up session"));
                }
                // Create custom lab session for make up session
                // Check if lab exists:
                LabId labId = new LabId(approveAbsentRequest.getLabName(), approveAbsentRequest.getLabRoom());
                Optional<Lab> labOptional = labRepository.findById(labId);
                if (labOptional.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Lab not found, please input a valid lab and lab room unable to schedule make up session"));
                }

                // Check whether the lab session is already created
                Optional<LabSession> newLabSessionOptional = labSessionRepository.findById(approveAbsentRequest.getNewLabSessionID());
                if (newLabSessionOptional.isEmpty()) {
                    newLabSession = LabSession.builder().AdhocSessionID(approveAbsentRequest.getNewLabSessionID()).classGroupID(classGroupId).Date(approveAbsentRequest.getLabDate()).StartTime(approveAbsentRequest.getLabStartTime()).EndTime(approveAbsentRequest.getLabEndTime()).isMakeUpLabSession(true).labID(labId).build();

                    labSessionRepository.save(newLabSession);
                } else {
                    newLabSession = newLabSessionOptional.get();
                }


            } else {
                // Create a new lab session for the MAKEUP class group using the same details as the provided lab session
                LabSession currLabSession = optionalLabSession.get();
                Optional<LabSession> newLabSessionOptional = labSessionRepository.findById(approveAbsentRequest.getNewLabSessionID());
                if (newLabSessionOptional.isEmpty()) {
                    newLabSession = LabSession.builder().AdhocSessionID(approveAbsentRequest.getNewLabSessionID()).classGroupID(classGroupId).Date(currLabSession.getDate()).StartTime(currLabSession.getStartTime()).EndTime(currLabSession.getEndTime()).labID(currLabSession.getLabID()).isMakeUpLabSession(true).build();

                    labSessionRepository.save(newLabSession);
                } else {
                    newLabSession = newLabSessionOptional.get();
                }

            }

            // Check if student is already enrolled in the make up session
            Optional<Attendance> makeUpAttendanceOptional = attendanceRepository.findAttendanceByLabSessionIdAndSemesterIdAndStudentId(newLabSession.getAdhocSessionID(), studentID);
            if (makeUpAttendanceOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Student is already enrolled in the make up session"));
            }

            // Create a new attendance record for the makeup session
            Attendance makeUpAttendance = Attendance.builder().Lab_SessionID(newLabSession.getAdhocSessionID()).Student_ID(studentID).Semester_ID(semesterID).Status("Pending").isMakeUpSession(true).build();

            attendanceRepository.save(makeUpAttendance);

            // Tie the previous absence to this attendance
            absentDetails.setMakeUpAttendanceID(makeUpAttendance.getAttendance_ID());
            absentDetails.setStatus("Awaiting Make Up");
            absentDetailsRepository.save(absentDetails);

            if (!rescheduleMakeUp) {
                // Create logs
                absentLog = new Absent_Logs(absentID, userOptional.get().getName(), "Make Up Lesson Session Scheduled: \n\n" + "Lab Name: " + newLabSession.getLabID().getLabName() + "\nRoom: " + newLabSession.getLabID().getRoom() + "\nDate: " + newLabSession.getDate() + "\nStart Time: " + newLabSession.getStartTime() + "\nEnd Time: " + newLabSession.getEndTime() + "\n\n Status Updated to [Awaiting Make Up]", "Action By Staff");
                absentLogsRepository.save(absentLog);

                // Send email to student
                // TODO: change this to send to real user instead of myself
                emailService.sendMakeUpSessionEmail("zsoh007", java.sql.Date.valueOf(newLabSession.getDate()), newLabSession.getStartTime(), newLabSession.getEndTime(), attendanceOptional.get().getStudent().getFullName(), newLabSession.getClassGroupID().getModuleCode(), attendanceOptional.get().getLabsession().getClassGroupID().getClassGroupID(), newLabSession.getLabID().getLabName(), newLabSession.getLabID().getRoom().toString(), AbsentService.generateOneTimeLink(absentDetails.getAbsent_ID()));
            } else {
                // Create logs
                absentLog = new Absent_Logs(absentID, userOptional.get().getName(), "Rescheduled Make Up Lesson Session: \n\n" + "Lab Name: " + newLabSession.getLabID().getLabName() + "\nRoom: " + newLabSession.getLabID().getRoom() + "\nDate: " + newLabSession.getDate() + "\nStart Time: " + newLabSession.getStartTime() + "\nEnd Time: " + newLabSession.getEndTime(), "Action By Staff");
                absentLogsRepository.save(absentLog);

                // Send reschedule email to student
                // TODO: change this to send to real user instead of myself
                emailService.sendRescheduledMakeUpEmail("zsoh007", java.sql.Date.valueOf(newLabSession.getDate()), newLabSession.getStartTime(), newLabSession.getEndTime(), attendanceOptional.get().getStudent().getFullName(), newLabSession.getClassGroupID().getModuleCode(), attendanceOptional.get().getLabsession().getClassGroupID().getClassGroupID(), newLabSession.getLabID().getLabName(), newLabSession.getLabID().getRoom().toString(), AbsentService.generateOneTimeLink(absentDetails.getAbsent_ID()));
            }


        } else {
            attendanceRepository.approveAbsent(absentID, approveAbsentRequest.getApprovalStatus());

            absentLog = new Absent_Logs(absentID, userOptional.get().getName(), "Absence status updated to [" + approveAbsentRequest.getApprovalStatus() + "]", "Action By Staff");
            absentLogsRepository.save(absentLog);
        }

        return ResponseEntity.ok(Map.of("message", "Absent approval status updated successfully, new status: " + approveAbsentRequest.getApprovalStatus(), "logID", absentLog.getLogID(), "timestamp", absentLog.getTimestamp(), "remarks", absentLog.getRemarks()));

    }*/

    /**
     * Retrieves current lab sessions based on the lab name, room, date, and time range.
     *
     * @param request The details of the current lab sessions to retrieve.
     * @return A list of current attendance records for lab sessions.
     */
    /*@PostMapping("/currentLabSessions")
    public List<Attendance> getCurrentLabSessions(@RequestBody GetCurrentLabSessionsRequest request) {

        return attendanceRepository.findAttendancesByLabNameAndRoomAndDateAndTimeRange(request.getLabName().toUpperCase(), request.getRoom(), request.getCurrentDate(), request.getCurrentTime());
    }*/

    /**
     * Retrieves all lab sessions from the current time to the end of the day.
     *
     * @param request The details of the lab sessions to retrieve.
     * @return A list of attendance records for lab sessions from the current time to the end of the day.
     */
    @PostMapping("/labSessionsToEndOfDay")
    public List<Attendance> getLabSessionsToEndOfDay(@RequestBody GetCurrentLabSessionsRequest request) {
        LocalTime endOfDay = LocalTime.of(23, 59, 59);
        List<Attendance> currentSessions = attendanceRepository.findAttendancesByLabNameAndRoomAndDateAndTimeRange(
                request.getLabName().toUpperCase(),
                request.getRoom(),
                request.getCurrentDate(),
                request.getCurrentTime()
        );
        List<Attendance> upcomingSessions = attendanceRepository.findAttendancesByLabNameAndRoomAndDateAndTimeRangeToEndOfDay(
                request.getLabName().toUpperCase(),
                request.getRoom(),
                request.getCurrentDate(),
                request.getCurrentTime(),
                endOfDay
        );
        currentSessions.addAll(upcomingSessions);

        System.out.println("Current sessions found: " + currentSessions.size());
        System.out.println("Upcoming sessions found: " + upcomingSessions.size());

        return currentSessions;
    }

    /**
     * Mark or Change Attendance (used by both tablet client and admin app)
     *
     * @param markAttendanceDTO The details of the attendance to be marked or changed.
     * @return ResponseEntity with a success message if the attendance is marked successfully,
     * or a 404 status if the attendance record is not found.
     */
    @PutMapping("/markAttendance")
    public ResponseEntity<MessageResponse> markAttendance(@RequestBody MarkAttendanceDTO markAttendanceDTO) {
        System.out.println("Received mark attendance request: " + markAttendanceDTO);

        switch(attendanceService.markAttendance(markAttendanceDTO)){
            case SUCCESS -> {
                Optional<Attendance> attendanceOptional = attendanceRepository.findById(markAttendanceDTO.getAttendanceID());
                Attendance attendance = attendanceOptional.get();
                return ResponseEntity.ok
                        (new MessageResponse
                                ("\"Attendance marked successfully, from \" + attendance.getStatus() + \" to \" + markAttendanceDTO.getStatus())"
                                        , ExecutionStatus.SUCCESS));
            }
            case NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                        "Attendance record not found", ExecutionStatus.NOT_FOUND));
            }
            default -> {return null;}
        }
    }

    /**
     * Checks if a lab room is full for a specific date and time range, considering the number of students to be added.
     *
     * @param labName               The name of the lab.
     * @param room                  The room number.
     * @param date                  The date of the lab session.
     * @param startTime             The start time of the lab session.
     * @param endTime               The end time of the lab session.
     * @param noOfStudentsToBeAdded The number of students to be added to the lab session.
     * @return ResponseEntity containing information about whether the lab room is full.
     */
    @GetMapping("/isLabRoomFull")
    public ResponseEntity<Map<String, Object>> isLabRoomFull(@RequestParam String labName,
                                                             @RequestParam int room,
                                                             @RequestParam LocalDate date,
                                                             @RequestParam LocalTime startTime,
                                                             @RequestParam LocalTime endTime,
                                                             @RequestParam int noOfStudentsToBeAdded) {
        Map<String, Object> result = Utility.isLabRoomFull(labName, room, date, startTime, endTime, noOfStudentsToBeAdded, labRepository, attendanceRepository);
        if (result.containsKey("message")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
