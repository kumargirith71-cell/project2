package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.*;
import com.example.Profenaa_touch.entity.Module;
import com.example.Profenaa_touch.entity.Course;
import com.example.Profenaa_touch.entity.CourseStatus;
import com.example.Profenaa_touch.entity.Department;
import com.example.Profenaa_touch.service.CourseImageStorageService;
import com.example.Profenaa_touch.service.SyllabusStorageService;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private final PaymentRepository paymentRepo;
    private final CartRepository cartRepo;
    private final SyllabusStorageService syllabusService;

    public AdminCourseController(
            CourseRepository courseRepo,
            DepartmentRepository deptRepo,
            CourseImageStorageService imageService,
            ModuleRepository moduleRepo,
            SubModuleRepository subModuleRepo,
            EnrollmentRepository enrollmentRepo,
            PaymentRepository paymentRepo,
            CartRepository cartRepo,
            SyllabusStorageService syllabusService
    ) {
        this.courseRepo = courseRepo;
        this.deptRepo = deptRepo;
        this.imageService = imageService;
        this.moduleRepo = moduleRepo;
        this.subModuleRepo = subModuleRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.paymentRepo = paymentRepo;
        this.cartRepo = cartRepo;
        this.syllabusService = syllabusService;
    }

    /* =========================
       VALIDATION METHOD
    ========================== */
    private void validateSyllabus(MultipartFile syllabus) {

        if (syllabus == null || syllabus.isEmpty()) {
            throw new RuntimeException("Syllabus file required");
        }

        if (!syllabus.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF allowed");
        }
    }

    /* =========================
       CREATE COURSE
    ========================== */
    @PostMapping
    public Course create(
            @RequestParam Long departmentId,
            @RequestParam String name,
            @RequestParam String instructor,
            @RequestParam Double rating,
            @RequestParam Integer totalUsers,
            @RequestParam Double price,
            @RequestParam(required = false) Double oldPrice,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) MultipartFile syllabus
    ) throws Exception {

        Department dept = deptRepo.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Course course = new Course();

        // ✅ BASIC
        course.setName(name);
        course.setInstructor(instructor);
        course.setRating(rating);
        course.setTotalUsers(totalUsers);
        course.setPrice(price);
        course.setOldPrice(oldPrice);
        course.setDescription(description);

        course.setStatus(CourseStatus.DRAFT);
        course.setDepartment(dept);

        // ✅ IMAGE
        if (image != null && !image.isEmpty()) {
            course.setPreviewImageUrl(imageService.save(image));
        }

        // ✅ SYLLABUS
        if (syllabus != null && !syllabus.isEmpty()) {
            validateSyllabus(syllabus);
            course.setSyllabusUrl(syllabusService.save(syllabus));
        }

        return courseRepo.save(course);
    }
    /* =========================
       UPLOAD SYLLABUS
    ========================== */
    @PostMapping("/{courseId}/syllabus")
    public Course uploadSyllabus(
            @PathVariable Long courseId,
            @RequestParam MultipartFile syllabus
    ) throws Exception {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // ✅ FIXED
        validateSyllabus(syllabus);

        String url = syllabusService.save(syllabus);
        course.setSyllabusUrl(url);

        return courseRepo.save(course);
    }

    /* =========================
       UPDATE SYLLABUS
    ========================== */
    @PutMapping("/{courseId}/syllabus")
    public Course updateSyllabus(
            @PathVariable Long courseId,
            @RequestParam MultipartFile syllabus
    ) throws Exception {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // ✅ FIXED: validate first
        validateSyllabus(syllabus);

        // delete old syllabus
        if (course.getSyllabusUrl() != null) {
            File oldFile = new File("/opt/amcurious/uploads" + course.getSyllabusUrl());
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        String url = syllabusService.save(syllabus);
        course.setSyllabusUrl(url);

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
       DELETE COURSE
    ========================== */
    @DeleteMapping("/{id}")
    @Transactional
    public void deleteCourse(@PathVariable Long id) {

        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        cartRepo.deleteAll(cartRepo.findByCourse(course));
        paymentRepo.deleteAll(paymentRepo.findByCourse(course));
        enrollmentRepo.deleteAll(enrollmentRepo.findByCourse(course));

        List<Module> modules = moduleRepo.findByCourse(course);

        for (Module module : modules) {
            subModuleRepo.deleteAll(subModuleRepo.findByModule(module));
            moduleRepo.delete(module);
        }

        courseRepo.delete(course);
    }

    /* =========================
       GET ALL COURSES
    ========================== */
    @GetMapping("/all")
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }
}