package com.example.Learning.Management.System.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity (name = "enrollments")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Enrollment {
    @Id   // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated ID
    private Long enrollmentId;
    
    // Student who enrolled in the course
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users student;

    // Course in which the student enrolled
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Courses course;

    private Double progress;
    private String status; // "ENROLLED", "COMPLETED"

    private LocalDateTime enrollmentDate;

}
