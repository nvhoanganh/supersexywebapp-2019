package com.rbus.scanservice;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ScanserviceApplication {
	private static Producer<String, String> producer;

	@RequestMapping("/")
	public ResponseEntity<String> healthz() {
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@RequestMapping("/scan")
	public ResponseEntity<String> scan(@RequestParam(value = "url") String url) {
		if (url.isBlank()) {
			return new ResponseEntity<>("url must be supplied", HttpStatus.BAD_REQUEST);
		}
		try {
			ScanserviceApplication.send(url);
			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ScanserviceApplication.class, args);
		producer = ProducerCreator.createProducer();
	}

	private static void send(String url) throws Exception {
		var record = new ProducerRecord<String, String>(IKafkaConstants.TOPIC_NAME, url);
		try {
			var metadata = producer.send(record).get();
			System.out
					.println("Record sent to partition " + metadata.partition() + " with offset " + metadata.offset());
		} catch (ExecutionException e) {
			System.out.println("Error in sending record");
			System.out.println(e);
			throw e;
		} catch (InterruptedException e) {
			System.out.println("Error in sending record");
			System.out.println(e);
			throw e;
		}
	}
}