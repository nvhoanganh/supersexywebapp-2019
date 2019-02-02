package com.rbus.scanworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import java.time.Duration;

@SpringBootApplication
@RestController
public class ScanworkerApplication {

	@RequestMapping("/")
	public ResponseEntity<String> healthz() {
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	public static void main(String[] args) {
		SpringApplication.run(ScanworkerApplication.class, args);
		runConsumer();
	}

	static void runConsumer() {
		var consumer = ConsumerCreator.createConsumer();
		int noMessageFound = 0;
		while (true) {
			var consumerRecords = consumer.poll(Duration.ofMillis(1000));
			if (consumerRecords.count() == 0) {
				noMessageFound++;
				if (noMessageFound > IKafkaConsumerConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					break;
				else
					continue;
			}
			consumerRecords.forEach(record -> {
				System.out.println("Record Key " + record.key());
				System.out.println("Record value " + record.value());
				System.out.println("Record partition " + record.partition());
				System.out.println("Record offset " + record.offset());
			});
			consumer.commitAsync();
		}
		consumer.close();
	}
}
