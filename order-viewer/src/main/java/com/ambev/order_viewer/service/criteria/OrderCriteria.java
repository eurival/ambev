package com.ambev.order_viewer.service.criteria;


import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import com.ambev.order_viewer.domain.enumeration.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

@ParameterObject
public class OrderCriteria {

    private String orderId;
    private OrderStatus status;
    private Boolean integrado;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant orderDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant orderDateTo;

    private BigDecimal totalValueMin;
    private BigDecimal totalValueMax;

    // Construtor sem argumentos
    public OrderCriteria() {
    }

    // Getters e Setters

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public Instant getOrderDateFrom() {
        return orderDateFrom;
    }

    public void setOrderDateFrom(Instant orderDateFrom) {
        this.orderDateFrom = orderDateFrom;
    }

    public Instant getOrderDateTo() {
        return orderDateTo;
    }

    public void setOrderDateTo(Instant orderDateTo) {
        this.orderDateTo = orderDateTo;
    }

    public BigDecimal getTotalValueMin() {
        return totalValueMin;
    }

    public void setTotalValueMin(BigDecimal totalValueMin) {
        this.totalValueMin = totalValueMin;
    }

    public BigDecimal getTotalValueMax() {
        return totalValueMax;
    }

    public void setTotalValueMax(BigDecimal totalValueMax) {
        this.totalValueMax = totalValueMax;
    }

    // Você pode adicionar métodos auxiliares, equals, hashCode e toString se necessário
}
