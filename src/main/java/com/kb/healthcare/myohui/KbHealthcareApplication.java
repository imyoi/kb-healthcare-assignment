package com.kb.healthcare.myohui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class KbHealthcareApplication {

	public static void main(String[] args) {
		SpringApplication.run(KbHealthcareApplication.class, args);
	}
}