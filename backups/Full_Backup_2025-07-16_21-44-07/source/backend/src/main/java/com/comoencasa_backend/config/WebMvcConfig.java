package com.comoencasa_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 🔧 Configuración para registrar interceptors HTTP
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

     @Autowired
     private HttpLoggingInterceptor httpLoggingInterceptor;

     @Override
     public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(httpLoggingInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(
                              "/actuator/**", // Excluir endpoints de monitoreo
                              "/static/**", // Excluir recursos estáticos
                              "/css/**", // Excluir CSS
                              "/js/**", // Excluir JavaScript
                              "/images/**", // Excluir imágenes
                              "/favicon.ico", // Excluir favicon
                              "/error" // Excluir página de error
                    );
     }
}
