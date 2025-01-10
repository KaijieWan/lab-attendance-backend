package com.example.lab_attendance_app.models.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id", nullable = false)
    private UUID role_id;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "permissionType")
    private String permissionType;

    @Column(name = "actions")
    private String actions; // Comma-separated actions

    public RolePermission() {
    }

    public RolePermission(UUID role_id, String role, String permissionType, String actions) {
        this.role = role;
        this.permissionType = permissionType;
        this.actions = actions;
        this.role_id = role_id;
    }

    public UUID getId() {
        return role_id;
    }

    public void setId(UUID id) {
        this.role_id = id;
    }

    public String getRole() {
        return role;
    }

    public RolePermission setRole(String role) {
        this.role = role;
        return this;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public RolePermission setPermissionType(String permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    public String getActions() {
        return actions;
    }

    public RolePermission setActions(String actions) {
        this.actions = actions;
        return this;
    }
}
