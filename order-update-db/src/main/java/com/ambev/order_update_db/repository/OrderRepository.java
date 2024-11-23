package com.ambev.order_update_db.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import com.ambev.order_update_db.service.dto.OrderDTO;

import jakarta.transaction.Transactional;

@Repository
public class OrderRepository {

    private JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void atualizarEmLote(List<OrderDTO> orders) {
        // Atualizar os pedidos
        String sqlOrder = "UPDATE tbl_order SET status = ?, total_value = ? WHERE order_id = ?";

        List<Object[]> batchArgsOrder = orders.stream()
            .map(order -> new Object[]{
                "PROCESSED", 
                order.getTotalValue(), 
                order.getOrderId()
            })
            .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sqlOrder, batchArgsOrder);

        // Atualizar os itens do pedido
        String sqlOrderItem = "UPDATE tbl_order_item SET total_price = ? WHERE id = ?";

        List<Object[]> batchArgsOrderItem = orders.stream()
            .flatMap(order -> order.getItems().stream()
                .map(item -> new Object[]{
                    item.getTotalPrice(), 
                    item.getId()
                })
            )
            .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sqlOrderItem, batchArgsOrderItem);
    }
}
