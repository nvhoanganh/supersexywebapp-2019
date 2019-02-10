package com.rbus.scanworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@SpringBootApplication
@RestController
public class ConsumerApp {

	@RequestMapping("/healthz")
	public ResponseEntity<String> healthz() {
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApp.class, args);
	}
}
