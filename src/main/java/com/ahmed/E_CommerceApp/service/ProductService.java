package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.mapper.ProductMapper;
import com.ahmed.E_CommerceApp.model.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";
    private final ProductMapper productMapper;
    private final ProductRepo productRepo;


    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO, MultipartFile image) throws IOException {
        Product product = productMapper.toEntity(productDTO);
        if (image != null && !image.isEmpty()){
            String fileName=saveImage(image);
            product.setImageUrl("/images/"+fileName);
        }

            return ResponseEntity.ok(productMapper.toDTO(productRepo.save(product)));
    }

    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return fileName;
    }
}
