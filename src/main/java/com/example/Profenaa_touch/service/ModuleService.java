package com.example.Profenaa_touch.service;

import com.example.Profenaa_touch.entity.*;
import com.example.Profenaa_touch.entity.Module;
import com.example.Profenaa_touch.Repository.ModuleRepository;
import com.example.Profenaa_touch.Repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepo;
    private final CourseRepository courseRepo;

    public ModuleService(ModuleRepository m, CourseRepository c) {
        moduleRepo = m;
        courseRepo = c;
    }

    public Module create(Long courseId, String title, int orderIndex) {
        Course course = courseRepo.findById(courseId).orElseThrow();



        Module module = new Module();
        module.setTitle(title);
        module.setOrderIndex(orderIndex);
        module.setCourse(course);

        return moduleRepo.save(module);
    }

    public void delete(Long moduleId) {
        moduleRepo.deleteById(moduleId);
    }
}
