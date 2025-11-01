package com.example.Learning.Management.System.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity (name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Courses {
    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated ID
    private Long courseId;

    private String courseName;
    private String courseDescription;


    //Instructor who created the course
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Users instructor;

    @Transient // This won't be stored in DB
    private Double progressPercentage;

    public Double getProgressPercentage() {
        return progressPercentage;  // Return the transient field
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage; // Set the transient field
    }

     @Column(nullable = false)
    private String approvalStatus = "PENDING";  // "PENDING", "APPROVED", "REJECTED"

}
