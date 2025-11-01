package com.example.Learning.Management.System.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Learning.Management.System.Models.Roles;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    Roles findByRoleName(String roleName); // Find role by name
}
