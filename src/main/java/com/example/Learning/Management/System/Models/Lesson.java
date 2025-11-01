package com.example.Learning.Management.System.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    
    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// Auto-generated ID
    private Long id;
    
    // Associated Feilds
    private Long courseId;
    private String title;
    private String description;
    private String type;
    private String fileUrl;
    private String fileName;
    private LocalDateTime createdAt;
}