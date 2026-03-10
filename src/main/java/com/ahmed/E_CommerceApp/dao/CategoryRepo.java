package com.ahmed.E_CommerceApp.dao;

import com.ahmed.E_CommerceApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category , Long> {
}
