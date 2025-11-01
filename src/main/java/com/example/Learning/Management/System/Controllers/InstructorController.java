package com.example.Learning.Management.System.Controllers;

import com.example.Learning.Management.System.Models.Courses;
import com.example.Learning.Management.System.Models.Lesson;
import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Repositories.LessonRepository;
import com.example.Learning.Management.System.Service.CourseService;
import com.example.Learning.Management.System.Service.EnrollmentService;
import com.example.Learning.Management.System.Service.LessonService;
import com.example.Learning.Management.System.Service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InstructorController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UsersService usersService;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    private LessonService   lessonService;

    // Instructor Dashboard
    @GetMapping("/instructor/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("INSTRUCTOR")) {
            return "redirect:/login";
        }

        List<Courses> courses = courseService.getCoursesByInstructor(user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("courses", courses);
        model.addAttribute("totalCourses", courses.size());

        // Count of pending approval courses
        long pendingCount = courses.stream()
            .filter(c -> "PENDING".equals(c.getApprovalStatus()))
            .count();
        model.addAttribute("pendingCoursesCount", pendingCount);

        int totalStudents = 0;
        Map<Long, Integer> enrollmentCounts = new HashMap<>();

        for (Courses course : courses) {
            // Only count students for APPROVED courses
            if ("APPROVED".equals(course.getApprovalStatus())) {
                int count = enrollmentService.getEnrollmentCountByCourse(course.getCourseId());
                totalStudents += count;
                enrollmentCounts.put(course.getCourseId(), count);
            }
        }

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("enrollmentCounts", enrollmentCounts);

        return "instructor-dashboard";
    }

    // Show Create Course Form
    @GetMapping("/instructor/courses/create")
    public String showCreateCourseForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equals("INSTRUCTOR")) {
            return "redirect:/login";
        }

        model.addAttribute("course", new Courses());
        return "instructor/create-course";
    }

    // Create New Course
    @PostMapping("/instructor/courses/create")
public String createCourse(@ModelAttribute Courses course,
                           Authentication authentication,  
                           RedirectAttributes redirectAttributes) {
    
    String email = authentication.getName();
    Users user = usersService.getUserByEmail(email);
    
    if (user == null) {
        redirectAttributes.addFlashAttribute("error", "User not found!");
        return "redirect:/login";
    }

    course.setInstructor(user);
    course.setApprovalStatus("PENDING");
    
    // DEBUG - BEFORE SAVE
    System.out.println("=== BEFORE SAVING COURSE ===");
    System.out.println("Course Name: " + course.getCourseName());
    System.out.println("Approval Status: " + course.getApprovalStatus());
    System.out.println("Instructor: " + user.getUserEmail());
    
    Courses savedCourse = courseService.createCourse(course);
    
    // DEBUG - AFTER SAVE
    System.out.println("=== AFTER SAVING COURSE ===");
    System.out.println("Saved Course ID: " + savedCourse.getCourseId());
    System.out.println("Saved Approval Status: " + savedCourse.getApprovalStatus());
    System.out.println("========================");
    
    redirectAttributes.addFlashAttribute("success", "Course submitted for approval!");

    return "redirect:/instructor/dashboard";
}

    // Show Edit Course Form
    @GetMapping("/instructor/courses/edit/{id}")
    public String showEditCourseForm(@PathVariable Long id,
                                     Authentication authentication,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("INSTRUCTOR")) {
            return "redirect:/login";
        }

        Courses course = courseService.getCourseById(id);

        if (course == null || !course.getInstructor().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this course");
            return "redirect:/instructor/dashboard";
        }

        model.addAttribute("course", course);
        return "instructor/edit-course";
    }

    // Update Course
    @PostMapping("/instructor/courses/edit/{id}")
    public String updateCourse(@PathVariable Long id,
                               @ModelAttribute Courses course,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("INSTRUCTOR")) {
            return "redirect:/login";
        }

        Courses existingCourse = courseService.getCourseById(id);

        if (existingCourse == null || !existingCourse.getInstructor().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this course");
            return "redirect:/instructor/dashboard";
        }

        existingCourse.setCourseName(course.getCourseName());
        existingCourse.setCourseDescription(course.getCourseDescription());
        courseService.createCourse(existingCourse);

        redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
        return "redirect:/instructor/dashboard";
    }

    // Delete Course
    @PostMapping("/instructor/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("INSTRUCTOR")) {
            return "redirect:/login";
        }

        Courses course = courseService.getCourseById(id);

        if (course == null || !course.getInstructor().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this course");
            return "redirect:/instructor/dashboard";
        }

        courseService.deleteCourse(id);
        redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
        return "redirect:/instructor/dashboard";
    }

    // View Course Details
    @GetMapping("/instructor/courses/{id}")
    public String viewCourseDetails(@PathVariable Long id,
                                   Authentication authentication,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);

        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("INSTRUCTOR")) {
            return "redirect:/login";
        }

        Courses course = courseService.getCourseById(id);

        if (course == null || !course.getInstructor().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to view this course");
            return "redirect:/instructor/dashboard";
        }

        model.addAttribute("course", course);
        model.addAttribute("enrolledStudents", enrollmentService.getEnrolledStudentsByCourse(id));
        model.addAttribute("enrollmentCount", enrollmentService.getEnrollmentCountByCourse(id));

        return "instructor/course-details";
    }

    // Show add lessons page
    @GetMapping("/instructor/add-lessons")  
    public String addLessons(@RequestParam Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "add-lessons";
    }

    // API endpoint to save lesson (called from JavaScript)
    @PostMapping("/instructor/api/lessons")  
    @ResponseBody
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        Lesson savedLesson = lessonService.createLesson(lesson);
        return ResponseEntity.ok(savedLesson);
    }

    // API endpoint to get lessons for a course
    @GetMapping("/instructor/api/lessons/course/{courseId}")  
    @ResponseBody
    public ResponseEntity<List<Lesson>> getLessonsByCourse(@PathVariable Long courseId) {
        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }
}
