package com.re.miniproject1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Positive(message = "Giá khóa học phải lớn hơn 0")
    @Column(nullable = false)
    private Double price;

    @Column(name = "image_url")
    private String imageUrl;

    // =====================
    // Constructors
    // =====================
    public Course() {}

    public Course(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Course(Long id, String name, String description, Double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
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

    @Override
    public String toString() {
        return "Course{id=" + id + ", name='" + name + "', price=" + price + "}";
    }
}
