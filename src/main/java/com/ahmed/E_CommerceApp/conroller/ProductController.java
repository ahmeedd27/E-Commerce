package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.ProductCreateRequest;
import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.dto.ProductListDTO;
import com.ahmed.E_CommerceApp.dto.ProductUpdateRequest;
import com.ahmed.E_CommerceApp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ─── ADMIN: Create ────────────────────────────────────────
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") @Valid ProductCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(productService.createProduct(request, image));
    }

    // ─── ADMIN: Update ────────────────────────────────────────
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
            // fix: removed "throws IOException" — service wraps it in IllegalStateException,
            //      nothing checked leaks to the controller layer anymore
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request, image));
    }

    // ─── ADMIN: Delete ────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ─── PUBLIC: Get single product with comments ─────────────
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // ─── PUBLIC: Get all products paginated ───────────────────
    @GetMapping
    public ResponseEntity<Page<ProductListDTO>> getProducts(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.getProducts(pageable));
    }

    // ─── PUBLIC: Search products ──────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<Page<ProductListDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "price") Pageable pageable) {
        return ResponseEntity.ok(
                productService.searchProducts(name, minPrice, maxPrice, categoryId, pageable));
    }
}