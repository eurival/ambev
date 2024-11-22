package com.ambev.order_update_db;

import com.ambev.order_update_db.domain.enumeration.OrderStatus;
import com.ambev.order_update_db.repository.OrderRepository;
import com.ambev.order_update_db.service.OrderUpdateService;
import com.ambev.order_update_db.service.dto.OrderDTO;
import com.ambev.order_update_db.service.dto.OrderItemDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUpdateServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private OrderUpdateService orderUpdateService;

    @BeforeEach
    void setUp() {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);
        // Instancia o serviço com os mocks
        orderUpdateService = new OrderUpdateService(orderRepository, rabbitTemplate);
    }

    @Test
    void testAtualizarEmLote() {
        // Dados de teste
        List<OrderItemDTO> items = new ArrayList<>();
        OrderDTO order1 = new OrderDTO("211", null, BigDecimal.valueOf(1000.00), OrderStatus.PENDING, false, items);
        OrderDTO order2 = new OrderDTO("213", null, BigDecimal.valueOf(2000.00), OrderStatus.PENDING, false, items);
        OrderDTO order3 = new OrderDTO("212", Instant.now(), BigDecimal.valueOf(1000.00), OrderStatus.PENDING, true, items);

        List<OrderDTO> orders = Arrays.asList(order1, order2, order3);

        // Certifica de que o método não lança exceção
        doNothing().when(orderRepository).atualizarEmLote(anyList());

        // Chama o método a ser testado
        orderUpdateService.processarAtualizacaoLote(orders);

        // Verifica se o método atualizarEmLote foi chamado
        verify(orderRepository, times(1)).atualizarEmLote(eq(orders));

        // Verifica que não houve interação com o rabbitTemplate
        verifyNoInteractions(rabbitTemplate);
    }
}
