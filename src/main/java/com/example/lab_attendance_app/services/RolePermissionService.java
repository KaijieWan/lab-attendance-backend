package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.RolePermission;

import java.util.*;

public interface RolePermissionService {

    public RolePermission createRolePermission(RolePermission rolePermission);

    public ExecutionStatus updateRolePermission(RolePermission rolePermission);

    public ExecutionStatus deleteRolePermission(String role);

    public List<RolePermission> findByRole(String role);

    public List<String> findAllDistinctRoles();

    public List<RolePermission> getRolePermissions(String role);

    public ExecutionStatus addPermission(UUID roleId, String newPermission);

    public boolean hasPermission(UUID roleId, String permission);

    public ExecutionStatus removePermission(UUID roleId, String permissionToRemove);

    public List<RolePermission> findPermissionsByAction(String action);
}
