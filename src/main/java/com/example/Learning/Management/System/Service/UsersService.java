package com.example.Learning.Management.System.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Learning.Management.System.Models.Roles;
import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Repositories.RolesRepository;
import com.example.Learning.Management.System.Repositories.UserRepository;

@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolesRepository rolesRepository;
    
    // Create new User
    public void createUser(Users user) {
       user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        userRepository.save(user);

    }

    public Users registerUser(Users user) {
    
    // Check if user already exists
    if (userRepository.findByUserEmail(user.getUserEmail()) != null) {
        throw new RuntimeException("User with this email already exists");
    }

    // Encode password before saving
    user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
    
    // Set default role (STUDENT)
    Roles studentRole = rolesRepository.findByRoleName("STUDENT");
    user.setRole(studentRole);
    Users savedUser = userRepository.save(user);
    return savedUser;
}

    // Get User by ID
    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get User by Email
    public Users getUserByEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }

    // Get User password by Email
    public String getUserPassword(String userEmail) {
        Users user = userRepository.findByUserEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user.getUserPassword();
    }

    // Delete User
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // Get all Users
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // Update User
    public Users updateUser(String userName, String userEmail, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserName(userName);
        user.setUserEmail(userEmail);
        return userRepository.save(user);
    }

   @Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Users user = userRepository.findByUserEmail(email);

    if (user == null) {
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    if (user.getRole() == null) {
        throw new UsernameNotFoundException("User has no role assigned");
    }

    // ✅ Prefix with ROLE_
    String roleName = user.getRole().getRoleName();
    if (!roleName.startsWith("ROLE_")) {
        roleName = "ROLE_" + roleName;
    }

    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(roleName));

    return new User(user.getUserEmail(), user.getUserPassword(), authorities);
}


}
