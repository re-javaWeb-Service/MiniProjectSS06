package com.re.miniproject1.controller;

import com.re.miniproject1.dto.CourseRequestDTO;
import com.re.miniproject1.dto.CourseResponseDTO;
import com.re.miniproject1.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ================================================================
    // GET ALL - Phân trang & sắp xếp
    // GET /api/courses?page=0&size=5&sort=price,desc
    // ================================================================
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        // Parse sort parameter: "price,desc" hoặc "name,asc"
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0].trim();
        Sort.Direction direction = (sortParams.length > 1 && sortParams[1].trim().equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<CourseResponseDTO> result = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(result);
    }

    // ================================================================
    // GET BY ID
    // GET /api/courses/{id}
    // ================================================================
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        CourseResponseDTO dto = courseService.getCourseById(id);
        return ResponseEntity.ok(dto);
    }

    // ================================================================
    // POST - Tạo mới
    // POST /api/courses
    // ================================================================
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO dto) {
        CourseResponseDTO created = courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ================================================================
    // PUT - Cập nhật toàn bộ (giữ imageUrl)
    // PUT /api/courses/{id}
    // ================================================================
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO dto
    ) {
        CourseResponseDTO updated = courseService.updateCourse(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ================================================================
    // PATCH - Cập nhật một phần
    // PATCH /api/courses/{id}
    // Body: {"price": 199.0} hoặc {"name": "React JS"}
    // ================================================================
    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> patchCourse(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        CourseResponseDTO patched = courseService.patchCourse(id, updates);
        return ResponseEntity.ok(patched);
    }

    // ================================================================
    // DELETE - Xóa khóa học + ảnh vật lý
    // DELETE /api/courses/{id}
    // ================================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // ================================================================
    // UPLOAD IMAGE
    // POST /api/courses/{id}/image  (form-data key: file)
    // ================================================================
    @PostMapping("/{id}/image")
    public ResponseEntity<CourseResponseDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        CourseResponseDTO result = courseService.uploadImage(id, file);
        return ResponseEntity.ok(result);
    }

    // ================================================================
    // DELETE IMAGE
    // DELETE /api/courses/{id}/image
    // ================================================================
    @DeleteMapping("/{id}/image")
    public ResponseEntity<CourseResponseDTO> deleteImage(@PathVariable Long id) {
        CourseResponseDTO result = courseService.deleteImage(id);
        return ResponseEntity.ok(result);
    }
}
