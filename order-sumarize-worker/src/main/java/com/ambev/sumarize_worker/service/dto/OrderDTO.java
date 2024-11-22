package com.ambev.sumarize_worker.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ambev.sumarize_worker.domain.enumeration.OrderStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O ID do pedido é obrigatório.")
    private String orderId;
    private Instant orderDate;
    private BigDecimal totalValue;
    private OrderStatus status;
    private Boolean  integrado;
    
    @NotEmpty(message = "A lista de itens não pode estar vazia.")
    @Size(min = 1, message = "O pedido deve conter pelo menos um item.")
    private List<OrderItemDTO> items;
}
