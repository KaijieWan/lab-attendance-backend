package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.Lab;
import com.example.lab_attendance_app.models.entities.embedded.LabId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabRepository extends JpaRepository<Lab, LabId> {

}
