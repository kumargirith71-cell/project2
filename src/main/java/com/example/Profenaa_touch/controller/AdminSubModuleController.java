package com.example.Profenaa_touch.controller;

import com.example.Profenaa_touch.entity.SubModule;
import com.example.Profenaa_touch.service.SubModuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/submodules")
public class AdminSubModuleController {

    private final SubModuleService subModuleService;

    public AdminSubModuleController(SubModuleService subModuleService) {
        this.subModuleService = subModuleService;
    }

    /* CREATE */
    @PostMapping("/{moduleId}")
    public SubModule createSubModule(
            @PathVariable Long moduleId,
            @RequestParam String title,
            @RequestParam Integer duration,
            @RequestParam MultipartFile video,
            @RequestParam(required = false) MultipartFile material
    ) throws Exception {

        return subModuleService.create(
                moduleId,
                title,
                duration,
                video,
                material
        );
    }

    /* UPDATE */
    @PutMapping("/{id}")
    public SubModule updateSubModule(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam Integer duration,
            @RequestParam(required = false) MultipartFile video,
            @RequestParam(required = false) MultipartFile material
    ) throws Exception {

        return subModuleService.update(
                id,
                title,
                duration,
                video,
                material
        );
    }

    /* DELETE (FIXED) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubModule(@PathVariable Long id) {

        subModuleService.delete(id);
        return ResponseEntity.ok().build();
    }
}
