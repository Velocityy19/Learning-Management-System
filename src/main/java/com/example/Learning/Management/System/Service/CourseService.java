package com.example.Learning.Management.System.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Learning.Management.System.Models.Courses;
import com.example.Learning.Management.System.Repositories.CourseRepository;
import com.example.Learning.Management.System.Repositories.EnrollmentRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // Get all courses
    public List<Courses> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get course by ID
    public Courses getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    // Get available courses (not enrolled by this student)
    public List<Courses> getAvailableCourses(Long userId) {
        List<Courses> allCourses = courseRepository.findAll();
        
        // Get list of enrolled course IDs
        List<Long> enrolledCourseIds = enrollmentRepository.findAll().stream()
            .filter(enrollment -> enrollment.getStudent().getUserId().equals(userId))
            .map(enrollment -> enrollment.getCourse().getCourseId())
            .collect(Collectors.toList());

        //Filter out enrolled courses
        return allCourses.stream()
            .filter(course -> !enrolledCourseIds.contains(course.getCourseId()))
            .collect(Collectors.toList());
    }

    // Create a new course
    public Courses createCourse(Courses course) {
    if (course.getCourseId() == null && course.getApprovalStatus() == null) {
        course.setApprovalStatus("PENDING");
    }
    return courseRepository.save(course);
}

     // Get courses by approval status
    public List<Courses> getCoursesByStatus(String status) {
        return courseRepository.findByApprovalStatus(status);
    }
    
    // Get pending courses count
    public long getPendingCoursesCount() {
        return courseRepository.countByApprovalStatus("PENDING");
    }
    
    // Get approved courses count
    public long getApprovedCoursesCount() {
        return courseRepository.countByApprovalStatus("APPROVED");
    }
    
    // Approve course
    public void approveCourse(Long courseId) {
        Courses course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setApprovalStatus("APPROVED");
            courseRepository.save(course);
        }
    }
    
    // Reject course
    public void rejectCourse(Long courseId) {
        Courses course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setApprovalStatus("REJECTED");
            courseRepository.save(course);
        }
    }

    // Get courses by instructor
    public List<Courses> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findAll().stream()
            .filter(course -> course.getInstructor() != null && 
                    course.getInstructor().getUserId().equals(instructorId))
            .collect(Collectors.toList());
    }

    // Delete course By Id
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    //Update a course
    public Courses updateCourse(Courses course){
        return courseRepository.save(course);
    }

}