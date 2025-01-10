package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.Semester;
import com.example.lab_attendance_app.models.repositories.SemesterRepository;
import com.example.lab_attendance_app.services.SemesterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class SemesterServiceImpl implements SemesterService {
    private final SemesterRepository semesterRepository;

    public SemesterServiceImpl(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    public ExecutionStatus createSemester(Semester semester) {
        Optional<Semester> semesterOptional = semesterRepository.findById(semester.getSemesterID());

        if (semesterOptional.isPresent()) {
            return ExecutionStatus.VALIDATION_ERROR;
        }

        semesterRepository.save(semester);
        return ExecutionStatus.SUCCESS;
    }
}
