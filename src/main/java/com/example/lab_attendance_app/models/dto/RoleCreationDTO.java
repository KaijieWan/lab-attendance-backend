package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.RolePermission;
import com.example.lab_attendance_app.models.entities.User;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class RoleCreationDTO implements Serializable {
    @NotBlank(message = "Role title is required")
    private String role;
    private String reportsTo;
    private List<PermissionDTO> permissions;

    public RoleCreationDTO() {
    }

    public RolePermission toEntity() {
        return new RolePermission()
                .setRole(role)
                .setReportsTo(reportsTo);
    }

    public @NotBlank(message = "Role title is required") String getRole() {return role;}

    public RoleCreationDTO setRole(@NotBlank(message = "Role title is required") String role) {
        this.role = role;
        return this;
    }

    public String getReportsTo() {return reportsTo;}

    public RoleCreationDTO setReportTo(String reportsTo) {
        this.reportsTo = reportsTo;
        return this;
    }

    public List<PermissionDTO> getPermissions() {return permissions;}

    public RoleCreationDTO setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
        return this;
    }


}
