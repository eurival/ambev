package com.ambev.order_update_db.consumer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ambev.order_update_db.config.RabbitMQConfig;
import com.ambev.order_update_db.service.dto.OrderDTO;

/*
 * @eurival
 * @author
 * Description: Componente responsavel por reprocessar a cada 60 segundos as orders com falha na
 * atualizão no bd
 */
@Component
public class ReprocessarFilaDeFalhas {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessadoConsumer.class);
    private final RabbitTemplate rabbitTemplate;

    public ReprocessarFilaDeFalhas(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 60000) // Executa a cada 60 segundos
    public void reprocessarFalhas() {
        List<OrderDTO> falhas = new ArrayList<>();
        OrderDTO order;
        while ((order = (OrderDTO) rabbitTemplate.receiveAndConvert(RabbitMQConfig.FAILURES_UPDATE_QUEUE)) != null) {
            falhas.add(order);
        }

        if (!falhas.isEmpty()) {
            logger.info("Reprocessando {} pedidos da fila de falhas", falhas.size());
            // Envia novamente para a fila de processados
            for (OrderDTO pedido : falhas) {
                rabbitTemplate.convertAndSend(RabbitMQConfig.PROCESSED_QUEUE, pedido);
            }
        }
    }
}
