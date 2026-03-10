package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.CategoryCreateRequest;
import com.ahmed.E_CommerceApp.dto.CategoryDTO;
import com.ahmed.E_CommerceApp.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ─── ADMIN: Create ────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(
            @RequestBody @Valid CategoryCreateRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(categoryService.createCategory(request));
    }

    // ─── ADMIN: Update ────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryCreateRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    // ─── ADMIN: Delete ────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // ─── PUBLIC: Get all categories ───────────────────────────
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // ─── PUBLIC: Get single category ──────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }
}