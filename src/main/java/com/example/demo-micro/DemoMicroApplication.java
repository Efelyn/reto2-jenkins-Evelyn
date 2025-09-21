package com.example.demo-micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoMicroApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoMicroApplication.class, args);
	}

	@GetMapping("/")
	public String home() {
		return "Hola, este es mi microservicio DevSecOps!";
	}
}
