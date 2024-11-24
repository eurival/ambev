package com.ambev.order_viewer.repository;

 
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ambev.order_viewer.domain.Order;
import com.ambev.order_viewer.domain.enumeration.OrderStatus;
import com.ambev.order_viewer.service.dto.OrderDTO;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
  
     Page<Order> findByStatus(OrderStatus status, Pageable pageable);
     
}

