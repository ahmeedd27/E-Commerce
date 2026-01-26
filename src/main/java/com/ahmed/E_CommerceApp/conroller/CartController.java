package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.CartDTO;
import com.ahmed.E_CommerceApp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
        Authentication connectedUser ,
        @RequestParam Long productId ,
        @RequestParam Integer quantity
    ){
        return cartService.addToCart(connectedUser , productId , quantity);
    }
    @GetMapping
    public ResponseEntity<CartDTO> getCart(Authentication connectedUser){
        return cartService.getCart(connectedUser);
    }
    @DeleteMapping
    public ResponseEntity<String> clearCart(Authentication connectedUser){
        return cartService.clearCart(connectedUser);
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeItemFromCart(Authentication connectedUser
    , @PathVariable Long productId){
        return cartService.removeCartItem(connectedUser , productId);
    }
}
