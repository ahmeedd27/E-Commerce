package com.ahmed.E_CommerceApp.service;

import com.ahmed.E_CommerceApp.dao.OrderRepo;
import com.ahmed.E_CommerceApp.dao.ProductRepo;
import com.ahmed.E_CommerceApp.dto.CartDTO;
import com.ahmed.E_CommerceApp.dto.OrderDTO;
import com.ahmed.E_CommerceApp.exception.InsufficientStockException;
import com.ahmed.E_CommerceApp.exception.ResourceNotFoundException;
import com.ahmed.E_CommerceApp.mapper.CartMapper;
import com.ahmed.E_CommerceApp.mapper.OrderMapper;
import com.ahmed.E_CommerceApp.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepo productRepo;
    private final CartService cartService;
    private final CartMapper cartMapper;
    private final OrderRepo orderRepo;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public ResponseEntity<List<OrderDTO>> getAllOrders() {
       List<Order> orders= orderRepo.findAll();
       List<OrderDTO> ordersDto= orders.stream().map(orderMapper::toDto).collect(Collectors.toList());
       return ResponseEntity.ok(ordersDto);
    }


    public List<OrderItems> createOrderItems(Cart cart , Order order){

        return cart.getItems().stream().map(cartItem ->
        {
            Product product=productRepo.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
            if(product.getQuantity()==null){
                throw new IllegalStateException("Product quantity is not set for product "+product.getName());
            }
            if(product.getQuantity()< cartItem.getQuantity()){
                throw new InsufficientStockException("Not enough stock for product "+product.getName());
            }
            product.setQuantity(product.getQuantity()- cartItem.getQuantity());
            productRepo.save(product);
            return new OrderItems(null , order , product , cartItem.getQuantity(), product.getPrice());
        }).collect(Collectors.toList());

    }

    @Transactional
    public ResponseEntity<OrderDTO> createOrder(Authentication connectedUser , String address , String phoneNumber){
        User user=(User) connectedUser.getPrincipal();
        if(!user.isEmailConfirmation()){
            throw new IllegalStateException("Email not confirmed. Please confirm email before placing order");
        }
        ResponseEntity<CartDTO> cartDTO=cartService.getCart(connectedUser);
        CartDTO cartDTO1=cartDTO.getBody();
        Cart cart=cartMapper.toCart(cartDTO1);
        if(cart.getItems().isEmpty()){
            throw new IllegalStateException("Cannot create an order with an empty cart");
        }
        Order order=new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setItems(createOrderItems(cart , order));
        Order savedOrder=orderRepo.save(order);
        try{
            emailService.sendOrderConfirmation(savedOrder);
        }catch (MailException e){
            logger.error("Failed to send order confirmation email for order ID "+savedOrder.getId(), e);
        }
           return ResponseEntity.ok(orderMapper.toDto(savedOrder));
    }

    public ResponseEntity<List<OrderDTO>> getUserOrders(Authentication connectedUser) {
        User user=(User) connectedUser.getPrincipal();
        List<Order> orders=orderRepo.findByUserId(user.getId());
        List<OrderDTO> ordersDto=orders.stream().map(orderMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(ordersDto);
    }

    public ResponseEntity<OrderDTO> updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order=orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        order.setStatus(status);
        return ResponseEntity.ok(orderMapper.toDto(orderRepo.save(order)));
    }
}
