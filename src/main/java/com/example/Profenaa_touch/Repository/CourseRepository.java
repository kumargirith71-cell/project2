package com.example.Profenaa_touch.Repository;

import com.example.Profenaa_touch.entity.CourseCardDTO;
import com.example.Profenaa_touch.entity.CourseStatus;
import com.example.Profenaa_touch.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
        SELECT new com.example.Profenaa_touch.entity.CourseCardDTO(
            c.id,
            c.name,
            c.price,
            c.previewImageUrl
        )
        FROM Course c
        WHERE c.status = :status
    """)
    List<CourseCardDTO> findCourseCardsByStatus(CourseStatus status);


    @Query("""
        SELECT DISTINCT c FROM Course c
        LEFT JOIN FETCH c.modules m
        LEFT JOIN FETCH m.subModules
        WHERE c.id = :id
    """)
    Course findCourseWithContent(Long id);
}
