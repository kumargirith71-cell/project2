package com.example.Profenaa_touch.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class CourseImageStorageService {

    // Save directly in project root folder
    private static final String DIR = "course-images";

    public String save(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(DIR);

        // ✅ Create folder if not exists
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path filePath = uploadPath.resolve(filename);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        // ✅ Return correct URL path
        return "/course-images/" + filename;
    }
}
