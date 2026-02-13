package com.codeguru.contextcompanion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API
            .cors(cors -> cors.disable()) // Configure CORS as needed
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Allow H2 console in dev
                .requestMatchers("/actuator/health").permitAll() // Health check
                .anyRequest().authenticated() // All other requests require authentication
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // Allow H2 console frames
            );

        return http.build();
    }
}
