package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole(ADMIN)")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") @Valid ProductDTO productDTO ,
            @RequestPart(value="image" , required = false) MultipartFile multipartFile
            ){
        return productService.createProduct(productDTO , multipartFile);
    }
}
