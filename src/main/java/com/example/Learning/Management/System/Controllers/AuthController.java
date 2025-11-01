package com.example.Learning.Management.System.Controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Repositories.RolesRepository;
import com.example.Learning.Management.System.Repositories.UserRepository;
import com.example.Learning.Management.System.Service.UsersService;

@Controller
public class AuthController {

    // Injecting UsersService to handle user-related operations
    @Autowired
    private UsersService usersService;

    // Injecting repositories and password encoder
    @Autowired
    UserRepository userRepository;

    // Injecting repositories and password encoder
    @Autowired
    PasswordEncoder passwordEncoder;

    // Injecting RolesRepository
    @Autowired
    RolesRepository rolesRepository;

    // Home page
    @GetMapping( "/")
    public String redirectHome() {
        return "home";
    }

    // Home page
    @GetMapping( "/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // User Registration
    @PostMapping("/register")
    public String register(@ModelAttribute Users user, Model model) {
        try {
            usersService.registerUser(user);
            model.addAttribute("message", "Registration successful! Please login.");
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/home?error=true";
        }
    }

    // Password Encoding Endpoint for Testing
    @GetMapping("/encode-password")
    public ResponseEntity<String> encodePassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        return ResponseEntity.ok("Encoded password: " + encoded);
    }

    //Logout now handled by Spring Security
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/home?logout=true";
    }
}