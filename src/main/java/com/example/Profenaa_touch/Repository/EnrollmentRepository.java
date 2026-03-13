package com.example.Profenaa_touch.Repository;

import com.example.Profenaa_touch.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    boolean existsByUser_IdAndCourse_Id(Long userId, Long courseId);

    boolean existsByUserAndCourse(User user, Course course);

    List<Enrollment> findByUser(User user);

    boolean existsByUser_EmailAndCourse_Id(String email, Long id);

    List<Enrollment> findByCourse(Course course);

    // 🔥 ADD THIS
    void deleteByUser(User user);
    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    List<Enrollment> findByUserId(Long userId);
}
