package com.example.Profenaa_touch.service;

import com.example.Profenaa_touch.entity.*;
import com.example.Profenaa_touch.Repository.ModuleRepository;
import com.example.Profenaa_touch.Repository.SubModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Profenaa_touch.entity.Module;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubModuleService {

    private final SubModuleRepository subRepo;
    private final ModuleRepository moduleRepo;
    private final VideoStorageService videoService;
    private final MaterialStorageService materialService;

    public SubModuleService(SubModuleRepository s,
                            ModuleRepository m,
                            VideoStorageService v,
                            MaterialStorageService mat) {
        subRepo = s;
        moduleRepo = m;
        videoService = v;
        materialService = mat;
    }

    public SubModule create(Long moduleId, String title, Integer duration,
                            Integer orderIndex,
                            MultipartFile video, MultipartFile material) throws Exception {

        Module module = moduleRepo.findById(moduleId).orElseThrow();

        // ✅ CHECK FIRST (IMPORTANT)
        boolean exists = subRepo.existsByModuleIdAndOrderIndex(moduleId, orderIndex);

        if (exists) {
            throw new RuntimeException("Order index already exists for this module");
        }

        // ✅ THEN CREATE OBJECT
        SubModule sm = new SubModule();
        sm.setTitle(title);
        sm.setDuration(duration);
        sm.setOrderIndex(orderIndex);
        sm.setModule(module);
        sm.setVideoUrl(videoService.save(video));

        if (material != null && !material.isEmpty()) {
            sm.setMaterialUrl(materialService.save(material));
        }

        return subRepo.save(sm);
    }

    @Transactional
    public void delete(Long subModuleId) {

        SubModule subModule = subRepo.findById(subModuleId)
                .orElseThrow(() -> new RuntimeException("Submodule not found"));

        Module module = subModule.getModule();

        if (module != null) {
            module.getSubModules().remove(subModule);
        }

        subRepo.delete(subModule);
    }

    public SubModule update(Long id,
                            String title,
                            Integer duration,
                            MultipartFile video,
                            MultipartFile material) throws Exception {

        SubModule sm = subRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Submodule not found"));

        sm.setTitle(title);
        sm.setDuration(duration);

        if (video != null && !video.isEmpty()) {
            sm.setVideoUrl(videoService.save(video));
        }

        if (material != null && !material.isEmpty()) {
            sm.setMaterialUrl(materialService.save(material));
        }

        return subRepo.save(sm);
    }

}
