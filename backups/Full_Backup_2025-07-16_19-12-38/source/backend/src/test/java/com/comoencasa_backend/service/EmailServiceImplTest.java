package com.comoencasa_backend.service;

import com.comoencasa_backend.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests para EmailServiceImpl
 * Siguiendo metodología TDD
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setup() {
        // Configurar el remitente para los tests
        ReflectionTestUtils.setField(emailService, "remitente", "test@example.com");
    }    @Nested
    @DisplayName("esEmailValido Tests")
    class EsEmailValidoTests {

        @Test
        @DisplayName("Debería validar correctamente un email con formato válido")
        void deberiaValidarCorrectamenteEmailConFormatoValido() {
            // El método esEmailValido es público, no necesitamos reflexión
            assertThat(emailService.esEmailValido("test@example.com")).isTrue();
            assertThat(emailService.esEmailValido("another.email@domain.co")).isTrue();
            assertThat(emailService.esEmailValido("email@sub.domain.com")).isTrue();
        }

        @Test
        @DisplayName("Debería rechazar un email con formato inválido")
        void deberiaRechazarEmailConFormatoInvalido() {
            // El método esEmailValido es público, no necesitamos reflexión
            assertThat(emailService.esEmailValido(null)).isFalse();
            assertThat(emailService.esEmailValido("")).isFalse();
            assertThat(emailService.esEmailValido("emailsindomain")).isFalse();
            assertThat(emailService.esEmailValido("email@")).isFalse();
            assertThat(emailService.esEmailValido("@domain.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("enviarNuevaContrasena Tests")
    class EnviarNuevaContrasenaTests {

        @Test
        @DisplayName("Debería lanzar excepción cuando el email es inválido")
        void deberiaLanzarExcepcionCuandoEmailEsInvalido() {
            // Then
            assertThrows(IllegalArgumentException.class, () ->
                    emailService.enviarNuevaContrasena("emailinvalido", "password123"));
        }

        @Test
        @DisplayName("Debería enviar email correctamente con contraseña nueva")
        void deberiaEnviarEmailCorrectamenteConNuevaContrasena() {
            // Given
            String email = "test@example.com";
            String password = "newPassword123";
            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            // When
            emailService.enviarNuevaContrasena(email, password);

            // Then
            verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        }
    }
}
