package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.*;
import com.example.lab_attendance_app.models.entities.AdhocSession;
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
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdhocSessionServiceImpl implements LabSessionService {
    private static final Logger logger = LogManager.getLogger(AdhocSessionServiceImpl.class);
    private final AdhocSessionRepository adhocSessionRepository;
    private final LabRepository labRepository;
    private final AttendanceRepository attendanceRepository;
    private final ClassGroupRepository classGroupRepository;

    public AdhocSessionServiceImpl(AdhocSessionRepository adhocSessionRepository, LabRepository labRepository, ClassGroupRepository classGroupRepository,
                                   AttendanceRepository attendanceRepository) {
        this.adhocSessionRepository = adhocSessionRepository;
        this.labRepository = labRepository;
        this.classGroupRepository = classGroupRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public ExecutionStatus createAdhocSession(CreateAdhocSessionDTO adhocSession) {
        Optional<AdhocSession> existingAdhocSession = adhocSessionRepository.findById(adhocSession.getAdhocSessionID());

        if (existingAdhocSession.isPresent()) {
            return ExecutionStatus.VALIDATION_ERROR;
        }

        ClassGroupId classGroupId = new ClassGroupId(adhocSession.getClass_group_id(), adhocSession.getModule_code(), adhocSession.getSemesterID());
        LabId labId = new LabId(adhocSession.getLab_name(), adhocSession.getRoom());
        String sessionContent = adhocSession.getSessionContent();
        logger.info("{} {} {}", adhocSession.getClass_group_id(), adhocSession.getLab_name(), adhocSession.getRoom());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime startTime, endTime;

        try {
            startTime = LocalTime.parse(adhocSession.getStartTime(), formatter);
            endTime = LocalTime.parse(adhocSession.getEndTime(), formatter);
        } catch (DateTimeParseException e) {
            return ExecutionStatus.INVALID;
        }

        AdhocSession newAdhocSession = new AdhocSession(adhocSession.getAdhocSessionID(), adhocSession.getDate(), startTime, endTime, classGroupId, sessionContent, labId);


        // Retrieve and check the ClassGroup entity
        Optional<ClassGroup> classGroupOptional = classGroupRepository.findById(classGroupId);
        if (classGroupOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        // Retrieve and check the Lab entity
        Optional<Lab> labOptional = labRepository.findById(labId);
        if (labOptional.isEmpty()) {
            return ExecutionStatus.FAILED;
        }

        // Save the new LabSession
        adhocSessionRepository.save(newAdhocSession);
        return ExecutionStatus.SUCCESS;
    }

    public ExecutionStatus updateAdhocSessions(UpdateAdhocSessionDTO updateRequest) {
        List<AdhocSession> adhocSessions = adhocSessionRepository.findAdhocSessionsBySemesterModuleAndClassGroupAndContent(
                updateRequest.getSemesterID(),
                updateRequest.getModuleCode(),
                updateRequest.getClassGroupID(),
                updateRequest.getSessionContent()
        );

        if (adhocSessions.isEmpty()) {
            return ExecutionStatus.FAILED;
        }

        LabId newLabId = new LabId(updateRequest.getNewLabName(), Integer.parseInt(updateRequest.getNewRoom()));
        Optional<Lab> labOptional = labRepository.findById(newLabId);
        if (labOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        for (AdhocSession adhocSession : adhocSessions) {
            adhocSession.setLabID(newLabId);
            adhocSession.setSessionContent(updateRequest.getSessionContent());
            adhocSessionRepository.save(adhocSession);
        }

        return ExecutionStatus.SUCCESS;
    }

    public List<AdhocSession> getAllAdhocSessions(){
        return adhocSessionRepository.findAll();
    }

    /*public List<String> getDistinctModulesBySemester(String semester){
        return adhocSessionRepository.findDistinctModulesBySemester(semester);
    }*/

    public ExecutionStatus deleteAdhocSession(String adhocSessionID) {
        Optional<AdhocSession> adhocSessionOptional = adhocSessionRepository.findById(adhocSessionID);

        if (adhocSessionOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        // Delete all associated attendances
        attendanceRepository.deleteAttendancesByLabSessionID(adhocSessionID);

        // Delete the lab session
        adhocSessionRepository.deleteById(adhocSessionID);

        return ExecutionStatus.SUCCESS;
    }

    public AdhocSession getAdhocSessionById(String adhocSessionID) {
        Optional<AdhocSession> adhocSession = adhocSessionRepository.findByAdhocSessionID(adhocSessionID);
        if (adhocSession.isEmpty()) {
            return null;
        } else {
            return adhocSession.get();
        }
    }

    public List<Object[]> getLabSchedules(String lab, int room, String semester){
        return adhocSessionRepository.findAdhocSessionsByLabNameRoomAndSemesterWithStudentCount(lab.toUpperCase(), room, semester.toUpperCase());
    }

    public List<AdhocSessionWithRemainingCapacityDTO> getLabSessionsByDateRangeAndModuleCode(MakeUpLabSessionDTO makeUpLabSessionDTO){
        List<AdhocSession> adhocSessions = adhocSessionRepository.findByDateRangeAndModuleCode(
                makeUpLabSessionDTO.getStartDate(),
                makeUpLabSessionDTO.getEndDate(),
                makeUpLabSessionDTO.getModuleCode()
        );
        List<LabSessionWithRemainingCapacityDTO> labSessionWithRemainingCapacityList = new ArrayList<>();

        for (AdhocSession adhocSession : adhocSessions) {
            Map<String, Object> capacityInfo = Utility.isLabRoomFull(
                    adhocSession.getLabID().getLabName(),
                    adhocSession.getLabID().getRoom(),
                    adhocSession.getDate(),
                    adhocSession.getStartTime(),
                    adhocSession.getEndTime(),
                    0, // Assuming no additional students to be added for this check
                    labRepository,
                    attendanceRepository
            );

            int remainingCapacity = (int) capacityInfo.get("remainingCapacity");

            if (remainingCapacity > 0) {
                LabSessionWithRemainingCapacityDTO labSessionWithRemainingCapacity = new LabSessionWithRemainingCapacityDTO(
                        adhocSession,
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

    public List<LabSession> getSpecificLabSessions(String classGroupId, String moduleCode, String semesterId){
        return adhocSessionRepository.findLabSessionsBySemesterModuleAndClassGroup(semesterId, moduleCode, classGroupId);
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
