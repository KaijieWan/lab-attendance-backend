package com.example.lab_attendance_app.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "module")
@Accessors(chain = true)
public class Module {
    @Id
    @Column(name = "module_code", nullable = false, unique = true)
    private String ModuleCode;

    @OneToMany(mappedBy = "module")
    @JsonIgnore
    Set<ClassGroup> classGroups;

}
