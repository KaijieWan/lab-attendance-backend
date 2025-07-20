package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.CreateLabSessionDTO;
import com.example.lab_attendance_app.models.dto.LabSessionWithRemainingCapacityDTO;
import com.example.lab_attendance_app.models.dto.MakeUpLabSessionDTO;
import com.example.lab_attendance_app.models.dto.UpdateLabSessionDTO;
import com.example.lab_attendance_app.models.entities.Lab;
import com.example.lab_attendance_app.models.entities.LabSession;

import java.util.List;

public interface LabSessionService {
    public ExecutionStatus createLabSession(CreateLabSessionDTO labSession);
    public List<LabSession> getAllLabSessions();
    public List<String> getDistinctModulesBySemester(String semester);
    public ExecutionStatus updateLabSessions(UpdateLabSessionDTO updateRequest);
    public ExecutionStatus deleteLabSession(String labSessionID);
    public LabSession getLabSessionById(String labSessionID);
    public List<Object[]> getLabSchedules(String lab, int room, String semester);
    public List<LabSessionWithRemainingCapacityDTO> getLabSessionsByDateRangeAndModuleCode(MakeUpLabSessionDTO makeUpLabSessionDTO);
    public List<Lab> getAllLabs();
    public List<LabSession> getSpecificLabSessions(String classGroupId, String moduleCode, String semesterId);
    public ExecutionStatus updateLabCapacity(String labName, int room, int newCapacity);
}
