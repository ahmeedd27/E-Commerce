package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.CategoryRepo;
import com.ahmed.E_CommerceApp.dto.CategoryCreateRequest;
import com.ahmed.E_CommerceApp.dto.CategoryDTO;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.CategoryMapper;
import com.ahmed.E_CommerceApp.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo   categoryRepo;
    private final CategoryMapper categoryMapper;

    // ─── CREATE ───────────────────────────────────────────────
    @Transactional
    public CategoryDTO createCategory(CategoryCreateRequest request) {
        // Guard: name must be unique (case-insensitive)
        if (categoryRepo.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException(
                "Category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepo.save(category);

        // New category always has 0 products
        CategoryDTO dto = categoryMapper.toDTO(saved);
        dto.setProductCount(0);
        return dto;
    }

    // ─── UPDATE ───────────────────────────────────────────────
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryCreateRequest request) {
        Category category = categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found with id: " + id));

        // Guard: new name must not clash with another category
        if (categoryRepo.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new IllegalArgumentException(
                "Category with name '" + request.getName() + "' already exists");
        }

        categoryMapper.updateEntityFromRequest(request, category);
        Category updated = categoryRepo.save(category);

        CategoryDTO dto = categoryMapper.toDTO(updated);
        dto.setProductCount(updated.getProducts() != null ? updated.getProducts().size() : 0);
        return dto;
    }

    // ─── DELETE ───────────────────────────────────────────────
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepo.findByIdWithProducts(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found with id: " + id));

        // ✅ Guard: prevent deletion if products still assigned
        // Category entity has cascade=ALL — deleting it would delete all products silently
        // That is never acceptable in an e-commerce app
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new IllegalStateException(
                "Cannot delete category '" + category.getName() + "' — it still has "
                + category.getProducts().size() + " product(s). "
                + "Reassign or delete the products first.");
        }

        categoryRepo.delete(category);
    }

    // ─── GET ALL ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepo.findAllWithProducts()
            .stream()
            .map(c -> {
                CategoryDTO dto = categoryMapper.toDTO(c);
                dto.setProductCount(
                    c.getProducts() != null ? c.getProducts().size() : 0);
                return dto;
            })
            .toList();
    }

    // ─── GET SINGLE ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepo.findByIdWithProducts(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found with id: " + id));

        CategoryDTO dto = categoryMapper.toDTO(category);
        dto.setProductCount(
            category.getProducts() != null ? category.getProducts().size() : 0);
        return dto;
    }
}