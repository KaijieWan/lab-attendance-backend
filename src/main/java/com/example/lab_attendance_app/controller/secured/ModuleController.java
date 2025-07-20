package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.MessageResponse;
import com.example.lab_attendance_app.models.dto.ModuleCreationDTO;
import com.example.lab_attendance_app.models.dto.ModuleResponseDTO;
import com.example.lab_attendance_app.models.entities.Module;
import com.example.lab_attendance_app.models.repositories.LabSessionRepository;
import com.example.lab_attendance_app.models.repositories.ModuleRepository;
import com.example.lab_attendance_app.services.ModuleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/module")
public class ModuleController {
    private static final Logger logger = LogManager.getLogger(ModuleController.class);
    private final ModuleRepository moduleRepository;
    private final ModuleService moduleService;
    private final LabSessionRepository labSessionRepository;

    public ModuleController(ModuleRepository moduleRepository, ModuleService moduleService, LabSessionRepository labSessionRepository) {
        this.moduleRepository = moduleRepository;
        this.moduleService = moduleService;
        this.labSessionRepository = labSessionRepository;
    }

    /**
     * Retrieves a list of all modules.
     *
     * @return A list of all modules from the repository.
     */
    @GetMapping("")
    public ResponseEntity<?> getAllModules() {
        logger.info("Retrieving all modules:");

        List<Module> modules = moduleService.getAllModules();

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
     * Retrieves a list of all module codes.
     *
     * @return A list of all module codes from the repository.
     */
    @GetMapping("/getModuleCodes")
    public List<String> getAllModulesCode() {
        return labSessionRepository.findModuleCodes();
    }


    /**
     * Creates a new module if it does not already exist.
     *
     * @param moduleCreationDTO The module object to be created.
     * @return ResponseEntity with a success message if the module is created,
     * or a conflict message if the module already exists.
     */
    @PostMapping("/createNewModule")
    public ResponseEntity<MessageResponse> createNewModule(@RequestBody ModuleCreationDTO moduleCreationDTO) {
        System.out.println(moduleCreationDTO);

        Module newModule = moduleService.createModule(moduleCreationDTO.toEntity());
        if (newModule != null) {
            return ResponseEntity.ok(new MessageResponse(
                    "Module created successfully",
                    ExecutionStatus.SUCCESS
            ));
        }
        else{
            return ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "Module already exists",
                            ExecutionStatus.VALIDATION_ERROR
                    )
            );
        }
    }
}
