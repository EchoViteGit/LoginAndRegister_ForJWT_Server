package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JwtBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtBackendApplication.class , args);
		System.out.println("------> JWT-Backend启动成功");
	}

}
