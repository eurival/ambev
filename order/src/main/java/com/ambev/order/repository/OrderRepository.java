package com.ambev.order.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ambev.order.domain.Order;
import com.ambev.order.domain.enumeration.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
     @SuppressWarnings("null")
     Page<Order> findAll(Pageable pageable);
     Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}

