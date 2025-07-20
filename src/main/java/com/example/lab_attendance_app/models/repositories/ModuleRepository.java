package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, String> {
    @Query("SELECT r.ModuleCode FROM Module r ORDER BY r.ModuleCode")
    List<String> findModuleCodes();

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.classGroups")
    List<Module> findAllWithClassGroups();
}
