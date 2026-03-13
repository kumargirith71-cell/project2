package com.example.Profenaa_touch.Repository;

import com.example.Profenaa_touch.entity.Course;
import com.example.Profenaa_touch.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCourseId(Long courseId);
    List<Module> findByCourse(Course course);
}
