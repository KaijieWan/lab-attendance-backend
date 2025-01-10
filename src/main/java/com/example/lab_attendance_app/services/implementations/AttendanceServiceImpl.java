package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.CreateAttendanceDTO;
import com.example.lab_attendance_app.models.entities.Absent_Details;
import com.example.lab_attendance_app.models.entities.Attendance;
import com.example.lab_attendance_app.models.repositories.AbsentDetailsRepository;
import com.example.lab_attendance_app.models.repositories.AttendanceRepository;
import com.example.lab_attendance_app.services.AttendanceService;
import com.example.lab_attendance_app.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger logger = LogManager.getLogger(AttendanceServiceImpl.class);
    private final AttendanceRepository attendanceRepository;
    private final AbsentDetailsRepository absentDetailsRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, AbsentDetailsRepository absentDetailsRepository) {
        this.attendanceRepository = attendanceRepository;
        this.absentDetailsRepository = absentDetailsRepository;
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
}
