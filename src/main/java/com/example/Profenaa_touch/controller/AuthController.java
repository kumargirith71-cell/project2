package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.UserRepository;
import com.example.Profenaa_touch.dto.AuthResponse;
import com.example.Profenaa_touch.dto.RequestOtpRequest;
import com.example.Profenaa_touch.dto.VerifyOtpRequest;
import com.example.Profenaa_touch.entity.OtpPurpose;
import com.example.Profenaa_touch.entity.RefreshToken;
import com.example.Profenaa_touch.entity.User;
import com.example.Profenaa_touch.service.JwtService;
import com.example.Profenaa_touch.service.OtpService;
import com.example.Profenaa_touch.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService; // ✅ ADD THIS

    @Autowired
    private UserRepository userRepository;

    public AuthController(
            OtpService otpService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService; // ✅ ASSIGN
    }

    // 🟢 REGISTER
    @PostMapping("/register/request-otp")
    public ResponseEntity<String> requestRegisterOtp(
            @RequestBody RequestOtpRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isVerified()) {
                throw new RuntimeException("User already registered. Please login.");
            }
        });

        otpService.sendRegisterOtp(
                request.getName(),
                request.getEmail()
        );

        return ResponseEntity.ok("Registration OTP sent");
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<String> verifyRegisterOtp(
            @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(
                request.getEmail(),
                request.getOtp(),
                OtpPurpose.REGISTER
        );

        return ResponseEntity.ok("User registered successfully");
    }

    // 🔵 LOGIN
    @PostMapping("/login/request-otp")
    public ResponseEntity<String> requestLoginOtp(
            @RequestBody RequestOtpRequest request) {

        otpService.sendLoginOtp(request.getEmail());
        return ResponseEntity.ok("Login OTP sent");
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<AuthResponse> verifyLoginOtp(
            @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(
                request.getEmail(),
                request.getOtp(),
                OtpPurpose.LOGIN
        );

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // 🔥 Create refresh token
        RefreshToken refreshToken = refreshTokenService
                .createRefreshToken(user.getEmail());

        return ResponseEntity.ok(
                new AuthResponse(
                        accessToken,
                        refreshToken.getToken()
                )
        );
    }

    // 🔁 REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestParam String refreshToken) {

        RefreshToken token = refreshTokenService
                .verifyRefreshToken(refreshToken);

        User user = token.getUser();

        String newAccessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                new AuthResponse(
                        newAccessToken,
                        refreshToken
                )
        );
    }
}
