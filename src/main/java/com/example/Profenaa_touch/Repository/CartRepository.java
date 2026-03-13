package com.example.Profenaa_touch.Repository;

import com.example.Profenaa_touch.entity.Cart;
import com.example.Profenaa_touch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);

    boolean existsByUser_IdAndCourse_Id(Long userId, Long courseId);

    void deleteByUser_IdAndCourse_Id(Long userId, Long courseId);
}