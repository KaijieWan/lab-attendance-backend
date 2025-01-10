package com.example.lab_attendance_app.models.entities;

import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import com.example.lab_attendance_app.models.entities.Module;
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
@Table(name = "class_group")
@Accessors(chain = true)
public class ClassGroup {
    @EmbeddedId
    private ClassGroupId classGroupId;

    @ManyToOne
    @MapsId("moduleCode")
    @JoinColumn(name = "module_code")
    private Module module;

    @OneToMany(mappedBy = "classGroup")
    Set<Student_Enrolled_ClassGroup> studentEnrolledClassGroups;
}
