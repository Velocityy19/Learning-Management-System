package com.example.Learning.Management.System.Controllers;

import com.example.Learning.Management.System.Models.Courses;
import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Models.Enrollment;
import com.example.Learning.Management.System.Service.CourseService;
import com.example.Learning.Management.System.Service.EnrollmentService;
import com.example.Learning.Management.System.Service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UsersService usersService;

    // Student Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("STUDENT")) {
            return "redirect:/login";
        }

        // Get enrolled courses
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(user.getUserId());
        List<CourseDTO> enrolledCourses = new ArrayList<>();
        
        int totalProgress = 0;
        int completedCourses = 0;
        
        for (Enrollment enrollment : enrollments) {
            CourseDTO dto = new CourseDTO();
            dto.setCourseId(enrollment.getCourse().getCourseId());
            dto.setCourseName(enrollment.getCourse().getCourseName());
            dto.setCourseDescription(enrollment.getCourse().getCourseDescription());
            
            // Calculate progress (you can adjust this based on your actual progress tracking)
            int progress = calculateCourseProgress(enrollment);
            dto.setProgress(progress);
            
            totalProgress += progress;
            if (progress == 100) {
                completedCourses++;
            }
            
            enrolledCourses.add(dto);
        }

        // Calculate overall progress
        int overallProgress = enrolledCourses.isEmpty() ? 0 : totalProgress / enrolledCourses.size();
        int inProgressCourses = enrolledCourses.size() - completedCourses;

        // Get available courses (only APPROVED courses that student hasn't enrolled in)
        List<Courses> allCourses = courseService.getCoursesByStatus("APPROVED");
        List<Long> enrolledCourseIds = enrollments.stream()
                .map(e -> e.getCourse().getCourseId())
                .collect(Collectors.toList());
        
        List<Courses> availableCourses = allCourses.stream()
                .filter(c -> !enrolledCourseIds.contains(c.getCourseId()))
                .collect(Collectors.toList());

        // Generate recent activities
        List<ActivityDTO> recentActivities = generateRecentActivities(enrollments);
        
        // Generate upcoming events (placeholder for now)
        List<EventDTO> upcomingEvents = generateUpcomingEvents();

        // Add all attributes to model
        model.addAttribute("user", user);
        model.addAttribute("totalEnrolled", enrolledCourses.size());
        model.addAttribute("completedCourses", completedCourses);
        model.addAttribute("inProgressCourses", inProgressCourses);
        model.addAttribute("availableCoursesCount", availableCourses.size());
        model.addAttribute("overallProgress", overallProgress);
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("availableCourses", availableCourses);
        model.addAttribute("recentActivities", recentActivities);
        model.addAttribute("upcomingEvents", upcomingEvents);

        return "student-dashboard";
    }

    // Enroll in a course
    @PostMapping("/enroll")
    public String enrollInCourse(@RequestParam Long courseId, 
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("STUDENT")) {
            return "redirect:/login";
        }

        try {
            enrollmentService.enrollStudent(user.getUserId(), courseId);
            redirectAttributes.addFlashAttribute("success", "Successfully enrolled in the course!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to enroll: " + e.getMessage());
        }

        return "redirect:/student/dashboard";
    }

    // Helper method to calculate course progress
    private int calculateCourseProgress(Enrollment enrollment) {
        // Check if enrollmentDate is null (for old records)
        if (enrollment.getEnrollmentDate() == null) {
            // Return progress from database if available
            if (enrollment.getProgress() != null) {
                return enrollment.getProgress().intValue();
            }
            return 0; // Default to 0 if no data
        }
        
        // Calculate progress based on enrollment date
        long daysEnrolled = ChronoUnit.DAYS.between(enrollment.getEnrollmentDate(), LocalDateTime.now());
        
        if (daysEnrolled > 30) return 100;
        if (daysEnrolled > 20) return 75;
        if (daysEnrolled > 10) return 50;
        if (daysEnrolled > 5) return 25;
        return 10;
    }

    // Generate recent activities
    private List<ActivityDTO> generateRecentActivities(List<Enrollment> enrollments) {
        List<ActivityDTO> activities = new ArrayList<>();
        
        // Filter out enrollments with null dates first, then sort
        List<Enrollment> validEnrollments = enrollments.stream()
            .filter(e -> e.getEnrollmentDate() != null) // Remove null dates
            .sorted((e1, e2) -> e2.getEnrollmentDate().compareTo(e1.getEnrollmentDate()))
            .limit(4)
            .toList();
        
        for (Enrollment enrollment : validEnrollments) {
            ActivityDTO activity = new ActivityDTO();
            
            int progress = calculateCourseProgress(enrollment);
            
            if (progress == 100) {
                activity.setIcon("✅");
                activity.setTitle("Completed " + enrollment.getCourse().getCourseName());
            } else if (progress > 50) {
                activity.setIcon("📖");
                activity.setTitle("Progress in " + enrollment.getCourse().getCourseName());
            } else {
                activity.setIcon("🎯");
                activity.setTitle("Started " + enrollment.getCourse().getCourseName());
            }
            
            activity.setTimeAgo(getTimeAgo(enrollment.getEnrollmentDate()));
            activities.add(activity);
        }
        
        return activities;
    }

    // Generate upcoming events (placeholder)
    private List<EventDTO> generateUpcomingEvents() {
        List<EventDTO> events = new ArrayList<>();
        // This is placeholder data. You can implement actual events from database
        return events; // Return empty for now
    }

    // Helper method to format time ago
    private String getTimeAgo(LocalDateTime dateTime) {
        long minutes = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
        
        if (minutes < 60) return minutes + " minutes ago";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        
        long days = hours / 24;
        if (days < 30) return days + " day" + (days > 1 ? "s" : "") + " ago";
        
        long months = days / 30;
        return months + " month" + (months > 1 ? "s" : "") + " ago";
    }

    // DTO Classes
    public static class CourseDTO {
        private Long courseId;
        private String courseName;
        private String courseDescription;
        private Integer progress;

        // Getters and Setters
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getCourseDescription() { return courseDescription; }
        public void setCourseDescription(String courseDescription) { this.courseDescription = courseDescription; }
        public Integer getProgress() { return progress; }
        public void setProgress(Integer progress) { this.progress = progress; }
    }

    public static class ActivityDTO {
        private String icon;
        private String title;
        private String timeAgo;

        // Getters and Setters
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getTimeAgo() { return timeAgo; }
        public void setTimeAgo(String timeAgo) { this.timeAgo = timeAgo; }
    }

    public static class EventDTO {
        private String title;
        private String dateTime;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDateTime() { return dateTime; }
        public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    }
}