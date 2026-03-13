package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.CourseRepository;
import com.example.Profenaa_touch.Repository.EnrollmentRepository;
import com.example.Profenaa_touch.Repository.UserRepository;
import com.example.Profenaa_touch.entity.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/enrollment")
public class AdminEnrollmentController {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollRepo;

    public AdminEnrollmentController(
            UserRepository userRepo,
            CourseRepository courseRepo,
            EnrollmentRepository enrollRepo
    ) {
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.enrollRepo = enrollRepo;
    }

    @PostMapping("/assign")
    public void assignCourse(
            @RequestParam String email,
            @RequestParam Long courseId
    ) {


        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not registered. Ask student to register first.")
                );



        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));


        if (enrollRepo.existsByUser_IdAndCourse_Id(user.getId(), course.getId())) {
            throw new RuntimeException("User already enrolled in this course");
        }


        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setSource(EnrollmentSource.ADMIN);
        enrollment.setAssignedAt(LocalDateTime.now());

        enrollRepo.save(enrollment);
    }
    @PostMapping("/assign/by-user-id")
    public void assignByUserId(
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (enrollRepo.existsByUser_IdAndCourse_Id(user.getId(), course.getId())) {
            throw new RuntimeException("User already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setSource(EnrollmentSource.ADMIN);
        enrollment.setAssignedAt(LocalDateTime.now());

        enrollRepo.save(enrollment);
    }

}
