package com.alibaba.assistant.agent.start.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Value("${dhgate.assistant.cors.allowed-origins:https://open.dhgate.com}")
	private String allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String[] origins = split(allowedOrigins);
		registry.addMapping("/api/**")
				.allowedOrigins(origins)
				.allowedMethods("GET", "POST", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}

	private String[] split(String s) {
		if (s == null || s.isBlank()) {
			return new String[0];
		}
		return s.trim().split("\\s*,\\s*");
	}
}

