package com.example.Learning.Management.System.Service;

import com.example.Learning.Management.System.Models.Lesson;
import com.example.Learning.Management.System.Repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    // Save a new lesson
    public Lesson createLesson(Lesson lesson) {
        lesson.setCreatedAt(LocalDateTime.now());
        return lessonRepository.save(lesson);
    }

    // Get all lessons for a course
    public List<Lesson> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    // Delete a lesson
    public void deleteLesson(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }
}