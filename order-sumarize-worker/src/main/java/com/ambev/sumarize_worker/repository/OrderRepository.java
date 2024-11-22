package com.ambev.sumarize_worker.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ambev.sumarize_worker.domain.Order;
import com.ambev.sumarize_worker.domain.enumeration.OrderStatus;


@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
     @SuppressWarnings("null")
     Page<Order> findAll(Pageable pageable);
     Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}

