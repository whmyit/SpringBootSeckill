package com.dxhy.order.ordermail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class OrderMailApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(OrderMailApplication.class, args);
	}

	/*@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OrderMailApplication.class);
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(OrderMailApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}*/
}
