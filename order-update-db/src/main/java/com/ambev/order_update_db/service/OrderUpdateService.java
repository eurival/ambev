package com.ambev.order_update_db.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambev.order_update_db.config.RabbitMQConfig;
import com.ambev.order_update_db.repository.OrderRepository;
import com.ambev.order_update_db.service.dto.OrderDTO;

@Service
public class OrderUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(OrderUpdateService.class);
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;

    public OrderUpdateService(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void processarAtualizacaoLote(List<OrderDTO> orders) {
        try {
            orderRepository.atualizarEmLote(orders);
            enviarParaFilaDeUpdated(orders);
        } catch (Exception e) {
            logger.error("Erro ao atualizar lote de pedidos: {}", e.getMessage(), e);
            enviarParaFilaDeFalhas(orders); // Envia pedidos falhos para DLQ
        }
    }

    public void enviarParaFilaDeFalhas(List<OrderDTO> orders) {
        for (OrderDTO order : orders) {
            try {
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,    // Exchange
                    RabbitMQConfig.FAILURES_UPDATE_ROUTING_KEY,  // Routing key
                    order                     // Mensagem
                );
                logger.warn("Pedido enviado para DLQ: {}", order.getOrderId());
            } catch (Exception e) {
                logger.error("Erro ao enviar pedido ID={} para DLQ: {}", order.getOrderId(), e.getMessage(), e);
                // Aqui  pode salvar o pedido em um fallback, como um banco de dados, para garantir que ele não seja perdido
                salvarEmFallback(order);
            }
        }
    }

    public void enviarParaFilaDeUpdated(List<OrderDTO> orders) {
        for (OrderDTO order : orders) {
            try {
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,    // Exchange
                    RabbitMQConfig.UPDATED_ROUTING_KEY,  // Routing key
                    order                     // Mensagem
                );
                logger.warn("Pedido enviado para Atualizados: {}", order.getOrderId());
            } catch (Exception e) {
                logger.error("Erro ao enviar pedido ID={} para Atualizados: {}", order.getOrderId(), e.getMessage(), e);
                
                salvarEmFallback(order);
            }
        }
    }

    private void salvarEmFallback(OrderDTO order) {
        // Lógica para salvar o pedido em um banco de dados ou outro armazenamento seguro
        logger.warn("Pedido ID={} salvo no fallback para reprocessamento posterior.", order.getOrderId());
        // Exemplo: repositoryFallback.save(order);
    }
}
