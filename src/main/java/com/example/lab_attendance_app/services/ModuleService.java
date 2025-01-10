package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.models.entities.Module;

import java.util.List;

public interface ModuleService {

    public Module createModule(Module module);

    public List<Module> getAllModules();
}
