package com.example.apartmanagebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Se aplica a todos nuestros Endpoints (empiezan por /api/v1/...)
                        .allowedOrigins("http://localhost:4200") // Damos permiso explícito a nuestro Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                        .allowedHeaders("*") // Permitimos cualquier cabecera (vital para enviar el Token JWT)
                        .allowCredentials(true); // Permitimos el envío de credenciales/cookies
            }
        };
    }
}