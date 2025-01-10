package com.example.lab_attendance_app.models.entities;

import com.example.lab_attendance_app.models.entities.embedded.StudentEnrolledClassGroupId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_enrolled_classgroup", indexes = {
        @Index(name = "idx_student_enrolled_semester_student", columnList = "semester_ID, student_Id"),
        @Index(name = "idx_semesterID", columnList = "semester_ID")
})
@Accessors(chain = true)
public class Student_Enrolled_ClassGroup {
    @EmbeddedId
    private StudentEnrolledClassGroupId id;

    @ManyToOne
    @MapsId("student_Id")
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "class_group_id", insertable = false, updatable = false),
            @JoinColumn(name = "module_code", insertable = false, updatable = false),
            @JoinColumn(name = "semester_id", insertable = false, updatable = false)
    })
    private ClassGroup classGroup;

}
