package com.example.Profenaa_touch.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    private String previewImageUrl;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @ManyToOne
    private Department department;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY

    )
    @JsonManagedReference
    private List<Module> modules = new ArrayList<>();   // ✅ FIXED


    /* ===== Getters & Setters ===== */

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public String getPreviewImageUrl() { return previewImageUrl; }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public CourseStatus getStatus() { return status; }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public Department getDepartment() { return department; }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Module> getModules() { return modules; }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}
