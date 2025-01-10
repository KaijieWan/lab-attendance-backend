package com.example.lab_attendance_app.models.dto;

import com.example.lab_attendance_app.models.entities.Module;

import java.io.Serializable;
import java.time.Instant;

public class ModuleCreationDTO implements Serializable {
    private String moduleCode;

    public ModuleCreationDTO() {
    }

    public ModuleCreationDTO(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Module toEntity() {
        return new Module().setModuleCode(moduleCode);
    }

    public String getModuleCode() {return moduleCode;}

    public ModuleCreationDTO setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }
}
