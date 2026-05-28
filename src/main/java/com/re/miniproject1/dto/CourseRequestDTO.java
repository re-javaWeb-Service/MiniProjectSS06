package com.re.miniproject1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CourseRequestDTO {

    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;

    private String description;

    @Positive(message = "Giá khóa học phải lớn hơn 0")
    private Double price;

    // =====================
    // Constructors
    // =====================
    public CourseRequestDTO() {}

    public CourseRequestDTO(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // =====================
    // Getters & Setters
    // =====================
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
