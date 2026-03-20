package com.example.Profenaa_touch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // ✅ SINGLE CORS CONFIG (Only here)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of(
                            "http://localhost:3000",
                            "https://amcurio.com",
                            "https://www.amcurio.com"
                    ));
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/materials/**").permitAll()
                        .requestMatchers("/course-images/**").permitAll()

                        // ⭐ IMPORTANT FIX
                        .requestMatchers("/video/stream/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers("/cart/**").authenticated()
                        .requestMatchers("/payment/**").authenticated()
                        .requestMatchers("/video/**").authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}