package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.CourseRepository;
import com.example.Profenaa_touch.Repository.EnrollmentRepository;
import com.example.Profenaa_touch.Repository.UserRepository;
import com.example.Profenaa_touch.entity.Course;
import com.example.Profenaa_touch.entity.Enrollment;
import com.example.Profenaa_touch.entity.Role;
import com.example.Profenaa_touch.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/analytics")
public class AdminAnalyticsController {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollRepo;

    public AdminAnalyticsController(
            UserRepository userRepo,
            CourseRepository courseRepo,
            EnrollmentRepository enrollRepo
    ) {
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.enrollRepo = enrollRepo;
    }

    @GetMapping
    public Map<String, Object> getAnalytics() {

        Map<String, Object> data = new HashMap<>();

        /* ============================
           TOTAL USERS
        ============================ */
        long totalUsers = userRepo.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.USER)
                .count();

        /* ============================
           TOTAL COURSES
        ============================ */
        long totalCourses = courseRepo.count();

        /* ============================
           TOTAL ENROLLMENTS
        ============================ */
        long totalEnrollments = enrollRepo.count();

        /* ============================
           COURSE ENROLLMENT COUNT
        ============================ */
        List<Enrollment> enrollments = enrollRepo.findAll();

        Map<Long, Long> courseCounts =
                enrollments.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.getCourse().getId(),
                                Collectors.counting()
                        ));

        List<Map<String, Object>> topCourses = new ArrayList<>();

        courseCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {

                    Course course =
                            courseRepo.findById(entry.getKey()).orElse(null);

                    if (course != null) {
                        Map<String, Object> c = new HashMap<>();
                        c.put("name", course.getName());
                        c.put("count", entry.getValue());
                        topCourses.add(c);
                    }
                });

        /* ============================
           RECENT ASSIGNMENTS
        ============================ */
        List<Map<String, Object>> recentAssignments =
                enrollments.stream()
                        .sorted(Comparator.comparing(
                                Enrollment::getAssignedAt
                        ).reversed())
                        .limit(5)
                        .map(e -> {
                            Map<String, Object> m = new HashMap<>();
                            m.put("user", e.getUser().getName());
                            m.put("course", e.getCourse().getName());
                            m.put("date", e.getAssignedAt());
                            return m;
                        })
                        .collect(Collectors.toList());

        data.put("totalUsers", totalUsers);
        data.put("totalCourses", totalCourses);
        data.put("totalEnrollments", totalEnrollments);
        data.put("topCourses", topCourses);
        data.put("recentAssignments", recentAssignments);

        return data;
    }
}
