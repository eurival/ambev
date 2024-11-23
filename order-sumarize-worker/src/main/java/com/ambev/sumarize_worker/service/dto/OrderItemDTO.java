package com.ambev.sumarize_worker.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


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

    


    public OrderItemDTO() {
    }
    public OrderItemDTO(Long id, @NotNull(message = "O ID do produto é obrigatório.") String productId,
            String productName, @NotNull(message = "Valor unitiario é obrigatória.") BigDecimal unitPrice,
            @NotNull(message = "A quantidade é obrigatória.") Integer quantity, BigDecimal totalPrice) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

}
