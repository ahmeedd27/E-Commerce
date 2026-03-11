package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.CartDTO;
import com.ahmed.E_CommerceApp.dto.CartItemRequest;
import com.ahmed.E_CommerceApp.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // ─── Get cart ─────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> getCart(
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(cartService.getCart(currentUser.getUsername()));
    }

    // ─── Add item (or increase quantity if already in cart) ───
    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> addItem(
            @RequestBody @Valid CartItemRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(cartService.addItem(request, currentUser.getUsername()));
    }

    // ─── Update item quantity ─────────────────────────────────
    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> updateItem(
            @PathVariable Long cartItemId,
            @RequestBody @Valid CartItemRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(
            cartService.updateItem(cartItemId, request, currentUser.getUsername()));
    }

    // ─── Remove single item ───────────────────────────────────
    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> removeItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(
            cartService.removeItem(cartItemId, currentUser.getUsername()));
    }

    // ─── Clear entire cart ────────────────────────────────────
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails currentUser) {
        cartService.clearCart(currentUser.getUsername());
        return ResponseEntity.noContent().build(); // 204
    }
}