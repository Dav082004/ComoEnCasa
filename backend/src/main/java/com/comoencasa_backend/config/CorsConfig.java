package com.comoencasa_backend.config;

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
                registry.addMapping("/**") // Aplica CORS a todas las rutas
                        .allowedOrigins("http://localhost:3000", "http://localhost:3001") // Asegura ambos orígenes
                        .allowedMethods("*") // Permite todos los métodos (GET, POST, etc.)
                        .allowedHeaders("*") // Permite todos los encabezados
                        .allowCredentials(true); // Necesario si usas cookies o auth con tokens
            }
        };
    }
}
