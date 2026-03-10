// service/CloudinaryService.java
package com.ahmed.E_CommerceApp.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    // ─── Upload image → returns secure URL ────────────────────
    public String uploadImage(MultipartFile file) throws IOException {
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Validate file size — 5MB max
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Image size must not exceed 5MB");
        }

        Map<?, ?> result = cloudinary.uploader().upload(
            file.getBytes(),
            Map.of("folder", "e-commerce/products") // organized in Cloudinary folders
        );

        return (String) result.get("secure_url"); // ✅ always use HTTPS URL
    }

    // ─── Delete image by URL ───────────────────────────────────
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (IOException e) {
            // ✅ specific exception, proper logger
            log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        // Extract "e-commerce/products/filename" without extension
        String withoutQuery = imageUrl.split("\\?")[0];
        String[] parts = withoutQuery.split("/upload/");
        if (parts.length < 2) return imageUrl;
        String afterUpload = parts[1];
        // Remove version prefix if present (v1234567/)
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }
        // Remove file extension
        return afterUpload.substring(0, afterUpload.lastIndexOf("."));
    }
}