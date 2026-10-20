package com.unand.asrama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AsramaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsramaBackendApplication.class, args);
	}

}
