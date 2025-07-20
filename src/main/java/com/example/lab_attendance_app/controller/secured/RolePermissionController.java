package com.example.lab_attendance_app.controller.secured;

import com.example.lab_attendance_app.controller.secured.user.UserController;
import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.MessageResponse;
import com.example.lab_attendance_app.models.dto.PermissionDTO;
import com.example.lab_attendance_app.models.dto.RoleCreationDTO;
import com.example.lab_attendance_app.models.entities.RolePermission;
import com.example.lab_attendance_app.models.entities.User;
import com.example.lab_attendance_app.models.repositories.RolePermissionRepository;
import com.example.lab_attendance_app.models.repositories.UserRepository;
import com.example.lab_attendance_app.services.RolePermissionService;
import com.example.lab_attendance_app.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/roles")
public class RolePermissionController {
    private static final Logger logger = LogManager.getLogger(RolePermissionController.class);
    private final RolePermissionService rolePermissionService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionController(RolePermissionService rolePermissionService, UserRepository userRepository, UserService userService, RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionService = rolePermissionService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @PostMapping("/createRole")
    public ResponseEntity<MessageResponse> createRole(@RequestBody RoleCreationDTO roleCreationDTO) {
        List<PermissionDTO> permissions = roleCreationDTO.getPermissions();
        List<RolePermission> createdPermissions = new ArrayList<>();

        if (rolePermissionService.findAllDistinctRoles().contains(roleCreationDTO.getRole())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "Role already exists",
                            ExecutionStatus.VALIDATION_ERROR
                    )
            );
        }

        for(PermissionDTO permission : permissions) {
            RolePermission createdRolePermission = rolePermissionService.createRolePermission(new RolePermission().setRole(roleCreationDTO.getRole())
                            .setReportsTo(roleCreationDTO.getReportsTo())
                    .setPermissionType(permission.getPermissionType())
                    .setActions(permission.getActions()));

        }


        return ResponseEntity.ok(
                new MessageResponse(
                        "Role with permissions created successfully",
                        ExecutionStatus.SUCCESS
                )
        );
    }

    @PutMapping("/updateRole")
    public ResponseEntity<MessageResponse> updateRole(@RequestBody RoleCreationDTO roleCreationDTO) {
        List<PermissionDTO> permissions = roleCreationDTO.getPermissions();
        List<ExecutionStatus> returnedStatus = new ArrayList<>();

        for(PermissionDTO permission : permissions) {
            ExecutionStatus status = rolePermissionService.updateRolePermission(new RolePermission().setRole(roleCreationDTO.getRole())
                            .setReportsTo(roleCreationDTO.getReportsTo())
                    .setPermissionType(permission.getPermissionType())
                    .setActions(permission.getActions()));

            returnedStatus.add(status);
        }

        if (returnedStatus.stream().anyMatch(status -> status == ExecutionStatus.FAILED)){
            return ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "Failed to update role with permissions.",
                            ExecutionStatus.VALIDATION_ERROR
                    )
            );
        }

        if (returnedStatus.stream().allMatch(status -> status == ExecutionStatus.SUCCESS)) {
            return ResponseEntity.ok(
                    new MessageResponse(
                            "All permissions updated successfully.",
                            ExecutionStatus.SUCCESS
                    )
            );
        }

        // Default response for partial success/failure
        return ResponseEntity.ok(
                new MessageResponse(
                        "Role with permissions partially updated.",
                        ExecutionStatus.SUCCESS
                )
        );
    }

    @DeleteMapping("/deleteRole")
    public ResponseEntity<MessageResponse> deleteRole(@RequestParam String role) {
        logger.info("Deleting role {}", role);
        ExecutionStatus status = rolePermissionService.deleteRolePermission(role);

        return switch (status) {
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "Role does not exist",
                            status
                    )
            );
            case SUCCESS -> ResponseEntity.ok(
                    new MessageResponse(
                            "Role deleted successfully",
                            status
                    )
            );
            case INVALID -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new MessageResponse(
                            "Role is assigned to one or more users and cannot be deleted",
                            status
                    )
            );
            default -> ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "Failed to delete role",
                            status
                    )
            );
        };
    }

    @GetMapping("/rolePermissions")
    public ResponseEntity<?> getRolePermissions(@RequestParam String role) {
        logger.info("Retrieving role permissions: {}", role);
        List<RolePermission> rolePermissions = rolePermissionService.getRolePermissions(role);

        // Role does not exist
        if (rolePermissions == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "Role does not exist",
                            ExecutionStatus.NOT_FOUND
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                rolePermissions
        );
    }

    @GetMapping("/distinctRoles")
    public ResponseEntity<?> getDistinctRoles(){
        logger.info("Retrieving distinct roles:");

        List<String> roles = rolePermissionService.findAllDistinctRoles();

        // Role does not exist
        if (roles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "No roles in database",
                            ExecutionStatus.INVALID
                    )
            );
        }
        // Success
        return ResponseEntity.ok(
                roles
        );
    }

}
