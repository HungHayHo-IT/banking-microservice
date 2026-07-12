package com.user_account_service_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "user account microservice REST API documentation",
				version = "v1",
				contact = @Contact(
						name = "HungHayHo-IT",
						email = "nguyenphuocnhahungl97@gmail.com"
				)
		)
)
public class UserAccountServiceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserAccountServiceServiceApplication.class, args);
	}

}
