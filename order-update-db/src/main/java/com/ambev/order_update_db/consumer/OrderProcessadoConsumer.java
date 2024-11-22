package com.ambev.order_update_db.consumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ambev.order_update_db.service.OrderUpdateService;
import com.ambev.order_update_db.service.dto.OrderDTO;

@Component
public class OrderProcessadoConsumer implements Serializable{

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessadoConsumer.class);
    private final OrderUpdateService orderUpdateService;
    private final BlockingQueue<OrderDTO> buffer = new LinkedBlockingQueue<>(1000); // Capacidade máxima
    private final int BATCH_SIZE = 100; // Tamanho do lote

    public OrderProcessadoConsumer(OrderUpdateService orderUpdateService) {
        this.orderUpdateService = orderUpdateService;
    }

    @Scheduled(fixedDelay = 1000) // Executa a cada 1 segundo
    public void processarBuffer() {
        List<OrderDTO> toProcess = new ArrayList<>();
        buffer.drainTo(toProcess, BATCH_SIZE); // Remove até BATCH_SIZE itens da fila

        if (!toProcess.isEmpty()) { // Processa somente se houver pedidos
            try {
                orderUpdateService.processarAtualizacaoLote(toProcess);
                
            } catch (Exception e) {
                logger.error("Erro ao processar buffer: {}", e.getMessage());
                enviarParaFilaDeFalhas(toProcess);
            }
        }
    }

    @RabbitListener(queues = "pedidos-processados")
    public void consumir(OrderDTO order) {
        logger.info("Adicionado ao buffer: {}", order.getOrderId());
        if (!buffer.offer(order)) { // Adiciona ao buffer, rejeitando se estiver cheio
            logger.error("Buffer cheio! Pedido descartado: {}", order.getOrderId());
            enviarParaFilaDeFalhas(List.of(order)); // Opcional: Tratar o pedido descartado
        }
    }

    private void enviarParaFilaDeFalhas(List<OrderDTO> orderFalhos) {
        orderUpdateService.enviarParaFilaDeFalhas(orderFalhos);
        for (OrderDTO pedido : orderFalhos) {
            logger.warn("Pedido enviado para DLQ: {}", pedido.getOrderId());
        }

    }
}
