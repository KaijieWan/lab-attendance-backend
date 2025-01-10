package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.CreateLabSessionDTO;
import com.example.lab_attendance_app.models.dto.LabSessionWithRemainingCapacityDTO;
import com.example.lab_attendance_app.models.dto.MakeUpLabSessionDTO;
import com.example.lab_attendance_app.models.dto.UpdateLabSessionDTO;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Lab;
import com.example.lab_attendance_app.models.entities.LabSession;
import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import com.example.lab_attendance_app.models.entities.embedded.LabId;
import com.example.lab_attendance_app.models.repositories.*;
import com.example.lab_attendance_app.services.LabSessionService;
import com.example.lab_attendance_app.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LabSessionServiceImpl implements LabSessionService {
    private static final Logger logger = LogManager.getLogger(LabSessionServiceImpl.class);
    private final LabSessionRepository labSessionRepository;
    private final LabRepository labRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassGroupRepository classGroupRepository;

    public LabSessionServiceImpl(LabSessionRepository labSessionRepository, LabRepository labRepository, ClassGroupRepository classGroupRepository,
                                 AttendanceRepository attendanceRepository) {
        this.labSessionRepository = labSessionRepository;
        this.labRepository = labRepository;
        this.classGroupRepository = classGroupRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public ExecutionStatus createLabSession(CreateLabSessionDTO labSession) {
        Optional<LabSession> existingLabSession = labSessionRepository.findById(labSession.getLabSessionID());

        if (existingLabSession.isPresent()) {
            return ExecutionStatus.VALIDATION_ERROR;
        }

        ClassGroupId classGroupId = new ClassGroupId(labSession.getClass_group_id(), labSession.getModule_code(), labSession.getSemesterID());
        LabId labId = new LabId(labSession.getLab_name(), labSession.getRoom());
        logger.info("{} {} {}", labSession.getClass_group_id(), labSession.getLab_name(), labSession.getRoom());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime startTime, endTime;

        try {
            startTime = LocalTime.parse(labSession.getStartTime(), formatter);
            endTime = LocalTime.parse(labSession.getEndTime(), formatter);
        } catch (DateTimeParseException e) {
            return ExecutionStatus.INVALID;
        }

        LabSession newLabSession = new LabSession(labSession.getLabSessionID(), labSession.getDate(), startTime, endTime, false, classGroupId, labId);


        // Retrieve and set the ClassGroup entity
        Optional<ClassGroup> classGroupOptional = classGroupRepository.findById(classGroupId);
        if (classGroupOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        // Retrieve and set the Lab entity
        Optional<Lab> labOptional = labRepository.findById(labId);
        if (labOptional.isEmpty()) {
            return ExecutionStatus.FAILED;
        }

        // Save the new LabSession
        labSessionRepository.save(newLabSession);
        return ExecutionStatus.SUCCESS;
    }

    public ExecutionStatus updateLabSessions(UpdateLabSessionDTO updateRequest) {
        List<LabSession> labSessions = labSessionRepository.findLabSessionsBySemesterModuleAndClassGroup(
                updateRequest.getSemesterID(),
                updateRequest.getModuleCode(),
                updateRequest.getClassGroupID()
        );

        if (labSessions.isEmpty()) {
            return ExecutionStatus.FAILED;
        }

        LabId newLabId = new LabId(updateRequest.getNewLabName(), Integer.parseInt(updateRequest.getNewRoom()));
        Optional<Lab> labOptional = labRepository.findById(newLabId);
        if (labOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        for (LabSession labSession : labSessions) {
            labSession.setLabID(newLabId);
            labSessionRepository.save(labSession);
        }

        return ExecutionStatus.SUCCESS;
    }

    public List<LabSession> getAllLabSessions(){
        return labSessionRepository.findAll();
    }

    public List<String> getDistinctModulesBySemester(String semester){
        return labSessionRepository.findDistinctModulesBySemester(semester);
    }

    public ExecutionStatus deleteLabSession(String labSessionID) {
        Optional<LabSession> labSessionOptional = labSessionRepository.findById(labSessionID);

        if (labSessionOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        // Delete all associated attendances
        attendanceRepository.deleteAttendancesByLabSessionID(labSessionID);

        // Delete the lab session
        labSessionRepository.deleteById(labSessionID);

        return ExecutionStatus.SUCCESS;
    }

    public LabSession getLabSessionById(String labSessionID) {
        Optional<LabSession> labSession = labSessionRepository.findByLabSessionID(labSessionID);
        if (labSession.isEmpty()) {
            return null;
        } else {
            return labSession.get();
        }
    }

    public List<Object[]> getLabSchedules(String lab, int room, String semester){
        return labSessionRepository.findLabSessionsByLabNameRoomAndSemesterWithStudentCount(lab.toUpperCase(), room, semester.toUpperCase());
    }

    public List<LabSessionWithRemainingCapacityDTO> getLabSessionsByDateRangeAndModuleCode(MakeUpLabSessionDTO makeUpLabSessionDTO){
        List<LabSession> labSessions = labSessionRepository.findByDateRangeAndModuleCode(
                makeUpLabSessionDTO.getStartDate(),
                makeUpLabSessionDTO.getEndDate(),
                makeUpLabSessionDTO.getModuleCode()
        );
        List<LabSessionWithRemainingCapacityDTO> labSessionWithRemainingCapacityList = new ArrayList<>();

        for (LabSession labSession : labSessions) {
            Map<String, Object> capacityInfo = Utility.isLabRoomFull(
                    labSession.getLabID().getLabName(),
                    labSession.getLabID().getRoom(),
                    labSession.getDate(),
                    labSession.getStartTime(),
                    labSession.getEndTime(),
                    0, // Assuming no additional students to be added for this check
                    labRepository,
                    attendanceRepository
            );

            int remainingCapacity = (int) capacityInfo.get("remainingCapacity");

            if (remainingCapacity > 0) {
                LabSessionWithRemainingCapacityDTO labSessionWithRemainingCapacity = new LabSessionWithRemainingCapacityDTO(
                        labSession,
                        remainingCapacity
                );
                labSessionWithRemainingCapacityList.add(labSessionWithRemainingCapacity);
            }
        }

        return labSessionWithRemainingCapacityList;
    }

    public List<Lab> getAllLabs(){
        return labRepository.findAll();
    }

    public ExecutionStatus updateLabCapacity(String labName, int room, int newCapacity){
        LabId labId = new LabId(labName, room);
        Optional<Lab> labOptional = labRepository.findById(labId);

        if (labOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        Lab lab = labOptional.get();
        lab.setCapacity(newCapacity);
        labRepository.save(lab);

        return ExecutionStatus.SUCCESS;
    }
}
