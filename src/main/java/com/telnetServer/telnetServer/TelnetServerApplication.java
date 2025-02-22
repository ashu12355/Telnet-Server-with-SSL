package com.telnetServer.telnetServer;

import com.telnetServer.telnetServer.telnet.TelnetServerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TelnetServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelnetServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(TelnetServerService telnetServerService) {
		return args -> {
			// Start the Telnet server when the Spring Boot application runs
			telnetServerService.startTelnetServer();
		};
	}
}
