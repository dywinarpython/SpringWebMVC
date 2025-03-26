package com.webapp.springBoot;

import com.webapp.springBoot.exception.EntyPointExceptionNoAuhenticaion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;



@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
				{
					authorizationManagerRequestMatcherRegistry.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
					authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();})
			.httpBasic(httpSecurityHttpBasicConfigurer -> {})
			.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new EntyPointExceptionNoAuhenticaion());})
			.csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}
}



