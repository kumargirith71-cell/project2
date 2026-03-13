package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.entity.Module;
import com.example.Profenaa_touch.Repository.CourseRepository;
import com.example.Profenaa_touch.Repository.DepartmentRepository;
import com.example.Profenaa_touch.Repository.ModuleRepository;
import com.example.Profenaa_touch.Repository.SubModuleRepository;
import com.example.Profenaa_touch.Repository.EnrollmentRepository;
import com.example.Profenaa_touch.entity.Course;
import com.example.Profenaa_touch.entity.CourseStatus;
import com.example.Profenaa_touch.entity.Department;
import com.example.Profenaa_touch.service.CourseImageStorageService;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/course")
public class AdminCourseController {

    private final CourseRepository courseRepo;
    private final DepartmentRepository deptRepo;
    private final CourseImageStorageService imageService;
    private final ModuleRepository moduleRepo;
    private final SubModuleRepository subModuleRepo;
    private final EnrollmentRepository enrollmentRepo;

    public AdminCourseController(
            CourseRepository courseRepo,
            DepartmentRepository deptRepo,
            CourseImageStorageService imageService,
            ModuleRepository moduleRepo,
            SubModuleRepository subModuleRepo,
            EnrollmentRepository enrollmentRepo
    ) {
        this.courseRepo = courseRepo;
        this.deptRepo = deptRepo;
        this.imageService = imageService;
        this.moduleRepo = moduleRepo;
        this.subModuleRepo = subModuleRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    /* =========================
       CREATE COURSE
    ========================== */
    @PostMapping
    public Course create(
            @RequestParam Long departmentId,
            @RequestParam String name,
            @RequestParam String price,
            @RequestParam(required = false) MultipartFile image
    ) throws Exception {

        Department dept = deptRepo.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Course course = new Course();
        course.setName(name);
        course.setPrice(Double.parseDouble(price));
        course.setStatus(CourseStatus.DRAFT);
        course.setDepartment(dept);

        if (image != null && !image.isEmpty()) {
            course.setPreviewImageUrl(imageService.save(image));
        }

        return courseRepo.save(course);
    }

    /* =========================
       PUBLISH COURSE
    ========================== */
    @PutMapping("/{id}/publish")
    public Course publish(@PathVariable Long id) {

        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setStatus(CourseStatus.PUBLISHED);

        return courseRepo.saveAndFlush(course);
    }

    /* =========================
       UPDATE COURSE
    ========================== */
    @PutMapping("/{id}")
    public Course updateCourse(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam(required = false) MultipartFile image
    ) throws Exception {

        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setName(name);
        course.setPrice(price);

        if (image != null && !image.isEmpty()) {
            course.setPreviewImageUrl(imageService.save(image));
        }

        return courseRepo.save(course);
    }

    /* =========================
       DELETE COURSE (FIXED)
    ========================== */
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteCourse(@PathVariable Long id) {

        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 1️⃣ Delete enrollments first
        enrollmentRepo.deleteAll(enrollmentRepo.findByCourse(course));

        // 2️⃣ Delete modules and submodules
        List<Module> modules = moduleRepo.findByCourse(course);

        for (Module module : modules) {

            subModuleRepo.deleteAll(
                    subModuleRepo.findByModule(module)
            );

            moduleRepo.delete(module);
        }

        // 3️⃣ Finally delete course
        courseRepo.delete(course);
    }

    /* =========================
       GET ALL COURSES (ADMIN)
    ========================== */
    @GetMapping("/all")
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }
}
