package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.CommentDTO;
import com.ahmed.E_CommerceApp.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "user" , target = "user.id")
    @Mapping(target = "product" , ignore = true)
    Comment toComment(CommentDTO commentDTO);

    @Mapping(source = "user.id" , target = "user")
    CommentDTO toDto(Comment comment);

}
