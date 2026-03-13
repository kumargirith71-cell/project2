package com.example.Profenaa_touch.entity;



public class CourseCardDTO {

    private Long id;
    private String name;
    private Double price;
    private String previewImageUrl;

    public CourseCardDTO(Long id,
                         String name,
                         Double price,
                         String previewImageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.previewImageUrl = previewImageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }
}
