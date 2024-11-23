package com.ambev.order_update_db.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitMQConfig {

    public static final String UPDATED_QUEUE = "pedidos-atualizadosBD";
    public static final String UPDATED_ROUTING_KEY = "pedidos.atualizadosBD";
    public static final String EXCHANGE_NAME = "pedidos.exchange";
    public static final String FAILURES_UPDATE_QUEUE = "pedidos-falhou-update";
    public static final String FAILURES_UPDATE_ROUTING_KEY = "pedidos.falhou.update";

    // Adicione esta constante
    public static final String PROCESSED_QUEUE = "pedidos-processados";

    @Bean
    public Queue criaFilhaPedidosUpdated() {
        return QueueBuilder.durable(UPDATED_QUEUE).build();
    }

    @Bean
    public Queue criaFilaPedidosFalhouUpdate() {
        return QueueBuilder.durable(FAILURES_UPDATE_QUEUE).build();
    }

    // fila 'pedidos-processados'
    @Bean
    public Queue criaFilaPedidosProcessados() {
        return QueueBuilder.durable(PROCESSED_QUEUE).build();
    }

    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingPedidosUpdated(
            @Qualifier("criaFilhaPedidosUpdated") Queue updatedQueue, 
            DirectExchange pedidosExchange) {
        return BindingBuilder.bind(updatedQueue).to(pedidosExchange).with(UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingPedidosFalhouUpdate(
            @Qualifier("criaFilaPedidosFalhouUpdate") Queue failuresQueue, 
            DirectExchange pedidosExchange) {
        return BindingBuilder.bind(failuresQueue).to(pedidosExchange).with(FAILURES_UPDATE_ROUTING_KEY);
    }

    // Binding para a  fila 'pedidos-processados'
    @Bean
    public Binding bindingPedidosProcessados(
            @Qualifier("criaFilaPedidosProcessados") Queue processedQueue, 
            DirectExchange pedidosExchange) {
        return BindingBuilder.bind(processedQueue).to(pedidosExchange).with("pedidos.processados");
    }

    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializarAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }
}
