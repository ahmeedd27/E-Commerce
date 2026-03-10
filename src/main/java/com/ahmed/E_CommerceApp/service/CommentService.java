package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.CommentRepo;
import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.dto.CommentCreateRequest;
import com.ahmed.E_CommerceApp.dto.CommentDTO;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.CommentMapper;
import com.ahmed.E_CommerceApp.model.Comment;
import com.ahmed.E_CommerceApp.model.Product;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo   commentRepo;
    private final ProductRepo   productRepo;
    private final UserRepo      userRepo;
    private final CommentMapper commentMapper;

    // ─── ADD COMMENT ──────────────────────────────────────────
    @Transactional
    public CommentDTO addComment(Long productId,
                                 CommentCreateRequest request,
                                 String currentUserEmail) {
        // Verify product exists
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found with id: " + productId));

        // Load authenticated user
        User user = userRepo.findByEmail(currentUserEmail)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found: " + currentUserEmail));

        Comment comment = Comment.builder()
            .content(request.getContent())
            .score(request.getScore())
            .user(user)
            .product(product)
            .createdAt(LocalDateTime.now())
            .build();

        return commentMapper.toDto(commentRepo.save(comment));
    }

    // ─── DELETE COMMENT ───────────────────────────────────────
    @Transactional
    public void deleteComment(Long commentId, String currentUserEmail, boolean isAdmin) {
        Comment comment = commentRepo.findByIdWithUser(commentId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Comment not found with id: " + commentId));

        //  Owner OR admin can delete
        boolean isOwner = comment.getUser().getEmail().equals(currentUserEmail);
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You can only delete your own comments");
        }

        commentRepo.delete(comment);
    }

    // ─── GET COMMENTS (paginated) ─────────────────────────────
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByProduct(Long productId, Pageable pageable) {
        // Verify product exists first — gives a clear 404 vs empty page
        if (!productRepo.existsById(productId)) {
            throw new ResourceNotFoundException(
                "Product not found with id: " + productId);
        }

        return commentRepo.findByProductId(productId, pageable)
                          .map(commentMapper::toDto);
    }
}