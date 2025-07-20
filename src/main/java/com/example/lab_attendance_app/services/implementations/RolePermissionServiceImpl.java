package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.RolePermission;
import com.example.lab_attendance_app.models.entities.User;
import com.example.lab_attendance_app.models.repositories.RolePermissionRepository;
import com.example.lab_attendance_app.models.repositories.UserRepository;
import com.example.lab_attendance_app.services.RolePermissionService;
import com.example.lab_attendance_app.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.*;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    private static final Logger logger = LogManager.getLogger(RolePermissionServiceImpl.class);
    private final RolePermissionRepository rolePermissionRepository;
    private final UserService userService;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository, UserService userService) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.userService = userService;
    }

    public RolePermission createRolePermission(RolePermission rolePermission) {
        List <RolePermission> existingRolePermissions =  rolePermissionRepository.findByRole(rolePermission.getRole());

        if(existingRolePermissions.isEmpty()){
            return rolePermissionRepository.save(rolePermission);
        }

        if(!existingRolePermissions.isEmpty()){
            for(RolePermission existingRolePermission : existingRolePermissions){
                if(rolePermission.getPermissionType().equalsIgnoreCase(existingRolePermission.getPermissionType())){
                    logger.debug("RolePermission with id {} already exists.", existingRolePermission.getId());
                    return null;
                }
            }
        }

        return rolePermissionRepository.save(rolePermission);
    }

    public List<RolePermission> findByRole(String role){
        return rolePermissionRepository.findByRole(role);
    }

    public List<String> findAllDistinctRoles(){
        return rolePermissionRepository.findAllDistinctRoles();
    }

    public List<RolePermission> getRolePermissions(String role){
        List<RolePermission> permissions = rolePermissionRepository.findByRole(role);
        if (permissions.isEmpty()) {
            return null;
        }
        return permissions;
    }

    public ExecutionStatus updateRolePermission(RolePermission rolePermission){
        RolePermission existingRolePermission = rolePermissionRepository.findByRoleAndPermissionType(rolePermission.getRole(), rolePermission.getPermissionType()).orElse(null);

        if(existingRolePermission != null){
            existingRolePermission.setActions(rolePermission.getActions());
            existingRolePermission.setReportsTo(rolePermission.getReportsTo());
            rolePermissionRepository.save(existingRolePermission);
            logger.debug("RolePermission {} permission actions has been updated.", existingRolePermission.getId());
            logger.debug("RolePermission {} reporting to has been updated.", existingRolePermission.getId());
            return ExecutionStatus.SUCCESS;
        }else if(existingRolePermission == null){
            rolePermissionRepository.save(rolePermission);
            logger.debug("New RolePermission has been added since not existing.");
            return ExecutionStatus.SUCCESS;
        }

        return ExecutionStatus.FAILED;
    }

    public ExecutionStatus deleteRolePermission(String role){
        logger.info("delete service called");
        if(rolePermissionRepository.findByRole(role).isEmpty()){
            logger.info("delete service: not found");
            return ExecutionStatus.NOT_FOUND;
        }

        Page<User> userPage = userService.searchUsersByRole(role, 0, 10);
        if (!userPage.getContent().isEmpty()) {
            logger.info("delete service: assigned to users");
            return ExecutionStatus.INVALID;
        }

        rolePermissionRepository.deleteByRole(role);
        return ExecutionStatus.SUCCESS;
    }

    public ExecutionStatus addPermission(UUID roleId, String newPermission) {
        Optional<RolePermission> rolePermissionOptional = rolePermissionRepository.findById(roleId);
        RolePermission rolePermission = rolePermissionOptional.orElse(null);

        if(rolePermission != null){
            return ExecutionStatus.FAILED;
        }

        String actions = rolePermission.getActions();

        if (!actions.contains(newPermission)) {
            rolePermission.setActions(actions.isEmpty() ? newPermission : actions + "," + newPermission);
            rolePermissionRepository.save(rolePermission);
        }
        return ExecutionStatus.SUCCESS;
    }

    public boolean hasPermission(UUID roleId, String permission) {
        Optional<RolePermission> rolePermissionOptional = rolePermissionRepository.findById(roleId);
        // Check if RolePermission exists and its actions are not empty
        if (rolePermissionOptional.isPresent()) {
            RolePermission rolePermission = rolePermissionOptional.get();
            String actions = rolePermission.getActions();

            // Check if actions is not null or empty and contains the permission
            return actions != null && !actions.isEmpty() &&
                    Arrays.asList(actions.split(",")).contains(permission);
        }

        // If rolePermission doesn't exist, return false
        return false;
    }

    public ExecutionStatus removePermission(UUID roleId, String permissionToRemove) {
        Optional<RolePermission> rolePermissionOptional = rolePermissionRepository.findById(roleId);
        RolePermission rolePermission = rolePermissionOptional.orElse(null);

        if(rolePermission != null){
            return ExecutionStatus.FAILED;
        }

        List<String> actionsList = new ArrayList<>(Arrays.asList(rolePermission.getActions().split(",")));
        if (actionsList.remove(permissionToRemove)) {
            rolePermission.setActions(String.join(",", actionsList));
            rolePermissionRepository.save(rolePermission);
        }
        return ExecutionStatus.SUCCESS;
    }

    public List<RolePermission> findPermissionsByAction(String action) {
        List<RolePermission> allPermissions = rolePermissionRepository.findAll();
        return allPermissions.stream()
                .filter(rp -> rp.getActions().contains(action))
                .toList();
    }
}
