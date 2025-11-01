package com.example.Learning.Management.System.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Learning.Management.System.Models.Courses;
import com.example.Learning.Management.System.Models.Enrollment;
import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Repositories.CourseRepository;
import com.example.Learning.Management.System.Repositories.EnrollmentRepository;
import com.example.Learning.Management.System.Repositories.UserRepository;

@Service
public class EnrollmentService {
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Enroll student in a course
    public void enrollStudentInCourse(Long studentId, Long courseId) {
        Users student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
       
        Courses course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

        // Use repository method instead of stream
        boolean alreadyEnrolled = enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(studentId, courseId);
        
        if (alreadyEnrolled) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgress(0.0);
        enrollment.setStatus("ENROLLED");
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    // Get all courses a student is enrolled in
    public List<Courses> getEnrolledCourses(Long userId) {
        return enrollmentRepository.findAll().stream()
            .filter(enrollment -> enrollment.getStudent().getUserId().equals(userId))
            .map(Enrollment::getCourse)
            .collect(Collectors.toList());
    }

    // Get enrollment progress for a student in a course
    public Double getProgress(Long userId, Long courseId) {
        return enrollmentRepository.findAll().stream()
            .filter(enrollment -> 
                enrollment.getStudent().getUserId().equals(userId) && 
                enrollment.getCourse().getCourseId().equals(courseId))
            .map(Enrollment::getProgress)
            .findFirst()
            .orElse(0.0);
    }

    // Update progress
    public void updateProgress(Long userId, Long courseId, Double progress) {
        Enrollment enrollment = enrollmentRepository.findAll().stream()
            .filter(e -> 
                e.getStudent().getUserId().equals(userId) && 
                e.getCourse().getCourseId().equals(courseId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setProgress(progress);
        
        if (progress >= 100.0) {
            enrollment.setStatus("COMPLETED");
        }
        
        enrollmentRepository.save(enrollment);
    }

    // Check if student is enrolled
    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(userId, courseId);
    }

    // Get count of students enrolled in a course
    public int getEnrollmentCountByCourse(Long courseId) {
        return (int) enrollmentRepository.countByCourse_CourseId(courseId);
    }

    // Get all students enrolled in a specific course
    public List<Users> getEnrolledStudentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourse_CourseId(courseId).stream()
            .map(Enrollment::getStudent)
            .collect(Collectors.toList());
    }

    // Get all enrollments for a specific course (with progress)
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourse_CourseId(courseId);
    }

    // Get enrollments by student
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudent_UserId(studentId);
    }

    // Enroll student (simplified version for StudentController)
    public void enrollStudent(Long studentId, Long courseId) {
        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Courses course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Check if already enrolled
        if (enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(studentId, courseId)) {
            throw new RuntimeException("Already enrolled in this course");
        }
        
        // Check if course is approved
        if (!"APPROVED".equals(course.getApprovalStatus())) {
            throw new RuntimeException("Cannot enroll in courses that are not approved");
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgress(0.0);
        enrollment.setStatus("ENROLLED");
        enrollmentRepository.save(enrollment);
    }
}