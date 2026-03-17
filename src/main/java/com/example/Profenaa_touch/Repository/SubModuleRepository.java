package com.example.Profenaa_touch.Repository;

import com.example.Profenaa_touch.entity.SubModule;
import com.example.Profenaa_touch.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubModuleRepository extends JpaRepository<SubModule, Long> {

    List<SubModule> findByModule(Module module);

    @Query("""
       SELECT sm
       FROM SubModule sm
       JOIN sm.module m
       WHERE m.course.id = :courseId
       ORDER BY sm.orderIndex ASC
       """)
    List<SubModule> findAllByCourseId(@Param("courseId") Long courseId);

    boolean existsByModuleIdAndOrderIndex(Long moduleId, Integer orderIndex);
}