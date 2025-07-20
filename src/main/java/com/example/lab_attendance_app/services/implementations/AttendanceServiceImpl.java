package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.CreateAttendanceDTO;
import com.example.lab_attendance_app.models.dto.MarkAttendanceDTO;
import com.example.lab_attendance_app.models.dto.UpdateRemarksDTO;
import com.example.lab_attendance_app.models.entities.Absent_Details;
import com.example.lab_attendance_app.models.entities.Absent_Logs;
import com.example.lab_attendance_app.models.entities.Attendance;
import com.example.lab_attendance_app.models.entities.User;
import com.example.lab_attendance_app.models.repositories.AbsentDetailsRepository;
import com.example.lab_attendance_app.models.repositories.AbsentLogsRepository;
import com.example.lab_attendance_app.models.repositories.AttendanceRepository;
import com.example.lab_attendance_app.models.repositories.UserRepository;
import com.example.lab_attendance_app.services.AttendanceService;
import com.example.lab_attendance_app.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger logger = LogManager.getLogger(AttendanceServiceImpl.class);
    private final AttendanceRepository attendanceRepository;
    private final AbsentDetailsRepository absentDetailsRepository;
    private final UserRepository userRepository;
    private final AbsentLogsRepository absentLogsRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, AbsentDetailsRepository absentDetailsRepository, UserRepository userRepository,
                                 AbsentLogsRepository absentLogsRepository) {
        this.attendanceRepository = attendanceRepository;
        this.absentDetailsRepository = absentDetailsRepository;
        this.userRepository = userRepository;
        this.absentLogsRepository = absentLogsRepository;
    }

    public ExecutionStatus createAttendance(CreateAttendanceDTO createAttendanceDTO){
        Optional<Attendance> existingAttendance = attendanceRepository.findByLabSessionIdAndStudentId(createAttendanceDTO.getLabSessionID(), createAttendanceDTO.getStudentID());

        if (existingAttendance.isPresent()) {
            return ExecutionStatus.INVALID;
        }

        if (createAttendanceDTO.getAbsentID() != null) {
            Optional<Absent_Details> existingAbsentDetails = absentDetailsRepository.findById(createAttendanceDTO.getAbsentID());

            if (existingAbsentDetails.isPresent()) {
                return ExecutionStatus.VALIDATION_ERROR;
            }
        }

        /* Create a new Attendance object and set the attributes */
        Attendance attendance = new Attendance();
        attendance.setLab_SessionID(createAttendanceDTO.getLabSessionID());
        attendance.setStudent_ID(createAttendanceDTO.getStudentID());

        if (createAttendanceDTO.getAbsentID() != null) {
            attendance.setAbsent_ID(createAttendanceDTO.getAbsentID());
        }
        attendance.setStatus(createAttendanceDTO.getStatus());
        attendance.setIsMakeUpSession(createAttendanceDTO.getIsMakeUpSession());
        attendance.setSemester_ID(createAttendanceDTO.getSemesterID());
        attendance.setRemarks(createAttendanceDTO.getRemarks());

        attendanceRepository.save(attendance);
        return ExecutionStatus.SUCCESS;
    }

    public List<Attendance> getAllAttendances(){
        return attendanceRepository.findAll();
    }

    public List<Attendance> getAttendanceByModuleAndSemesterGroupByClassGroupAndDate(String moduleCode, String semesterId){
        return attendanceRepository.findAttendanceByModuleAndSemesterGroupByClassGroupAndDate(moduleCode, semesterId);
    }

    public List<Attendance> getAllAttendancesByLabSessionId(String labSessionId){
        return attendanceRepository.findAllByLabSessionId(labSessionId);
    }

    public ExecutionStatus markAttendance(MarkAttendanceDTO markAttendanceDTO){
        Optional<Attendance> attendanceOptional = attendanceRepository.findById(markAttendanceDTO.getAttendanceID());

        if (attendanceOptional.isPresent()) {
            // Check whether if the attendance have an absent record -> this implies it was previously marked absent
            // Should override absence record as approved if an admin manually marks the attendance as present.
            if (attendanceOptional.get().getAbsentDetails() != null && (Objects.equals(markAttendanceDTO.getStatus(), "Present") || Objects.equals(markAttendanceDTO.getStatus(), "Excused"))) {
                Absent_Details absentDetails = attendanceOptional.get().getAbsentDetails();

                // Check if there is an approver and mark the absent as approved
                if (markAttendanceDTO.getApproverUsername() != null) {
                    Optional<User> userOptional = userRepository.findByUsername(markAttendanceDTO.getApproverUsername());
                    if (userOptional.isPresent() && !Objects.equals(absentDetails.getStatus(), "Approved")) {
                        // Mark absent as approved
                        absentDetails.setStatus("Approved");
                        absentDetailsRepository.save(absentDetails);

                        // Create a new absent log for the approval
                        Absent_Logs absentLog = new Absent_Logs(absentDetails.getAbsent_ID(), userOptional.get().getName(), "Attendance was marked present manually, hence absence record is no longer required. \n\nStatus updated to [Approved]", "Action By Staff");
                        absentLogsRepository.save(absentLog);
                    }
                }
            } else if (attendanceOptional.get().getAbsentDetails() == null && (Objects.equals(markAttendanceDTO.getStatus(), "Present") || Objects.equals(markAttendanceDTO.getStatus(), "Excused"))) {
                // If there is no absent record, there are two scenarios
                // One is this is a normal lab session, another is this is a makeup session
                // If it is a makeup session, we need to update the absent record to "Approved" after makeup attended
                // and also mark the original attendance as "Excused"
                // If it is a normal lab session, we just need to mark the attendance as present
                if (attendanceOptional.get().getIsMakeUpSession()) {
                    // This is a makeup session
                    // Retrieve the absent record
                    Absent_Details absentDetails = absentDetailsRepository.getAbsentDetailsByMakeUpAttendanceID(attendanceOptional.get().getAttendance_ID()).get();
                    absentDetails.setStatus("Approved");
                    absentDetailsRepository.save(absentDetails);

                    // Check if there is an approver and log the approval as accordingly
                    if (markAttendanceDTO.getApproverUsername() != null) {
                        Optional<User> userOptional = userRepository.findByUsername(markAttendanceDTO.getApproverUsername());
                        if (userOptional.isPresent() && !Objects.equals(absentDetails.getStatus(), "Approved")) {
                            // Create a new absent log for the approval
                            Absent_Logs absentLog = new Absent_Logs(absentDetails.getAbsent_ID(), userOptional.get().getName(), "Make Up Session Attendance was marked present/excused manually. \n\nStatus updated to [Approved]", "Action By Staff");
                            absentLogsRepository.save(absentLog);
                        }
                    } else {
                        if (!Objects.equals(absentDetails.getStatus(), "Approved")) {
                            // Create a new absent log for the approval
                            Absent_Logs absentLog = new Absent_Logs(absentDetails.getAbsent_ID(), "System", "Student attended make up session. \n\nStatus updated to [Approved]", "System Generated");
                            absentLogsRepository.save(absentLog);
                        }
                    }

                    // Retrieve original attendance record and mark it as "Excused"
                    Attendance originalAttendance = attendanceRepository.findAbsentByAbsentID(absentDetails.getAbsent_ID()).get();
                    originalAttendance.setStatus("Excused");
                    attendanceRepository.save(originalAttendance);
                }
            }

            Attendance attendance = attendanceOptional.get();
            attendance.setStatus(markAttendanceDTO.getStatus());
            attendanceRepository.save(attendance);
            return ExecutionStatus.SUCCESS;
        } else {
            return ExecutionStatus.NOT_FOUND;
        }
    }

    public ExecutionStatus updateAttendanceRemarks(UpdateRemarksDTO updateRemarksDTO){
        Optional<Attendance> existingAttendance = attendanceRepository.findById(Integer.parseInt(updateRemarksDTO.getAttendanceID()));

        if (existingAttendance.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        Attendance attendance = existingAttendance.get();
        attendance.setRemarks(updateRemarksDTO.getNewRemarks());
        attendanceRepository.save(attendance);

        return ExecutionStatus.SUCCESS;
    }
}
