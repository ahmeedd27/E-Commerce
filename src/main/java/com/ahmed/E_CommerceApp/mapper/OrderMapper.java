package com.ahmed.E_CommerceApp.mapper;

import com.ahmed.E_CommerceApp.dto.OrderDTO;
import com.ahmed.E_CommerceApp.dto.OrderItemDTO;
import com.ahmed.E_CommerceApp.model.Order;
import com.ahmed.E_CommerceApp.model.OrderItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "user" , target="user.id")
    Order toOrder(OrderDTO orderDTO);
    @Mapping(source = "user.id" , target="user")
    OrderDTO toDto(Order order);
    @Mapping(source = "product.id" , target = "product")
    OrderItemDTO toOrderItemsDto(OrderItems orderItems);
    @Mapping(source = "product" , target = "product.id")
    OrderItems toOrderItems(OrderItemDTO orderItemsDto);
    List<OrderItemDTO> toOrderItemDTOs(List<OrderItems> orderItem);
    List<OrderItems> toOrderItemEntities(List<OrderItemDTO> orderItemDTO);
}
