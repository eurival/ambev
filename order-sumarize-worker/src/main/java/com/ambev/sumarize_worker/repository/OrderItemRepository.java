package com.ambev.sumarize_worker.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ambev.sumarize_worker.domain.OrderItem;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Métodos de consulta personalizados, se necessário
}
