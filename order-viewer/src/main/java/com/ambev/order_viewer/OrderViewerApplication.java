package com.ambev.order_viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class OrderViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderViewerApplication.class, args);
	}

}
