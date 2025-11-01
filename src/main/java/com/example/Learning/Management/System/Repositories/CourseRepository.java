package com.example.Learning.Management.System.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Learning.Management.System.Models.Courses;

public interface CourseRepository  extends JpaRepository<Courses, Long> {
    // Query methods
    List<Courses> findByInstructor_UserId(Long instructorId); 
    List<Courses> findByApprovalStatus(String approvalStatus);
    long countByApprovalStatus(String approvalStatus);
}
