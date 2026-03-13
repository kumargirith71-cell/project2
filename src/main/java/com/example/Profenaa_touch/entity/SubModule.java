package com.example.Profenaa_touch.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class SubModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer duration;
    private int orderIndex;

    private String videoUrl;
    private String materialUrl;

    @ManyToOne
    @JsonBackReference
    private Module module;

    /* ===== Getters & Setters ===== */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getMaterialUrl() { return materialUrl; }
    public void setMaterialUrl(String materialUrl) {
        this.materialUrl = materialUrl;
    }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
}
