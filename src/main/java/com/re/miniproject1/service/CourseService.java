package com.re.miniproject1.service;

import com.re.miniproject1.dto.CourseRequestDTO;
import com.re.miniproject1.dto.CourseResponseDTO;
import com.re.miniproject1.entity.Course;
import com.re.miniproject1.exception.ResourceNotFoundException;
import com.re.miniproject1.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    public CourseService(CourseRepository courseRepository, FileStorageService fileStorageService) {
        this.courseRepository = courseRepository;
        this.fileStorageService = fileStorageService;
    }

    // ================================================================
    // GET ALL - phân trang
    // ================================================================
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(CourseResponseDTO::fromEntity);
    }

    // ================================================================
    // GET BY ID
    // ================================================================
    public CourseResponseDTO getCourseById(Long id) {
        Course course = findCourseOrThrow(id);
        return CourseResponseDTO.fromEntity(course);
    }

    // ================================================================
    // POST - Tạo mới
    // ================================================================
    public CourseResponseDTO createCourse(CourseRequestDTO dto) {
        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        // imageUrl = null (không truyền lúc tạo)
        Course saved = courseRepository.save(course);
        return CourseResponseDTO.fromEntity(saved);
    }

    // ================================================================
    // PUT - Cập nhật toàn bộ (giữ nguyên imageUrl)
    // ================================================================
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto) {
        Course course = findCourseOrThrow(id);
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        // GIỮ NGUYÊN imageUrl cũ
        Course saved = courseRepository.save(course);
        return CourseResponseDTO.fromEntity(saved);
    }

    // ================================================================
    // PATCH - Cập nhật một phần
    // ================================================================
    public CourseResponseDTO patchCourse(Long id, Map<String, Object> updates) {
        Course course = findCourseOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> {
                    if (value == null || value.toString().isBlank()) {
                        throw new IllegalArgumentException("Tên khóa học không được để trống");
                    }
                    course.setName(value.toString());
                }
                case "description" -> course.setDescription(value != null ? value.toString() : null);
                case "price" -> {
                    double price = Double.parseDouble(value.toString());
                    if (price <= 0) {
                        throw new IllegalArgumentException("Giá khóa học phải lớn hơn 0");
                    }
                    course.setPrice(price);
                }
                default -> {
                    // Bỏ qua các field không hợp lệ (id, imageUrl, ...)
                }
            }
        });

        Course saved = courseRepository.save(course);
        return CourseResponseDTO.fromEntity(saved);
    }

    // ================================================================
    // DELETE - Xóa khóa học (kèm xóa ảnh vật lý nếu có)
    // ================================================================
    public void deleteCourse(Long id) {
        Course course = findCourseOrThrow(id);
        // Xóa file ảnh vật lý nếu tồn tại
        if (course.getImageUrl() != null) {
            fileStorageService.deleteFile(course.getImageUrl());
        }
        courseRepository.delete(course);
    }

    // ================================================================
    // UPLOAD IMAGE
    // ================================================================
    public CourseResponseDTO uploadImage(Long id, MultipartFile file) {
        Course course = findCourseOrThrow(id);

        // Nếu đã có ảnh cũ → xóa file cũ trước
        if (course.getImageUrl() != null) {
            fileStorageService.deleteFile(course.getImageUrl());
        }

        // Lưu file mới → nhận URL
        String imageUrl = fileStorageService.storeFile(file);
        course.setImageUrl(imageUrl);

        Course saved = courseRepository.save(course);
        return CourseResponseDTO.fromEntity(saved);
    }

    // ================================================================
    // DELETE IMAGE
    // ================================================================
    public CourseResponseDTO deleteImage(Long id) {
        Course course = findCourseOrThrow(id);

        if (course.getImageUrl() == null) {
            throw new IllegalArgumentException("Khóa học với id = " + id + " chưa có ảnh đại diện");
        }

        // Xóa file vật lý
        fileStorageService.deleteFile(course.getImageUrl());
        // Set null trong DB
        course.setImageUrl(null);

        Course saved = courseRepository.save(course);
        return CourseResponseDTO.fromEntity(saved);
    }

    // ================================================================
    // Helper
    // ================================================================
    private Course findCourseOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học", id));
    }
}
