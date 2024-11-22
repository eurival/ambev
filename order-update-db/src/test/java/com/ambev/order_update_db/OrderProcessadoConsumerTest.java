package com.ambev.order_update_db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ambev.order_update_db.consumer.OrderProcessadoConsumer;
import com.ambev.order_update_db.service.OrderUpdateService;
import com.ambev.order_update_db.service.dto.OrderDTO;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessadoConsumerTest {

    @Mock
    private OrderUpdateService orderUpdateService;

    private OrderProcessadoConsumer orderProcessadoConsumer;

    @BeforeEach
    void setUp() {
        // Inicialize o consumidor com o serviço mockado
        orderProcessadoConsumer = new OrderProcessadoConsumer(orderUpdateService);
    }

    // Começamos os testes

    @Test // Testar o Método consumir Quando o Buffer Não Está Cheio
    void testConsumir_BufferNotFull_OrderAddedToBuffer() {
        // Dados testes
        OrderDTO order = new OrderDTO();
        order.setOrderId("123");

        // Act
        orderProcessadoConsumer.consumir(order);

        // Usando reflexão para acessar o buffer, porque ele é um campo privado, e
        // verificar se o pedido foi adicionado
        BlockingQueue<OrderDTO> buffer = getBufferFromConsumer();
        assertTrue(buffer.contains(order));
    }

    private BlockingQueue<OrderDTO> getBufferFromConsumer() {
        try {
            Field bufferField = OrderProcessadoConsumer.class.getDeclaredField("buffer");
            bufferField.setAccessible(true);
            return (BlockingQueue<OrderDTO>) bufferField.get(orderProcessadoConsumer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Método de teste para o método {@link OrderProcessadoConsumer#consumir(OrderDTO)} quando o buffer está cheio.
     *
     * <p>Este caso de teste simula o cenário em que o buffer já está em sua capacidade máxima.
     * Um pedido é adicionado ao buffer, e o método em teste deve lidar com essa situação enviando o pedido para a fila de falhas (DLQ).
     *
     * @throws Exception Se ocorrer um erro durante o teste.
     */

    @Test // Testar o Método consumir Quando o Buffer Está Cheio
    void testConsumir_BufferFull_OrderSentToDLQ() throws Exception {
        // Arrange
        // Cria uma nova instância de OrderDTO com um ID de pedido específico.
        OrderDTO order = new OrderDTO();
        order.setOrderId("123");

        // Preenche o buffer até sua capacidade máxima.
        // Isso é feito adicionando novas instâncias de OrderDTO ao buffer.
        BlockingQueue<OrderDTO> buffer = getBufferFromConsumer();
        for (int i = 0; i < 1000; i++) {
            buffer.offer(new OrderDTO());
        }

        // Spy no consumidor para verificar chamadas a métodos privados
        OrderProcessadoConsumer spyConsumer = spy(orderProcessadoConsumer);
        // Act
        spyConsumer.consumir(order);
        // Assert
        // Verifica se o pedido não foi adicionado ao buffer.
        // Como o buffer está cheio, o pedido deve ter sido enviado para a DLQ.
        assertFalse(buffer.contains(order));

        verify(orderUpdateService, times(1)).enviarParaFilaDeFalhas(argThat(orders -> 
            orders.size() == 1 && orders.get(0).equals(order)
        ));
    }

    /* 
        Testar o Método processarBuffer Com Pedidos no Buffer
        Verificar se os pedidos são processados corretamente em lotes.
    */
    @Test
    void testProcessarBuffer_WithOrders_CallsProcessarAtualizacaoLote() throws Exception {
        // Arrange
        BlockingQueue<OrderDTO> buffer = getBufferFromConsumer();
        OrderDTO order1 = new OrderDTO();
        order1.setOrderId("123");
        buffer.offer(order1);

        OrderDTO order2 = new OrderDTO();
        order2.setOrderId("456");
        buffer.offer(order2);

        // Act
        orderProcessadoConsumer.processarBuffer();

        // Assert
        ArgumentCaptor<List<OrderDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderUpdateService, times(1)).processarAtualizacaoLote(captor.capture());

        List<OrderDTO> processedOrders = captor.getValue();
        assertEquals(2, processedOrders.size());
        assertTrue(processedOrders.contains(order1));
        assertTrue(processedOrders.contains(order2));

        assertTrue(buffer.isEmpty());

        // Verificar que enviarParaFilaDeFalhas não foi chamado
        verify(orderUpdateService, times(0)).enviarParaFilaDeFalhas(anyList());
    }
    /*
     * Testar o Método processarBuffer Quando processarAtualizacaoLote Lança Exceção
     */
    @Test
    void testProcessarBuffer_QuandProcessarAtualizacaoLoteLancaException_SendsToDLQ() {
        // Arrange
        BlockingQueue<OrderDTO> buffer = getBufferFromConsumer();
        OrderDTO order1 = new OrderDTO();
        order1.setOrderId("123");
        buffer.offer(order1);

        doThrow(new RuntimeException("Erro ao processar lote"))
                .when(orderUpdateService).processarAtualizacaoLote(anyList());

        // Act
        orderProcessadoConsumer.processarBuffer();

        // Assert
        verify(orderUpdateService, times(1)).processarAtualizacaoLote(anyList());
        verify(orderUpdateService, times(1)).enviarParaFilaDeFalhas(argThat(orders -> 
            orders.size() == 1 && orders.get(0).equals(order1)
        ));
    }

}
