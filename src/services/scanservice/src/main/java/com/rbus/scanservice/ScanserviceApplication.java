package com.rbus.scanservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ScanserviceApplication {

	@RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
		var d = "Hello";
		if (d.isBlank()) {
			System.out.println("it is blank");
		}
        return name;
    }		
	public static void main(String[] args) {
		SpringApplication.run(ScanserviceApplication.class, args);
	}
}
