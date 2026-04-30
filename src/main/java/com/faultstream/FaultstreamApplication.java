package com.faultstream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FaultstreamApplication {
	public static void main(String[] args) {
		SpringApplication.run(FaultstreamApplication.class, args);
	}
}
