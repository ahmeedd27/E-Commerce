package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.dto.ProductListDTO;
import com.ahmed.E_CommerceApp.model.Product;
import com.ahmed.E_CommerceApp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("SELECT new com.ahmed.E_CommerceApp.dto.ProductListDTO(p.id , p.name , p.description , p.price , p.quantity , p.imageUrl) FROM Product p")
    Page<ProductListDTO> getProductsWithoutComments(Pageable pageable);
}
