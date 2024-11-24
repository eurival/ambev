package com.ambev.order_viewer.service.dto;


import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Long id;
    @NotNull(message = "O ID do produto é obrigatório.")
    private String productId;
    private String productName;

    @NotNull(message = "Valor unitiario é obrigatória.")
    private BigDecimal unitPrice;

    @NotNull(message = "A quantidade é obrigatória.")
    private Integer quantity;
    private BigDecimal totalPrice;

}
