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

    // para visualizar as orders utilize o microservice order-viewer
    
}
