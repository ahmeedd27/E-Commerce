package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.CartRepo;
import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dto.CartDTO;
import com.ahmed.E_CommerceApp.exception.InsufficientStockException;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.CartMapper;
import com.ahmed.E_CommerceApp.model.Cart;
import com.ahmed.E_CommerceApp.model.CartItems;
import com.ahmed.E_CommerceApp.model.Product;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepo productRepo;
    private final CartRepo cartRepo;
    private final CartMapper cartMapper;

    public ResponseEntity<CartDTO> addToCart(Authentication connectedUser, Long productId, Integer quantity) {
        User user=(User) connectedUser.getPrincipal();
        Product product=productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));

        if(product.getQuantity()<quantity){
            throw new InsufficientStockException("Not enough available");
        }
        Cart cart=cartRepo.findByUserId(user.getId())
                .orElse(new Cart(null , user , new ArrayList<>()));
        Optional<CartItems> cartItem=cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        if(cartItem.isPresent()){
            CartItems item=cartItem.get();
            item.setQuantity(item.getQuantity()+quantity);
        }else{
            CartItems cartItems=new CartItems(null , cart , product , quantity);
            cart.getItems().add(cartItems);
        }
        return ResponseEntity.ok(cartMapper.toDto(cartRepo.save(cart)));

    }

    public ResponseEntity<CartDTO> getCart(Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        return ResponseEntity.ok(cartMapper.toDto
                (cartRepo.findByUserId(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Not Found"))));
    }

    public ResponseEntity<String> clearCart(Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        Cart cart=cartRepo.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        cart.getItems().clear();
        cartRepo.save(cart);
        return ResponseEntity.ok("deleted successfully");
    }

    public ResponseEntity<String> removeCartItem(Authentication connectedUser, Long productId) {
        Cart cart=cartRepo.findByUserId(((User) connectedUser.getPrincipal()).getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        cart.getItems().removeIf( item -> item.getProduct().getId().equals(productId));
        cartRepo.save(cart);
        return ResponseEntity.ok("Deleted successfully");
    }
}
