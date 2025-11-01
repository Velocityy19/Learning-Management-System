package com.example.Learning.Management.System.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Learning.Management.System.Models.Users;

public interface UserRepository extends JpaRepository<Users, Long> { 
        Users findByUserEmail(String userEmail);        // Find user by email
}
