package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.CommentDTO;
import com.ahmed.E_CommerceApp.dto.ProductDTO;
import com.ahmed.E_CommerceApp.model.Comment;
import com.ahmed.E_CommerceApp.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO productDTO);
    @Mapping(target = "userId",source = "user.id")
    CommentDTO toDTO(Comment comment);
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "product", ignore = true)
    Comment toEntity(CommentDTO commentDTO);

}
