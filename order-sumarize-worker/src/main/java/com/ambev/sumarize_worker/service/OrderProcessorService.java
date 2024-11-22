package com.ambev.sumarize_worker.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambev.sumarize_worker.config.RabbitMQConfig;
import com.ambev.sumarize_worker.domain.Order;
import com.ambev.sumarize_worker.domain.enumeration.OrderStatus;
import com.ambev.sumarize_worker.repository.OrderRepository;

@Service
public class OrderProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessorService.class);
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;

    public OrderProcessorService(RabbitTemplate rabbitTemplate, OrderRepository orderRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderRepository = orderRepository;
    }

    @RabbitListener(queues = "pedidos-pendentes")
    @Transactional
    public void processOrder(Order order) {
        try {
            logger.info("Recebendo pedido para processamento: {}", order.getOrderId());

            // Processa os itens do pedido e atualiza o valor total
            BigDecimal totalValue = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalValue(totalValue);

            // Atualiza o status e salva no banco
            order.setStatus(OrderStatus.PROCESSED);
            order.setIntegrada(true);

            logger.info("Pedido processado com sucesso: {}", order.getOrderId());

            // Envia para a fila de pedidos processados
            enviarParaFilaProcessados(order);

        } catch (Exception e) {
            logger.error("Erro ao processar pedido {}: {}", order.getOrderId(), e.getMessage(), e);
            // salva dados no banco porem com integração falsa, e adiciona a fila de falha 
            order.setIntegrada(false);
            orderRepository.save(order);
            enviarParaFilaDeFalhas(order);
        }
    }

    private void enviarParaFilaProcessados(Order order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PROCESSED_QUEUE,
            order
        );
        logger.info("Pedido enviado para a fila 'pedidos-processados': {}", order.getOrderId());
    }

    private void enviarParaFilaDeFalhas(Order order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FAILURES_QUEUE,
            order
        );
        logger.error("Pedido enviado para a fila 'pedidos-falhas': {}", order.getOrderId());
    }
}
