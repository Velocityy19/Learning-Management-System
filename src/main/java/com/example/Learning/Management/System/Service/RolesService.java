package com.example.Learning.Management.System.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Learning.Management.System.Models.Roles;
import com.example.Learning.Management.System.Repositories.RolesRepository;
import com.example.Learning.Management.System.Repositories.UserRepository;

@Service
public class RolesService {
    
    @Autowired
    // Role Repository injection
    private RolesRepository rolesRepository;

    // User Repository injection
    @Autowired
    private UserRepository userRepository;

    public void assignRoleToUser(Long roleId, Long userId) {
        
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        var role = rolesRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);
        userRepository.save(user);
    }

    // Create a new role
    public void createRole(String roleName) {
        Roles role = new Roles();
        role.setRoleName(roleName);
        rolesRepository.save(role);
    }

    // Delete a role
    public void deleteRole(Long roleId) {
        rolesRepository.deleteById(roleId);
    }
    
    // Get role by ID
    public Roles getRoleById(Long roleId) {
        return rolesRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    //Get a role by Name
    public Roles getRoleByName(String roleName) {
    return rolesRepository.findByRoleName(roleName);
}

    // Get all roles
    public List<Roles> getAllRoles() {
        return rolesRepository.findAll();
    }

    //save role
    public Roles saveRole(Roles role) {
        return rolesRepository.save(role);
    }


}