package com.comoencasa_backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.TestInstance;

/**
 * Clase base para todos los tests de integración
 * Configuración común para TDD
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {
    
    /**
     * Método para setup común si es necesario
     */
    protected void setUp() {
        // Configuración común para todos los tests
    }
    
    /**
     * Método para cleanup común si es necesario
     */
    protected void tearDown() {
        // Limpieza común para todos los tests
    }
}
