package com.ambev.order_viewer.service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ambev.order_viewer.service.dto.OrderItemDTO;
import com.ambev.order_viewer.domain.enumeration.OrderStatus;
import lombok.Data;

@Data
public class OrderDTO {
    private String orderId;
    private Instant orderDate;
    private BigDecimal totalValue;
    private OrderStatus status;
    private Boolean integrado;
    private List<OrderItemDTO> items;


}
