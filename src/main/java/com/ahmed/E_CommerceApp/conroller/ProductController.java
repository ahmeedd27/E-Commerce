package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.dto.ProductListDTO;
import com.ahmed.E_CommerceApp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") @Valid ProductDTO productDTO ,
            @RequestPart(value="image" , required = false) MultipartFile multipartFile
            ) throws IOException {
        return productService.createProduct(productDTO , multipartFile);
    }
    @PutMapping(value="/{id}" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id ,
            @RequestPart("product") @Valid ProductDTO productDTO ,
            @RequestPart(value = "image" , required = false) MultipartFile image
    ) throws IOException {
         return productService.updateProduct(id , productDTO , image);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        return productService.deleteProduct(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id){
        return productService.getProduct(id);
    }
    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> getProducts(@PageableDefault(size=10) Pageable pageable){
        return productService.getProducts(pageable);
    }



}
