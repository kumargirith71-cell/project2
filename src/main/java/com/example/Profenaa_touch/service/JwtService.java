package com.example.Profenaa_touch.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    private static final String SECRET =
            "superSecretJwtKeyForOtpAuth123456";

    private static final long EXPIRY = 1000L * 60 * 60 * 24 * 7;

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .claim("role", role)   // 🔥 ADD ROLE
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRY)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }


    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
