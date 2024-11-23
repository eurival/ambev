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


public class OrderDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O ID do pedido é obrigatório.")
    private String orderId;
    private Instant orderDate;
    private BigDecimal totalValue;
    private OrderStatus status;
    private Boolean  integrado=false;
    
    @NotEmpty(message = "A lista de itens não pode estar vazia.")
    @Size(min = 1, message = "O pedido deve conter pelo menos um item.")
    private List<OrderItemDTO> items;

    public OrderDTO() {
    }

    public OrderDTO(@NotNull(message = "O ID do pedido é obrigatório.") String orderId, Instant orderDate,
            BigDecimal totalValue, OrderStatus status, Boolean integrado,
            @NotEmpty(message = "A lista de itens não pode estar vazia.") @Size(min = 1, message = "O pedido deve conter pelo menos um item.") List<OrderItemDTO> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalValue = totalValue;
        this.status = status;
        this.integrado = integrado;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Boolean getIntegrado() {
        return integrado;
    }

    public void setIntegrado(Boolean integrado) {
        this.integrado = integrado;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
    
}
