package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.CategoryRepo;
import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dto.ProductCreateRequest;
import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.dto.ProductListDTO;
import com.ahmed.E_CommerceApp.dto.ProductUpdateRequest;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.ProductMapper;
import com.ahmed.E_CommerceApp.model.Category;
import com.ahmed.E_CommerceApp.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo       productRepo;
    private final CategoryRepo      categoryRepo;
    private final ProductMapper     productMapper;
    private final CloudinaryService cloudinaryService;

    // ─── CREATE ───────────────────────────────────────────────
    @Transactional
    public ProductDTO createProduct(ProductCreateRequest request, MultipartFile image) {
        Product product = productMapper.toEntity(request);

        if (request.getCategoryId() != null) {
            product.setCategory(resolveCategory(request.getCategoryId()));
        }

        // fix: guard against null image — image is optional on create
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(uploadOrThrow(image));
        }

        return productMapper.toDTO(productRepo.save(product));
    }

    // ─── UPDATE ───────────────────────────────────────────────
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request, MultipartFile image) {
        // fix: no longer declares throws IOException — infrastructure leaks stop at this layer
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        // fix: one call instead of 4 manual setters — uses @MappingTarget in ProductMapper
        productMapper.updateEntityFromRequest(request, product);

        // fix: null categoryId on update now explicitly clears the category
        // (previously it was silently ignored, which surprised callers)
        if (request.getCategoryId() != null) {
            product.setCategory(resolveCategory(request.getCategoryId()));
        } else {
            product.setCategory(null);
        }

        if (image != null && !image.isEmpty()) {
            cloudinaryService.deleteImage(product.getImageUrl());
            product.setImageUrl(uploadOrThrow(image));
        }

        return productMapper.toDTO(productRepo.save(product));
    }

    // ─── DELETE ───────────────────────────────────────────────
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        cloudinaryService.deleteImage(product.getImageUrl());
        productRepo.delete(product);
    }

    // ─── GET SINGLE ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        return productMapper.toDTO(
                productRepo.findByIdWithComments(id)
                           .orElseThrow(() -> new ResourceNotFoundException(
                                   "Product not found with id: " + id)));
    }

    // ─── GET ALL (paginated) ──────────────────────────────────
    @Transactional(readOnly = true)
    public Page<ProductListDTO> getProducts(Pageable pageable) {
        return productRepo.getProductsWithoutComments(pageable);
    }

    // ─── SEARCH ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<ProductListDTO> searchProducts(
            String name, BigDecimal minPrice, BigDecimal maxPrice,
            Long categoryId, Pageable pageable) {

        String normalizedName = (name != null && !name.trim().isEmpty())
                ? name.trim() : null;

        if (normalizedName != null && normalizedName.length() < 3) {
            throw new IllegalArgumentException("Search term must be at least 3 characters");
        }

        return productRepo.searchProducts(normalizedName, minPrice, maxPrice, categoryId, pageable);
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────

    private Category resolveCategory(Long categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + categoryId));
    }

    /**
     * Wraps Cloudinary's checked IOException in an unchecked exception
     * so it never leaks past the service layer.
     */
    private String uploadOrThrow(MultipartFile image) {
        try {
            return cloudinaryService.uploadImage(image);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload image. Please try again.", e);
        }
    }
}