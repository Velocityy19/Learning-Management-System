package com.example.Learning.Management.System.Controllers;

import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Models.Courses;
import com.example.Learning.Management.System.Models.Roles;
import com.example.Learning.Management.System.Service.UsersService;
import com.example.Learning.Management.System.Service.CourseService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private CourseService courseService;

    // Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
    String email = authentication.getName();
    Users user = usersService.getUserByEmail(email);
    
    System.out.println("=== ADMIN DASHBOARD DEBUG ===");
    System.out.println("Admin email: " + email);
    System.out.println("Admin user: " + (user != null ? user.getUserName() : "NULL"));
    
    if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
        System.out.println("Not admin or user is null");
        return "redirect:/home";
    }

    // Get all data
    List<Users> allUsers = usersService.getAllUsers();
    List<Courses> allCourses = courseService.getAllCourses();

    System.out.println("Total users in DB: " + allUsers.size());
    System.out.println("Total courses in DB: " + allCourses.size());
    
    // Print all courses
    System.out.println("=== ALL COURSES ===");
    for (Courses c : allCourses) {
        System.out.println("Course ID: " + c.getCourseId() + 
                         " | Name: " + c.getCourseName() + 
                         " | Status: " + c.getApprovalStatus() + 
                         " | Instructor: " + (c.getInstructor() != null ? c.getInstructor().getUserEmail() : "NULL"));
    }

    // Get pending courses for approval
    List<Courses> pendingCourses = courseService.getCoursesByStatus("PENDING");
    
    System.out.println("=== PENDING COURSES ===");
    System.out.println("Pending count: " + pendingCourses.size());
    for (Courses c : pendingCourses) {
        System.out.println("Pending Course: " + c.getCourseName() + " | Status: " + c.getApprovalStatus());
    }
    System.out.println("========================");
    
    model.addAttribute("pendingCourses", pendingCourses);
    model.addAttribute("pendingCoursesCount", (long) pendingCourses.size());

        // Get approved courses count
        long approvedCoursesCount = courseService.getApprovedCoursesCount();
        model.addAttribute("approvedCoursesCount", approvedCoursesCount);

        // Calculate stats
        long totalInstructors = allUsers.stream()
            .filter(u -> u.getRole() != null && u.getRole().getRoleName().equalsIgnoreCase("INSTRUCTOR"))
            .count();
            
        long totalStudents = allUsers.stream()
            .filter(u -> u.getRole() != null && u.getRole().getRoleName().equalsIgnoreCase("STUDENT"))
            .count();

        // Get all roles 
        List<Roles> allRoles = getAllRolesFromUsers(allUsers);

        // Count users per role
        Map<Long, Long> roleCounts = new HashMap<>();
        for (Roles role : allRoles) {
            long count = allUsers.stream()
                .filter(u -> u.getRole() != null && u.getRole().getRoleId().equals(role.getRoleId()))
                .count();
            roleCounts.put(role.getRoleId(), count);  
        }

        // Add data to model
        model.addAttribute("user", user);
        model.addAttribute("users", allUsers);
        model.addAttribute("courses", allCourses);
        model.addAttribute("roles", allRoles);
        model.addAttribute("roleCounts", roleCounts);
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalCourses", allCourses.size());
        model.addAttribute("totalInstructors", totalInstructors);
        model.addAttribute("totalStudents", totalStudents);

        return "admin-dashboard";
    }

    // Helper to get all distinct roles from users
    private List<Roles> getAllRolesFromUsers(List<Users> users) {
        return users.stream()
            .map(Users::getRole)
            .filter(role -> role != null)
            .distinct()
            .toList();
    }

    // Approve Course
    @PostMapping("/courses/approve/{id}")
    public String approveCourse(@PathVariable Long id, 
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);
        
        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return "redirect:/home";
        }
        
        courseService.approveCourse(id);
        redirectAttributes.addFlashAttribute("success", "Course approved successfully!");
        return "redirect:/admin/dashboard";
    }

    // Reject Course
    @PostMapping("/courses/reject/{id}")
    public String rejectCourse(@PathVariable Long id, 
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);
        
        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return "redirect:/home";
        }
        
        courseService.rejectCourse(id);
        redirectAttributes.addFlashAttribute("success", "Course rejected!");
        return "redirect:/admin/dashboard";
    }

    // Update User
    @PostMapping("/users/update")
    public String updateUser(@RequestParam Long userId,
                            @RequestParam String userName,
                            @RequestParam String userEmail,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);
        
        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return "redirect:/home";
        }

        try {
            usersService.updateUser(userName, userEmail, userId);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    // Delete User
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);
        
        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return "redirect:/home";
        }

        try {
            // Don't allow admin to delete themselves
            if (user.getUserId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "You cannot delete your own account!");
                return "redirect:/admin/dashboard";
            }

            usersService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    // Update Course
    @PostMapping("/courses/update")
    public String updateCourse(@RequestParam Long courseId,
                              @RequestParam String courseName,
                              @RequestParam String courseDescription,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);
        
        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return "redirect:/home";
        }

        try {
            Courses course = courseService.getCourseById(courseId);
            course.setCourseName(courseName);
            course.setCourseDescription(courseDescription);
            courseService.updateCourse(course);
            
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update course: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    // Delete Course
    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Users user = usersService.getUserByEmail(email);
        
        if (user == null || !user.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            return "redirect:/home";
        }

        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("success", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete course: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }
}