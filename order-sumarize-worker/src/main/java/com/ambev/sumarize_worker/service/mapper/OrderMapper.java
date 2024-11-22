package com.ambev.sumarize_worker.service.mapper;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ambev.sumarize_worker.domain.Order;
import com.ambev.sumarize_worker.domain.OrderItem;
import com.ambev.sumarize_worker.service.dto.OrderDTO;
import com.ambev.sumarize_worker.service.dto.OrderItemDTO;



@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderDTO dto); 

    OrderDTO toDTO(Order entity);

    List<OrderDTO> toDTOList(List<Order> entities);

    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDTO dto);

    OrderItemDTO toDTO(OrderItem entity);
}
