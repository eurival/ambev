package com.ambev.sumarize_worker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ambev.sumarize_worker.service.OrderProcessorService;
import com.ambev.sumarize_worker.service.dto.OrderDTO;
import com.ambev.sumarize_worker.service.dto.OrderItemDTO;

@SpringBootTest
class SumarizeWorkerApplicationTests {

	@Autowired
	private OrderProcessorService orderProcessorService;

	@MockBean
	private RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	public void testProcessOrderCalcular() {
		OrderDTO order = new OrderDTO();
		order.setOrderId("1L");
		order.setItems(Arrays.asList(
				new OrderItemDTO(null, "prod1", "Produto 1", new BigDecimal("10.00"), 2, null),
				new OrderItemDTO(null, "prod2", "Produto 2", new BigDecimal("15.00"), null, null),
				new OrderItemDTO(null, "prod3", "Produto 3", null, 1, null)));

		orderProcessorService.processOrder(order);

		assertEquals(0, new BigDecimal("20.00").compareTo(order.getItems().get(0).getTotalPrice()));
		assertEquals(0, BigDecimal.ZERO.compareTo(order.getItems().get(1).getTotalPrice()));
		assertEquals(0, BigDecimal.ZERO.compareTo(order.getItems().get(2).getTotalPrice()));
		assertEquals(0, new BigDecimal("20.00").compareTo(order.getTotalValue()));
	}

	@Test
	public void testMultiplicaPorZero() {
		OrderDTO order = new OrderDTO();
		order.setOrderId("2L");
		order.setItems(Arrays.asList(
				new OrderItemDTO(null, "prod4", "Produto 4", new BigDecimal("0.00"), null, null), // Preço Unitário 0.00
				new OrderItemDTO(null, "prod5", "Produto 5", new BigDecimal("20.00"), 0, null) // Quantidade 0
		));

		orderProcessorService.processOrder(order);

		assertEquals(0, BigDecimal.ZERO.compareTo(order.getItems().get(0).getTotalPrice())); // 0.00 * 5 = 0.00
		assertEquals(0, BigDecimal.ZERO.compareTo(order.getItems().get(1).getTotalPrice())); // 20.00 * 0 = 0.00
		assertEquals(0, BigDecimal.ZERO.compareTo(order.getTotalValue())); // Soma dos totalPrice = 0.00
	}

}
