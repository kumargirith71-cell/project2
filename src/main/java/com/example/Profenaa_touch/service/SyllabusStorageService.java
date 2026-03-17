package com.example.Profenaa_touch.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class SyllabusStorageService {

    private static final String DIR = "uploads/syllabus/";

    public String save(MultipartFile file) throws Exception {

        Path uploadPath = Paths.get(DIR);
        Files.createDirectories(uploadPath);

        String name = System.currentTimeMillis()
                + "_" + file.getOriginalFilename();

        Path filePath = uploadPath.resolve(name);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/syllabus/" + name;
    }
}