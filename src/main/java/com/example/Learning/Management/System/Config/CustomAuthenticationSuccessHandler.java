package com.example.Learning.Management.System.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Repositories.UserRepository;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private UserRepository userRepository;  

   @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                   HttpServletResponse response,
                                   Authentication authentication) throws IOException, ServletException {
    
    String email = authentication.getName();
    Users user = userRepository.findByUserEmail(email);
    request.getSession().setAttribute("user", user);
    
    String redirectUrl = "/home";
    
    for (GrantedAuthority authority : authentication.getAuthorities()) {
        String role = authority.getAuthority();
        
        // Determine redirect URL based on role
        if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
            redirectUrl = "/admin/dashboard";  
            break;
        } else if (role.equals("INSTRUCTOR") || role.equals("ROLE_INSTRUCTOR")) {
            redirectUrl = "/instructor/dashboard";  
            break;
        } else if (role.equals("STUDENT") || role.equals("ROLE_STUDENT")) {
            redirectUrl = "/student/dashboard";  
            break;
        }
    }
    
    System.out.println("=== LOGIN SUCCESS ===");
    System.out.println("User: " + email);
    System.out.println("Role: " + authentication.getAuthorities());
    System.out.println("Redirecting to: " + redirectUrl);
    System.out.println("========================");
    
    response.sendRedirect(redirectUrl);
}
}