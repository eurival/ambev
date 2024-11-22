package com.ambev.order.service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ambev.order.domain.Order;
import com.ambev.order.domain.OrderItem;
import com.ambev.order.service.dto.OrderDTO;
import com.ambev.order.service.dto.OrderItemDTO;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderDTO dto); 

    OrderDTO toDTO(Order entity);

    List<OrderDTO> toDTOList(List<Order> entities);

    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDTO dto);

    OrderItemDTO toDTO(OrderItem entity);
}
