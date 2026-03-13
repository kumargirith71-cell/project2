package com.example.Profenaa_touch.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int orderIndex;

    @ManyToOne
    @JsonBackReference
    private Course course;

    @OneToMany(
            mappedBy = "module",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY

    )
    @JsonManagedReference
    private List<SubModule> subModules = new ArrayList<>();  // ✅ FIXED


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int getOrderIndex() { return orderIndex; }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Course getCourse() { return course; }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<SubModule> getSubModules() {
        return subModules;
    }

    public void setSubModules(List<SubModule> subModules) {
        this.subModules = subModules;
    }
}
