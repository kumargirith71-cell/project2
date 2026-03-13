package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.CourseRepository;
import com.example.Profenaa_touch.entity.Course;
import com.example.Profenaa_touch.entity.CourseCardDTO;
import com.example.Profenaa_touch.entity.CourseStatus;
import com.example.Profenaa_touch.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "*")
public class PublicCourseController {

    private final CourseService courseService;

    public PublicCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<CourseCardDTO> getPublishedCourses() {
        return courseService.getPublishedCourses();
    }

    @GetMapping("/course/{id}")
    public Course getFullCourse(@PathVariable Long id) {
        return courseService.getFullCourse(id);
    }


}

