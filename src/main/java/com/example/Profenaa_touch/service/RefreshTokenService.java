package com.example.Profenaa_touch.service;

import com.example.Profenaa_touch.Repository.RefreshTokenRepository;
import com.example.Profenaa_touch.Repository.UserRepository;
import com.example.Profenaa_touch.entity.RefreshToken;
import com.example.Profenaa_touch.entity.User;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    private final long REFRESH_EXPIRY_DAYS = 7;

    public RefreshTokenService(
            RefreshTokenRepository repo,
            UserRepository userRepo
    ) {
        this.repo = repo;
        this.userRepo = userRepo;
    }
    @Transactional
    public RefreshToken createRefreshToken(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow();

        // 🔍 Check if refresh token already exists
        RefreshToken existingToken = repo.findByUser(user).orElse(null);

        if (existingToken != null) {
            existingToken.setToken(UUID.randomUUID().toString());
            existingToken.setExpiryDate(
                    LocalDateTime.now().plusDays(REFRESH_EXPIRY_DAYS)
            );
            return repo.save(existingToken);
        }

        // 🔥 Otherwise create new
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                LocalDateTime.now().plusDays(REFRESH_EXPIRY_DAYS)
        );

        return repo.save(token);
    }

    @Transactional
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken = repo.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            repo.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
}
