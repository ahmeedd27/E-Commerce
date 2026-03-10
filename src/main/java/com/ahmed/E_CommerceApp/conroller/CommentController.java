package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.CommentCreateRequest;
import com.ahmed.E_CommerceApp.dto.CommentDTO;
import com.ahmed.E_CommerceApp.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ─── Authenticated user: add comment ──────────────────────
    @PostMapping("/api/products/{productId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long productId,
            @RequestBody @Valid CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(commentService.addComment(productId, request, currentUser.getUsername()));
    }

    // ─── Owner or ADMIN: delete comment ───────────────────────
    @DeleteMapping("/api/comments/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {

        boolean isAdmin = currentUser.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        commentService.deleteComment(id, currentUser.getUsername(), isAdmin);
        return ResponseEntity.noContent().build(); // 204
    }

    // ─── Public: paginated comments for a product ─────────────
    @GetMapping("/api/products/{productId}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt"
                    , direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
            commentService.getCommentsByProduct(productId, pageable));
    }
}