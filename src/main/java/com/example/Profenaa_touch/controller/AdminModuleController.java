package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.entity.Module;
import com.example.Profenaa_touch.entity.Course;
import com.example.Profenaa_touch.Repository.ModuleRepository;
import com.example.Profenaa_touch.Repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/modules")
public class AdminModuleController {

    private final ModuleRepository moduleRepo;
    private final CourseRepository courseRepo;

    public AdminModuleController(
            ModuleRepository moduleRepo,
            CourseRepository courseRepo
    ) {
        this.moduleRepo = moduleRepo;
        this.courseRepo = courseRepo;
    }

    /* =========================
       CREATE MODULE (NEW)
    ========================== */
    @PostMapping("/{courseId}")
    public Module createModule(
            @PathVariable Long courseId,
            @RequestParam String title,
            @RequestParam int orderIndex
    ) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Module module = new Module();
        module.setTitle(title);
        module.setOrderIndex(orderIndex);
        module.setCourse(course);

        return moduleRepo.save(module);
    }

    /* =========================
       UPDATE MODULE
    ========================== */
    @PutMapping("/{id}")
    public Module updateModule(
            @PathVariable Long id,
            @RequestParam String title
    ) {

        Module module = moduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        module.setTitle(title);

        return moduleRepo.saveAndFlush(module);
    }

    /* =========================
       DELETE MODULE
    ========================== */
    @Transactional
    @DeleteMapping("/{id}")
    public void deleteModule(@PathVariable Long id) {

        Module module = moduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        moduleRepo.delete(module);
        moduleRepo.flush();
    }
}
