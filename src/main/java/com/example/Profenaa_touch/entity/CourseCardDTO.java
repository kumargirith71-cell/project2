package com.example.Profenaa_touch.entity;



public class CourseCardDTO {

    private Long id;
    private String name;
    private String instructor;
    private Double rating;
    private Integer totalUsers;
    private Double price;
    private Double oldPrice;
    private String previewImageUrl;
    private String syllabusUrl;

    public CourseCardDTO(Long id,
                         String name,
                         String instructor,
                         Double rating,
                         Integer totalUsers,
                         Double price,
                         Double oldPrice,
                         String previewImageUrl,
                         String syllabusUrl) {

        this.id = id;
        this.name = name;
        this.instructor = instructor;
        this.rating = rating;
        this.totalUsers = totalUsers;
        this.price = price;
        this.oldPrice = oldPrice;
        this.previewImageUrl = previewImageUrl;
        this.syllabusUrl = syllabusUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getInstructor() { return instructor; }
    public Double getRating() { return rating; }
    public Integer getTotalUsers() { return totalUsers; }
    public Double getPrice() { return price; }
    public Double getOldPrice() { return oldPrice; }
    public String getPreviewImageUrl() { return previewImageUrl; }
    public String getSyllabusUrl() { return syllabusUrl; }
}