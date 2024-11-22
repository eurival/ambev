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

@Configuration
public class RabbitMQConfig {


    public static final String UPDATED_QUEUE = "pedidos-atualizadosBD";
    public static final String UPDATED_ROUTING_KEY = "pedidos.atualizadosBD";
    public static final String EXCHANGE_NAME = "pedidos.exchange";
    public static final String FAILURES_UPDATE_QUEUE = "pedidos-falhou-update";
    public static final String FAILURES_UPDATE_ROUTING_KEY = "pedidos.falhou.update";
    


    /**
     * Declara a fila de pedidos processados.
     */
    @Bean
    public Queue criaFilhaPedidosUpdated() {
        return QueueBuilder.durable(UPDATED_QUEUE).build();
    }

    /**
     * Declara a exchange direta para gerenciar o roteamento de mensagens.
     */
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

  
    /**
     * Binding da fila de pedidos processados com a exchange.
     */
    @Bean
    public Binding bindingPedidosUpdated(Queue criaFilaPedidosUpdated, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(criaFilaPedidosUpdated).to(pedidosExchange).with(UPDATED_ROUTING_KEY);
    }


    @Bean
    public Queue criaFilaPedidosFalhouUpdate() {
        return QueueBuilder.durable(FAILURES_UPDATE_QUEUE).
        build();
    }
    
    @Bean
    public Binding bindingPedidosFalhouUpdate(Queue criaFilaPedidosFalhouUpdate, DirectExchange pedidosExchange) {
        return BindingBuilder.bind(criaFilaPedidosFalhouUpdate).to(pedidosExchange).with(FAILURES_UPDATE_ROUTING_KEY);
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
