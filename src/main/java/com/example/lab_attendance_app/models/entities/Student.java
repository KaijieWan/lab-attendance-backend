package com.example.lab_attendance_app.models.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "Student_ID")
public class Student {
    @Id
    private String Student_ID;

    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    Set<Student_Enrolled_ClassGroup> studentEnrolledClassGroupSet;


    public String getStudent_ID() {
        return Student_ID;
    }

    public Student setStudent_ID(String Student_ID) {
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
