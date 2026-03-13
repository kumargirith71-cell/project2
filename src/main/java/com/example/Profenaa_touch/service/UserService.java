package com.example.Profenaa_touch.service;

import com.example.Profenaa_touch.Repository.UserRepository;
import com.example.Profenaa_touch.entity.Role;
import com.example.Profenaa_touch.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔍 Find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // ✅ Check if user exists
    public boolean exists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // 🆕 Register NEW user only
    public User register(String name, String email) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setVerified(true);
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // 🔄 Save / update existing user
    public User save(User user) {
        return userRepository.save(user);
    }
}
