package com.example.Profenaa_touch.service;

import com.example.Profenaa_touch.entity.*;
import com.example.Profenaa_touch.Repository.CourseRepository;
import com.example.Profenaa_touch.Repository.DepartmentRepository;
import com.example.Profenaa_touch.entity.Module;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final DepartmentRepository deptRepo;
    private final CourseImageStorageService imageService;

    public CourseService(CourseRepository c,
                         DepartmentRepository d,
                         CourseImageStorageService i) {
        courseRepo = c;
        deptRepo = d;
        imageService = i;
    }

    /* ====================================
       CREATE COURSE
    ==================================== */
    public Course create(Long deptId, String name, Double price,
                         MultipartFile image) throws Exception {

        Department dept = deptRepo.findById(deptId).orElseThrow();

        Course course = new Course();
        course.setName(name);
        course.setPrice(price);
        course.setDepartment(dept);
        course.setStatus(CourseStatus.DRAFT);

        if (image != null && !image.isEmpty()) {
            course.setPreviewImageUrl(imageService.save(image));
        }

        return courseRepo.save(course);
    }

    /* ====================================
       PUBLISH COURSE
    ==================================== */
    public void publish(Long courseId) {

        Course course = courseRepo.findById(courseId).orElseThrow();

        if (course.getModules() == null || course.getModules().isEmpty()) {
            throw new RuntimeException("Add modules before publishing");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        courseRepo.save(course);
    }

    /* ====================================
       GET PUBLISHED COURSES (SAFE FIX)
    ==================================== */
    public List<CourseCardDTO> getPublishedCourses() {
        return courseRepo.findCourseCardsByStatus(CourseStatus.PUBLISHED);
    }
    @Transactional(readOnly = true)
    public Course getFullCourse(Long id) {

        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 🔥 FORCE initialize modules
        Hibernate.initialize(course.getModules());

        for (Module module : course.getModules()) {
            Hibernate.initialize(module.getSubModules());
        }

        return course;
    }




}
