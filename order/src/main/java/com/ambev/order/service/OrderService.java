package com.ambev.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambev.order.config.RabbitMQConfig;
import com.ambev.order.domain.Order;
import com.ambev.order.domain.enumeration.OrderStatus;
import com.ambev.order.exception.DuplicateOrderException;
import com.ambev.order.exception.OrderProcessingException;
import com.ambev.order.repository.OrderRepository;
import com.ambev.order.service.criteria.OrderCriteria;
import com.ambev.order.service.dto.OrderDTO;
import com.ambev.order.service.mapper.OrderMapper;
import com.ambev.order.service.specification.OrderSpecification;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(RabbitTemplate rabbitTemplate, OrderRepository orderRepository, OrderMapper orderMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * Processa um pedido verificando duplicatas, salvando-o no repositório
     * e enviando-o para a fila de mensagens apropriada.
     *
     * @param orderDTO o objeto de transferência de dados contendo os detalhes do
     *                 pedido a ser processado.
     * @throws DuplicateOrderException  se o pedido já existir no repositório.
     * @throws OrderProcessingException se ocorrer algum erro durante o
     *                                  processamento do pedido.
     */
    @Transactional
    public void processOrder(OrderDTO orderDTO) {
        try {
            // Verifica duplicidade
            if (orderRepository.existsById(orderDTO.getOrderId())) {
                logger.warn("Pedido duplicado detectado: {}", orderDTO.getOrderId());
                try {
                    enviarPedidoDuplicadoParaFila(orderDTO);
                } catch (Exception ex) {
                    logger.error("Erro ao enviar pedido duplicado para a fila: {}", orderDTO.getOrderId(), ex);
                    throw new OrderProcessingException("Erro ao tratar pedido duplicado: " + orderDTO.getOrderId(), ex);
                }
                throw new DuplicateOrderException("Pedido já existe: " + orderDTO.getOrderId());
            }

            // Mapeia e salva o pedido
            Order order = orderMapper.toEntity(orderDTO);
            order.setStatus(OrderStatus.PENDING);
            order.setIntegrado(true);
            // Associa os itens ao pedido
            if (order.getItems() != null) {
                order.getItems().forEach(item -> item.setOrder(order));
            }
            orderRepository.save(order);

            // Tenta enviar para a fila de processamento
            try {
                enviarPedidoParaFila(orderDTO);
                logger.info("Pedido processado com sucesso: {}", order.getOrderId());

            } catch (RuntimeException e) { // implementa a resiliencia do sistema

                order.setIntegrado(false); // muda o status para informar que esse pedido não está integrado ao fluxo
                orderRepository.save(order); // posterior mente o job de tentativas irá tentar enviar para a fila
                                             // novamente

            } catch (Exception ex) {
                logger.error("Erro ao enviar pedido para a fila: {}", order.getOrderId(), ex);
                // Opcional: registrar pedido na fila de falhas
                enviarParaFilaDeFalhas(orderDTO);
                throw new OrderProcessingException("Erro ao enviar pedido para a fila: " + order.getOrderId(), ex);
            }

        } catch (DuplicateOrderException e) {
            // Já tratado acima
            throw e;
        } catch (Exception e) {
            // Lida com erros genéricos
            enviarParaFilaDeFalhas(orderDTO);
            logger.error("Erro ao processar o pedido {}: {}", orderDTO.getOrderId(), e.getMessage(), e);
            // throw new OrderProcessingException("Erro ao processar o pedido: " +
            // orderDTO.getOrderId(), e);
        }
    }

    public void enviarPedidoParaFila(OrderDTO order) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.PENDING_ROUTING_KEY,
                order);

        logger.info("Pedido enviado para fila 'pedidos-pendentes': {}", order.getOrderId());
    }

    private void enviarPedidoDuplicadoParaFila(OrderDTO orderDTO) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.DUPLICATE_ROUTING_KEY,
                orderDTO);
        logger.info("Pedido duplicado enviado para fila 'pedidos-duplicados': {}", orderDTO.getOrderId());
    }

    private void enviarParaFilaDeFalhas(OrderDTO orderDTO) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.FAILURES_ROUTING_KEY,
                orderDTO);
        logger.info("Pedido duplicado enviado para fila 'pedidos-duplicados': {}", orderDTO.getOrderId());
    }

    public Page<OrderDTO> findAllOrders(OrderCriteria criteria, Pageable pageable) {
        Specification<Order> specification = OrderSpecification.createSpecification(criteria);
        return orderRepository.findAll(specification, pageable).map(orderMapper::toDTO);
    }
    

}
