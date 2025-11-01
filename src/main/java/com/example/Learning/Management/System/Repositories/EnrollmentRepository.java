package com.example.Learning.Management.System.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Learning.Management.System.Models.Enrollment;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    // Find all enrollments by student ID
    List<Enrollment> findByStudent_UserId(Long studentId);
    
    // Find all enrollments by course ID
    List<Enrollment> findByCourse_CourseId(Long courseId);
    
    // Check if a student is already enrolled in a course
    boolean existsByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    
    // Count enrollments by course ID
    long countByCourse_CourseId(Long courseId);
    
    // Find specific enrollment by student and course
    Enrollment findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
}