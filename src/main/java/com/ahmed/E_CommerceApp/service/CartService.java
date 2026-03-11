package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.CartItemsRepo;
import com.ahmed.E_CommerceApp.dao.CartRepo;
import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dao.UserRepo;
import com.ahmed.E_CommerceApp.dto.CartDTO;
import com.ahmed.E_CommerceApp.dto.CartItemDTO;
import com.ahmed.E_CommerceApp.dto.CartItemRequest;
import com.ahmed.E_CommerceApp.exception.InsufficientStockException;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.CartMapper;
import com.ahmed.E_CommerceApp.model.Cart;
import com.ahmed.E_CommerceApp.model.CartItems;
import com.ahmed.E_CommerceApp.model.Product;
import com.ahmed.E_CommerceApp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo      cartRepo;
    private final CartItemsRepo cartItemsRepo;
    private final ProductRepo   productRepo;
    private final UserRepo      userRepo;
    private final CartMapper    cartMapper;

    // ─── GET CART ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public CartDTO getCart(String email) {
        User user = resolveUser(email);
        Cart cart = resolveCart(user.getId());
        return buildCartDTO(cart);
    }

    // ─── ADD ITEM ─────────────────────────────────────────────
    @Transactional
    public CartDTO addItem(CartItemRequest request, String email) {
        User user    = resolveUser(email);
        Cart cart    = resolveCart(user.getId());
        Product product = resolveProduct(request.getProductId());

        // ✅ Stock validation
        validateStock(product, request.getQuantity());

        Optional<CartItems> existing =
            cartItemsRepo.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existing.isPresent()) {
            // ─── Product already in cart — increase quantity ───
            CartItems item = existing.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            // ✅ Validate combined quantity against stock
            validateStock(product, newQuantity);

            item.setQuantity(newQuantity);
            cartItemsRepo.save(item);
        } else {
            // ─── New item — add to cart ────────────────────────
            CartItems newItem = CartItems.builder()
                .cart(cart)
                .product(product)
                .quantity(request.getQuantity())
                .build();
            cartItemsRepo.save(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        // Reload cart with all items fresh
        return buildCartDTO(resolveCart(user.getId()));
    }

    // ─── UPDATE ITEM QUANTITY ──────────────────────────────────
    @Transactional
    public CartDTO updateItem(Long cartItemId, CartItemRequest request, String email) {
        User user         = resolveUser(email);
        Cart cart         = resolveCart(user.getId());
        CartItems item    = resolveCartItem(cartItemId);

        // ✅ Ownership check — item must belong to caller's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new AccessDeniedException("This item does not belong to your cart");
        }

        // ✅ Stock validation on new quantity
        validateStock(item.getProduct(), request.getQuantity());

        item.setQuantity(request.getQuantity());
        cartItemsRepo.save(item);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        return buildCartDTO(resolveCart(user.getId()));
    }

    // ─── REMOVE ITEM ──────────────────────────────────────────
    @Transactional
    public CartDTO removeItem(Long cartItemId, String email) {
        User user      = resolveUser(email);
        Cart cart      = resolveCart(user.getId());
        CartItems item = resolveCartItem(cartItemId);

        // ✅ Ownership check
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new AccessDeniedException("This item does not belong to your cart");
        }

        cartItemsRepo.delete(item);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);

        return buildCartDTO(resolveCart(user.getId()));
    }

    // ─── CLEAR CART ───────────────────────────────────────────
    @Transactional
    public void clearCart(String email) {
        User user = resolveUser(email);
        Cart cart = resolveCart(user.getId());

        // orphanRemoval=true on Cart.items → clearing the list deletes all CartItems rows
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);
    }

    // ─── PRIVATE HELPERS ──────────────────────────────────────

    private User resolveUser(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found: " + email));
    }

    private Cart resolveCart(Long userId) {
        return cartRepo.findByUserIdWithItems(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cart not found for user id: " + userId));
    }

    private Product resolveProduct(Long productId) {
        return productRepo.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found with id: " + productId));
    }

    private CartItems resolveCartItem(Long cartItemId) {
        return cartItemsRepo.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cart item not found with id: " + cartItemId));
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getQuantity()) {
            throw new InsufficientStockException(
                "Only " + product.getQuantity()
                + " unit(s) available for '" + product.getName() + "'");
        }
    }

    // ─── Build CartDTO from Cart entity ───────────────────────
    // Computes subtotal per item and totalPrice for the whole cart
    private CartDTO buildCartDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
            .map(item -> {
                CartItemDTO dto = cartMapper.toDTO(item);
                // ✅ compute subtotal here — not in mapper, needs multiplication
                dto.setSubtotal(
                    item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                return dto;
            })
            .toList();

        BigDecimal totalPrice = itemDTOs.stream()
            .map(CartItemDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
            .id(cart.getId())
            .items(itemDTOs)
            .totalPrice(totalPrice)
            .updatedAt(cart.getUpdatedAt())
            .build();
    }
}