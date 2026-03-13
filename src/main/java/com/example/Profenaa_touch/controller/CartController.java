package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.*;
import com.example.Profenaa_touch.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartRepository cartRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;

    public CartController(
            CartRepository cartRepo,
            UserRepository userRepo,
            CourseRepository courseRepo
    ) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
    }

    @PostMapping("/add/{courseId}")
    public void addToCart(
            @PathVariable Long courseId,
            Authentication auth
    ) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        Course course = courseRepo.findById(courseId).orElseThrow();

        if (!cartRepo.existsByUser_IdAndCourse_Id(user.getId(), courseId)) {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setCourse(course);
            cartRepo.save(cart);
        }
    }

    @GetMapping
    public List<Cart> getCart(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        return cartRepo.findByUser(user);
    }

    /* =========================
       REMOVE FROM CART (FIXED)
    ========================== */
    @Transactional
    @DeleteMapping("/remove/{courseId}")
    public void removeFromCart(
            @PathVariable Long courseId,
            Authentication auth
    ) {
        User user = userRepo.findByEmail(auth.getName()).orElseThrow();
        cartRepo.deleteByUser_IdAndCourse_Id(user.getId(), courseId);
    }
}