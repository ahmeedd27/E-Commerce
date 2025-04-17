package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.dto.ProductListDTO;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.ProductMapper;
import com.ahmed.E_CommerceApp.model.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return fileName;
    }

     @Transactional
    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO, MultipartFile image) throws IOException {
        Product product = productMapper.toEntity(productDTO);
        if (image != null && !image.isEmpty()){
            String fileName=saveImage(image);
            product.setImageUrl("/images/"+fileName);
        }

            return ResponseEntity.ok(productMapper.toDTO(productRepo.save(product)));
    }



    @Transactional
    public ResponseEntity<ProductDTO> updateProduct(Long id, ProductDTO productDTO, MultipartFile image) throws IOException {
        Product existingProduct=productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setQuantity(productDTO.getQuantity());
        if(image!=null && !image.isEmpty()){
            String fileName=saveImage(image);
            existingProduct.setImageUrl("/images/"+fileName);
        }
        return ResponseEntity.ok(productMapper.toDTO(productRepo.save(existingProduct)));
    }

    @Transactional
    public ResponseEntity<String> deleteProduct(Long id) {
        if(!productRepo.existsById(id)){
            throw new ResourceNotFoundException("Not Found");
        }
        productRepo.deleteById(id);
        return ResponseEntity.ok("Deleted Successfully");
    }


    public ResponseEntity<ProductDTO> getProduct(Long id) {
        Product product=productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found"));
        return ResponseEntity.ok(productMapper.toDTO(product));
    }

    public ResponseEntity<Page<ProductListDTO>> getProducts(Pageable pageable) {
        return ResponseEntity.ok(productRepo.getProductsWithoutComments(pageable));
    }
}
