package com.ambev.order_viewer.web.rest;

 
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
 
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ambev.order_viewer.service.OrderService;
import com.ambev.order_viewer.service.criteria.OrderCriteria;
import com.ambev.order_viewer.service.dto.OrderDTO;
import com.ambev.order_viewer.web.util.PaginationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
 

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * {@code GET  /orders} : 
     *
     * @param pageable informação de paginação ex: /api/orders?sort=id&page=0&size=100
     * @param criteria tamtem pode ser usado, exemplo: /api/orders?orderId=1 ou 
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e a lista de order no body.
     */

    @Operation(summary = "Retorna todas as orders", description = "Retorna uma lista de ordens com opcional filtragem.")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully.")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(
            @ParameterObject OrderCriteria criteria,
            @ParameterObject Pageable pageable) {
    
        Page<OrderDTO> page = orderService.findAllOrders(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(), page);
    
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
    


    
}
