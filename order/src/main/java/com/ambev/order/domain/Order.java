package com.ambev.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ambev.order.domain.enumeration.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_order")
public class Order implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private String orderId; // Use UUID ou o identificador único fornecido
    private Instant orderDate;
    private BigDecimal totalValue;
    @Enumerated(EnumType.STRING) // ou EnumType.STRING
    private OrderStatus status;
    private Boolean integrado;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;




    
}