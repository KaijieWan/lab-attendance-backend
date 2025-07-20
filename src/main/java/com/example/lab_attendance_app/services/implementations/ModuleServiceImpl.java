package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.models.entities.Module;
import com.example.lab_attendance_app.models.repositories.ModuleRepository;
import com.example.lab_attendance_app.services.ModuleService;
import com.example.lab_attendance_app.services.UserService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {
    private static final Logger logger = LogManager.getLogger(ModuleServiceImpl.class);
    private final ModuleRepository moduleRepository;
    //private final UserService userService;

    public ModuleServiceImpl(ModuleRepository moduleRepository, UserService userService) {
        this.moduleRepository = moduleRepository;
    }

    public Module createModule(Module module){
        Optional<Module> existingModule = moduleRepository.findById(module.getModuleCode());

        if (existingModule.isPresent()) {
            return null;
        }

        return moduleRepository.save(module);
    }

    @Transactional
    public List<Module> getAllModules(){
        return moduleRepository.findAll();
    }
}
