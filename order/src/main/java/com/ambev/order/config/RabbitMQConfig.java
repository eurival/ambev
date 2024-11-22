package com.ambev.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PENDING_QUEUE = "pedidos-pendentes";
    public static final String EXCHANGE_NAME = "pedidos.exchange";
    public static final String PENDING_ROUTING_KEY = "pedidos.pendentes";
    public static final String DUPLICATE_QUEUE = "pedidos-duplicados";
    public static final String DUPLICATE_ROUTING_KEY = "pedidos.duplicados";
    public static final String FAILURES_QUEUE = "pedidos-falhou";
    public static final String FAILURES_ROUTING_KEY = "pedidos.falhou";
    

    /**
     * Declara a fila de pedidos pendentes.
     */
    @Bean
    public Queue criaFilhaPedidosPendentes() {
        return QueueBuilder.durable(PENDING_QUEUE).build();
    }

    /**
     * Declara a exchange direta para gerenciar o roteamento de mensagens.
     */
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    /**
     * Binding da fila de pedidos pendentes com a exchange.
     */
    @Bean
    public Binding bindingPedidosPendentes(Queue criaFilhaPedidosPendentes, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(criaFilhaPedidosPendentes).to(pedidosExchange).with(PENDING_ROUTING_KEY);
    }



    @Bean
    public Queue criaFilaPedidosDuplicados() {
        return QueueBuilder.durable(DUPLICATE_QUEUE).
        build();
    }
    
    @Bean
    public Binding bindingPedidosDuplicados(Queue criaFilaPedidosDuplicados, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(criaFilaPedidosDuplicados).to(pedidosExchange).with(DUPLICATE_ROUTING_KEY);
    }
    @Bean
    public Queue criaFilaPedidosFalhou() {
        return QueueBuilder.durable(FAILURES_QUEUE).
        build();
    }
    
    @Bean
    public Binding bindingPedidosFalhou(Queue criaFilaPedidosFalhou, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(criaFilaPedidosFalhou).to(pedidosExchange).with(FAILURES_ROUTING_KEY);
    }
    /**
     * Declara um RabbitAdmin para gerenciar filas, exchanges e bindings.
     */
    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Configura o RabbitTemplate para usar o Jackson2JsonMessageConverter.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * Cria um bean do Jackson2JsonMessageConverter.
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    /**
     * Inicializa o RabbitAdmin após a aplicação estar pronta.
     */
    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializarAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    
}
