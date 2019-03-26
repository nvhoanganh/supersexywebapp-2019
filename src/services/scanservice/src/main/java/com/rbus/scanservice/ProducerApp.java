package com.rbus.scanservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.boot.SpringApplication;
@SpringBootApplication
@RequestMapping(value = "/")
public class ProducerApp {
	private final Producer producer;

	@Autowired
	ProducerApp(Producer producer) {
		 this.producer = producer;
	}
	@GetMapping(value = "/scan")
	public ResponseEntity<String> sendMessageToKafkaTopic(@RequestParam("url") String url) {
		 this.producer.sendMessage("scan-request",url);
		 return new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@RequestMapping("/healthz")
	public ResponseEntity<String> healthz() {
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
	public static void main(String[] args) {
		SpringApplication.run(ProducerApp.class, args);
	}
}