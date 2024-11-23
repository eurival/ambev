package com.ambev.sumarize_worker.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambev.sumarize_worker.config.RabbitMQConfig;
import com.ambev.sumarize_worker.domain.enumeration.OrderStatus;
import com.ambev.sumarize_worker.service.dto.OrderDTO;

@Service
public class OrderProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessorService.class);
    private final RabbitTemplate rabbitTemplate;


    public OrderProcessorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

    }

    @RabbitListener(queues = "pedidos-pendentes")
    @Transactional
    public void processOrder(OrderDTO order) {
        try {
            logger.info("Recebendo pedido para processamento: {}", order.getOrderId());
            
            // Calcula o valor total do pedido
            BigDecimal totalValue = order.getItems().stream()
                .map(item -> {
                    // Trata valores nulos para preço unitário
                    BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                    
                    // Trata valores nulos para quantidade e converte para BigDecimal
                    BigDecimal quantity = BigDecimal.ZERO;
                    if (item.getQuantity() != null) {
                        quantity = BigDecimal.valueOf(item.getQuantity().doubleValue());
                    }
                    item.setTotalPrice(unitPrice.multiply(quantity));
                    // Retorna o valor total do item (preço unitário * quantidade)
                    return item.getTotalPrice();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            order.setTotalValue(totalValue);
           
            logger.info("Pedido processado com sucesso: {}", order.getOrderId());
    
            // Envia para a fila de pedidos processados
            enviarParaFilaProcessados(order);
    
        } catch (Exception e) {
            logger.error("Erro ao processar pedido {}: {}", order.getOrderId(), e.getMessage(), e);
            // Salva dados no banco com integração falsa e adiciona à fila de falhas
            // Atualiza apenas o campo 'integrado' usando o método personalizado
            order.setIntegrado(false);
            enviarParaFilaDeFalhas(order);
        }
    }
    

    private void enviarParaFilaProcessados(OrderDTO order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PROCESSED_QUEUE,
            order
        );
        logger.info("Pedido enviado para a fila 'pedidos-processados': {}", order.getOrderId());
    }

    private void enviarParaFilaDeFalhas(OrderDTO order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FAILURES_QUEUE,
            order
        );
        logger.error("Pedido enviado para a fila 'pedidos-falhas': {}", order.getOrderId());
    }
}
