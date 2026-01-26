package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.CommentRepo;
import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dto.CommentDTO;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.CommentMapper;
import com.ahmed.E_CommerceApp.model.Comment;
import com.ahmed.E_CommerceApp.model.Product;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ProductRepo productRepo;
    private final CommentRepo commentRepo;
    private final CommentMapper commentMapper;


    public ResponseEntity<CommentDTO> addComment(
            Long id , Authentication connectedUser , CommentDTO commentDTO
    ){
        Product product=productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        User user=(User) connectedUser.getPrincipal();
        Comment comment=commentMapper.toComment(commentDTO);
        comment.setUser(user);
        comment.setProduct(product);
        return ResponseEntity.ok(commentMapper.toDto(commentRepo.save(comment)));
    }

    public ResponseEntity<List<CommentDTO>> getCommentByProduct(Long id) {
        List<Comment> comments=commentRepo.findByProductId(id);
        return ResponseEntity.ok(comments.stream()
                .map(commentMapper::toDto).collect(Collectors.toList()));
    }

    // map is implement the Function interface
    /*@Override
    public CommentDTO apply(Comment comment) {
        return commentMapper.toDto(comment);
    }
    map(comment -> commentMapper.toDto(comment)) .. then method reference
    */
}
