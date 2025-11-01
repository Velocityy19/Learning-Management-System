package com.example.Learning.Management.System.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Learning.Management.System.Models.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
     List<Lesson> findByCourseId(Long courseId);    // Query method to find lessons by course ID
}
