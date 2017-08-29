package com.example.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@SpringBootApplication
//@EnableZipkinStreamServer
public class ClientApplication {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean
	public Sampler defaultSampler() {
		return new AlwaysSampler();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@GetMapping("hi")
	@NewSpan("hiMethod")
	public String hi(@SpanTag("testTag") @RequestParam("param") String param) {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromUriString("http://localhost:8081/hi")
				.queryParam("param", param);
		logger.info("chamada do cliente hiMethod");
		return restTemplate().getForEntity(builder.toUriString(), String.class).getBody();
	}

	@GetMapping("method-with-error")
	@NewSpan("callMethodWithError")
	public String callMethodWithError(@SpanTag("testTag") @RequestParam("param") String param) {
		logger.info("chamada do cliente do metodo com error");
		try {
			String valor = "x";
			valor = null;
			valor.equals("x");
		} catch (Exception e) {
			logger.error("Chamada de metodo com error", e);
			throw new RuntimeException("Chamada de metodo com error", e);
		}
		return "ERROR";
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}
}
