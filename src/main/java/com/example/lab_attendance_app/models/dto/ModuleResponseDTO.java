package com.example.lab_attendance_app.models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ModuleResponseDTO implements Serializable {
    private String moduleCode;
    private List<ClassGroupDTO> classGroups;

    public ModuleResponseDTO() {
    }

    public ModuleResponseDTO(String moduleCode, List<ClassGroupDTO> classGroups) {
        this.moduleCode = moduleCode;
        this.classGroups = classGroups;
    }
}
