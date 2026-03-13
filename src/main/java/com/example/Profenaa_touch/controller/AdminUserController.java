package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.UserRepository;
import com.example.Profenaa_touch.Repository.EnrollmentRepository;
import com.example.Profenaa_touch.Repository.RefreshTokenRepository;
import com.example.Profenaa_touch.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepo;
    private final EnrollmentRepository enrollRepo;
    private final RefreshTokenRepository refreshTokenRepo;

    public AdminUserController(
            UserRepository userRepo,
            EnrollmentRepository enrollRepo,
            RefreshTokenRepository refreshTokenRepo
    ) {
        this.userRepo = userRepo;
        this.enrollRepo = enrollRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    /* =========================
       GET ALL USERS
    ========================== */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /* =========================
       DELETE USER (FINAL FIX)
    ========================== */
    @Transactional
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 Delete enrollments first
        enrollRepo.deleteByUser(user);

        // 🔥 Delete refresh token
        refreshTokenRepo.deleteByUser(user);

        // 🔥 Finally delete user
        userRepo.delete(user);
    }
}
