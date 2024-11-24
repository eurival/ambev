package com.ambev.order_viewer.service;

 
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambev.order_viewer.domain.Order;

import com.ambev.order_viewer.repository.OrderRepository;
import com.ambev.order_viewer.service.criteria.OrderCriteria;
import com.ambev.order_viewer.service.dto.OrderDTO;
import com.ambev.order_viewer.service.mapper.OrderMapper;
import com.ambev.order_viewer.service.specification.OrderSpecification;

@Service
public class OrderService {
   // private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

 
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(  OrderRepository orderRepository, OrderMapper orderMapper) {
 
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
    @Transactional
   // @Cacheable(value = "orders", key = "#criteria.toString() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<OrderDTO> findAllOrders(OrderCriteria criteria, Pageable pageable) {
        Specification<Order> specification = OrderSpecification.createSpecification(criteria);
        return orderRepository.findAll(specification, pageable).map(orderMapper::toDTO);
    }

}
