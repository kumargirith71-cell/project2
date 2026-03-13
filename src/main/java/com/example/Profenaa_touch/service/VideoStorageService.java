package com.example.Profenaa_touch.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;

@Service
public class VideoStorageService {

    private static final String DIR = "uploads/videos/";
    private static final long MAX_SIZE = 500 * 1024 * 1024;

    public String save(MultipartFile file) throws Exception {

        if (file.getSize() > MAX_SIZE)
            throw new RuntimeException("Video > 500MB");

        Path uploadPath = Paths.get(DIR);
        Files.createDirectories(uploadPath);

        String name = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(name);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING   // 🔥 FIX
        );

        return "/videos/" + name;
    }
}