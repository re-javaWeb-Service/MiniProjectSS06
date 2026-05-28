package com.re.miniproject1.dto;

import com.re.miniproject1.entity.Course;

public class CourseResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;

    // =====================
    // Constructors
    // =====================
    public CourseResponseDTO() {}

    public CourseResponseDTO(Long id, String name, String description, Double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    /**
     * Factory method: chuyển từ Entity → DTO
     */
    public static CourseResponseDTO fromEntity(Course course) {
        return new CourseResponseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getPrice(),
                course.getImageUrl()
        );
    }

    // =====================
    // Getters & Setters
    // =====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
