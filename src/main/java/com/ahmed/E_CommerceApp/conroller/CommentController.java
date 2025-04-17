package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.CommentDTO;
import com.ahmed.E_CommerceApp.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/product/{id}")
    public ResponseEntity<CommentDTO> addComment(
        @PathVariable Long id ,
        Authentication connectedUser ,
        @RequestBody @Valid CommentDTO commentDTO
    ){
   return commentService.addComment(id , connectedUser , commentDTO);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentByProduct(@PathVariable Long id){
        return commentService.getCommentByProduct(id);
    }

}
