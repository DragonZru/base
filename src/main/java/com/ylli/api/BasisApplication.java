package com.ylli.api;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BasisApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasisApplication.class, args);
	}

	@PostConstruct
	void defaultTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}

}
