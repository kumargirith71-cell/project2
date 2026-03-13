package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.entity.*;
import com.example.Profenaa_touch.Repository.*;
import com.example.Profenaa_touch.entity.Module;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserCourseController {

    private final EnrollmentRepository enrollRepo;
    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final ModuleRepository moduleRepo;
    private final PaymentRepository paymentRepo;   // ✅ NEW

    public UserCourseController(
            EnrollmentRepository enrollRepo,
            CourseRepository courseRepo,
            UserRepository userRepo,
            ModuleRepository moduleRepo,
            PaymentRepository paymentRepo   // ✅ NEW
    ) {
        this.enrollRepo = enrollRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.moduleRepo = moduleRepo;
        this.paymentRepo = paymentRepo;    // ✅ NEW
    }

    // ================= EXISTING LOGIC (UNCHANGED) =================

    @GetMapping("/course/{courseId}/modules")
    public List<Module> getModules(
            @PathVariable Long courseId,
            Authentication auth
    ) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!enrollRepo.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("Buy course first");
        }

        return moduleRepo.findByCourseId(courseId);
    }

    @GetMapping("/my-courses")
    public List<Course> myCourses(Authentication auth) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Enrollment> enrollments =
                enrollRepo.findByUserId(user.getId());

        return enrollments.stream()
                .map(enrollment -> {

                    Course course = enrollment.getCourse();

                    // 🔥 Force load modules safely
                    if (course.getModules() != null) {
                        course.getModules().forEach(module -> {
                            if (module.getSubModules() != null) {
                                module.getSubModules().size();
                            }
                        });
                    }

                    return course;
                })
                .toList();
    }

    // ================= NEW ADDITIONS (NO EXISTING LOGIC MODIFIED) =================

    // ✅ Check if user has access (used for preview lock)
    @GetMapping("/has-access/{courseId}")
    public boolean hasAccess(
            @PathVariable Long courseId,
            Authentication auth
    ) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollRepo.existsByUserAndCourse(user, course);
    }

    // ✅ Payment History
    @GetMapping("/payment-history")
    public List<Payment> paymentHistory(Authentication auth) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentRepo
                .findByUserAndStatus(user, "SUCCESS");
    }

    // ✅ Check Course Completion
    @GetMapping("/course/{courseId}/completion")
    public boolean isCompleted(
            @PathVariable Long courseId,
            Authentication auth
    ) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollRepo.findByUserAndCourse(user, course)
                .map(Enrollment::isCompleted)
                .orElse(false);
    }

    // ✅ Mark Course Completed
    @PostMapping("/course/{courseId}/complete")
    public void markCompleted(
            @PathVariable Long courseId,
            Authentication auth
    ) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = enrollRepo
                .findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Not enrolled"));

        enrollment.setCompleted(true);
        enrollment.setCompletedAt(LocalDateTime.now());

        enrollRepo.save(enrollment);
    }

    // ✅ Check Certificate Eligibility
    @GetMapping("/course/{courseId}/certificate-eligible")
    public boolean certificateEligible(
            @PathVariable Long courseId,
            Authentication auth
    ) {

        User user = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollRepo.findByUserAndCourse(user, course)
                .map(Enrollment::isCompleted)
                .orElse(false);
    }
}