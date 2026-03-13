package com.example.Profenaa_touch.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;

@Service
public class MaterialStorageService {

    private static final String DIR = "uploads/materials/";

    public String save(MultipartFile file) throws Exception {

        if (file == null) return null;

        Path uploadPath = Paths.get(DIR);
        Files.createDirectories(uploadPath);

        String name = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(name);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING   // 🔥 FIX
        );

        return "/materials/" + name;
    }
}