package com.ambev.order.web.rest;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ambev.order.service.OrderService;
import com.ambev.order.service.criteria.OrderCriteria;
import com.ambev.order.service.dto.OrderDTO;
import com.ambev.order.web.util.PaginationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Criar um novo pedido", description = "Processa e salva um novo pedido com base nos dados fornecidos no OrderDTO.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso."),
        @ApiResponse(responseCode = "403", description = "Requisição sem autenticação."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "409", description = "Pedido duplicado detectado."),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor.")
    })    
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        orderService.processOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

            /**
     * {@code GET  /orders} : 
     *
     * @param pageable informação de paginação ex: /api/orders?sort=id&page=0&size=100
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
