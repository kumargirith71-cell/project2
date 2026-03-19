package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.Repository.EnrollmentRepository;
import com.example.Profenaa_touch.Repository.SubModuleRepository;
import com.example.Profenaa_touch.Repository.UserRepository;
import com.example.Profenaa_touch.entity.Course;
import com.example.Profenaa_touch.entity.Role;
import com.example.Profenaa_touch.entity.SubModule;
import com.example.Profenaa_touch.entity.User;
import com.example.Profenaa_touch.service.JwtService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final SubModuleRepository subModuleRepo;
    private final EnrollmentRepository enrollRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    public VideoController(SubModuleRepository subModuleRepo,
                           EnrollmentRepository enrollRepo,
                           UserRepository userRepo,
                           JwtService jwtService) {

        this.subModuleRepo = subModuleRepo;
        this.enrollRepo = enrollRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @GetMapping("/stream/{subModuleId}")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable Long subModuleId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) throws Exception {

        SubModule subModule = subModuleRepo.findById(subModuleId)
                .orElseThrow(() -> new RuntimeException("SubModule not found"));

        Course course = subModule.getModule().getCourse();

        // ✅ First video check
        boolean isFirstVideo = subModule.getOrderIndex() == 1;

        User user = null;

        /* ================= AUTH CHECK ================= */
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if (jwtService.isTokenValid(token)) {

                String email = jwtService.extractEmail(token);

                user = userRepo.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));
            }
        }

        /* ================= ACCESS CONTROL ================= */
        if (!isFirstVideo) {

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (user.getRole() != Role.ADMIN) {

                boolean enrolled = enrollRepo.existsByUser_IdAndCourse_Id(
                        user.getId(),
                        course.getId()
                );

                if (!enrolled) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }

        File file = new File("uploads" + subModule.getVideoUrl());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        long fileLength = file.length();

        /* ================= NO RANGE (FIRST LOAD) ================= */
        if (rangeHeader == null) {

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.valueOf("video/mp4"))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE,
                            "bytes 0-" + (fileLength - 1) + "/" + fileLength)
                    .contentLength(fileLength)
                    .body(new InputStreamResource(new FileInputStream(file)));
        }

        /* ================= RANGE STREAMING ================= */

        String[] ranges = rangeHeader.replace("bytes=", "").split("-");

        long start = Long.parseLong(ranges[0]);

        long end = ranges.length > 1 && !ranges[1].isEmpty()
                ? Long.parseLong(ranges[1])
                : fileLength - 1;

        long contentLength = end - start + 1;

        FileInputStream fis = new FileInputStream(file);
        fis.skip(start);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.valueOf("video/mp4"))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE,
                        "bytes " + start + "-" + end + "/" + fileLength)
                .contentLength(contentLength)
                .body(new InputStreamResource(fis));
    }
}