package com.re.miniproject1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Danh sách định dạng ảnh cho phép
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    /**
     * Lưu file ảnh vào thư mục uploads, trả về URL tương đối
     */
    public String storeFile(MultipartFile file) {
        // Validate file không rỗng
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }

        // Validate định dạng (MIME type)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Định dạng file không hợp lệ. Chỉ chấp nhận: jpg, jpeg, png, gif, webp"
            );
        }

        // Lấy extension từ tên file gốc
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown"
        );
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        // Validate extension
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Phần mở rộng file không hợp lệ. Chỉ chấp nhận: " + ALLOWED_EXTENSIONS
            );
        }

        // Tạo tên file unique với UUID
        String newFileName = UUID.randomUUID().toString() + extension;

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Lưu file
            Path targetLocation = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL tương đối (dùng để truy cập qua HTTP)
            return "/uploads/" + newFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Không thể lưu file ảnh: " + newFileName, ex);
        }
    }

    /**
     * Xóa file ảnh vật lý khỏi server
     * @param imageUrl URL tương đối: /uploads/abc.jpg
     */
    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            // Lấy tên file từ URL: /uploads/abc.jpg → abc.jpg
            String fileName = imageUrl.startsWith("/uploads/")
                    ? imageUrl.substring("/uploads/".length())
                    : imageUrl;

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(fileName).normalize();

            // Đảm bảo không bị path traversal attack
            if (!filePath.startsWith(uploadPath)) {
                throw new SecurityException("Đường dẫn file không hợp lệ");
            }

            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Log lỗi nhưng không throw (không chặn luồng chính)
            System.err.println("Cảnh báo: Không thể xóa file ảnh: " + imageUrl + " - " + ex.getMessage());
        }
    }
}
