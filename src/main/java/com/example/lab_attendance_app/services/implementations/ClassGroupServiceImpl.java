package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Module;
import com.example.lab_attendance_app.models.repositories.ClassGroupRepository;
import com.example.lab_attendance_app.models.repositories.ModuleRepository;
import com.example.lab_attendance_app.services.ClassGroupService;
import com.example.lab_attendance_app.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassGroupServiceImpl implements ClassGroupService {
    private static final Logger logger = LogManager.getLogger(ClassGroupServiceImpl.class);
    private final ClassGroupRepository classGroupRepository;
    private final ModuleRepository moduleRepository;

    public ClassGroupServiceImpl(ClassGroupRepository classGroupRepository, ModuleRepository moduleRepository) {
        this.classGroupRepository = classGroupRepository;
        this.moduleRepository = moduleRepository;
    }

    public List<ClassGroup> getAllClassGroups(){
        return classGroupRepository.findAll();
    }

    public ExecutionStatus createClassGroup(ClassGroup classGroup) {
        // Check if the ClassGroup already exists
        Optional<ClassGroup> existingClassGroup = classGroupRepository.findById(classGroup.getClassGroupId());
        if (existingClassGroup.isPresent()) {
            return ExecutionStatus.VALIDATION_ERROR;
        }

        // Retrieve the Module by its module code
        String moduleCode = classGroup.getClassGroupId().getModuleCode();
        Optional<Module> moduleOptional = moduleRepository.findById(moduleCode);
        if (moduleOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }

        // Set the Module to the ClassGroup
        classGroup.setModule(moduleOptional.get());

        // Save the new ClassGroup
        classGroupRepository.save(classGroup);
        return ExecutionStatus.SUCCESS;
    }
}
