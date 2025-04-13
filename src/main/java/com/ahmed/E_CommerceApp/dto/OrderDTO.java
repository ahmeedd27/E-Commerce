package com.ahmed.E_CommerceApp.dto;

import com.ahmed.E_CommerceApp.model.Order;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long user;
    @NotBlank(message = "address is required")
    @NotEmpty(message = "address is required")
    private String address;
    @NotBlank(message = "phone number is required")
    @NotEmpty(message = "phone number is required")
    private String phoneNumber;
    private Order.OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;

}
