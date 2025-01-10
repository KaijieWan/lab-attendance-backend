package com.example.lab_attendance_app.models.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class PermissionDTO implements Serializable {
    @NotBlank(message = "Permission type is required")
    private String permissionType;
    private String actions;

    public @NotBlank(message = "Permission type is required") String getPermissionType() {return permissionType;}

    public PermissionDTO setPermissionType(@NotBlank(message = "Permission type is required") String permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    public String getActions() {return actions;}

    public PermissionDTO setActions(String actions) {
        this.actions = actions;
        return this;
    }
}
