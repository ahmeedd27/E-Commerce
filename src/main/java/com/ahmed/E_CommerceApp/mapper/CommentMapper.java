package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.CommentDTO;
import com.ahmed.E_CommerceApp.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // Comment entity → CommentDTO response
    @Mapping(source = "user.id",    target = "userId")
    @Mapping(source = "user.email", target = "userEmail")  //  added
    CommentDTO toDto(Comment comment);

    // CommentDTO → Comment entity (used internally if needed)
    @Mapping(source = "userId",  target = "user.id")
    @Mapping(target = "product", ignore = true)
    Comment toComment(CommentDTO commentDTO);
}