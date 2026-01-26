package com.ahmed.E_CommerceApp.conroller;

import com.ahmed.E_CommerceApp.dto.OrderDTO;
import com.ahmed.E_CommerceApp.model.Order;
import com.ahmed.E_CommerceApp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            Authentication connectedUser ,
            @RequestParam String address ,
            @RequestParam String phoneNumber
    ){
        return orderService.createOrder(connectedUser , address , phoneNumber);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders(){
         return orderService.getAllOrders();
    }

    @GetMapping("/user")
    public ResponseEntity<List<OrderDTO>> getUserOrders(Authentication connectedUser){
        return orderService.getUserOrders(connectedUser);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId
    , @RequestParam Order.OrderStatus status
    ){
        return orderService.updateOrderStatus(orderId , status);
    }


}
