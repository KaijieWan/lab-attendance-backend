package com.example.lab_attendance_app.models.entities;

import com.example.lab_attendance_app.models.dto.StudentDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student", indexes = {
        @Index(name = "idx_studentID", columnList = "Student_ID")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Student {
    @Id
    private String Student_ID;

    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    @OneToMany(mappedBy = "student")
    Set<Student_Enrolled_ClassGroup> studentEnrolledClassGroupSet;


    public String getStudentID() {
        return Student_ID;
    }

    public Student setStudentID(String Student_ID) {
        this.Student_ID = Student_ID;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public Student setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

}
