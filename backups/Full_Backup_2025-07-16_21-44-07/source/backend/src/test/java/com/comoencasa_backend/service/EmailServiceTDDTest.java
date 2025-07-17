package com.comoencasa_backend.service;

import com.comoencasa_backend.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests TDD para EmailService siguiendo metodología Red-Green-Refactor
 * 
 * Funcionalidades a probar:
 * 1. Envío de email con nueva contraseña
 * 2. Validación de parámetros
 * 3. Configuración correcta del mensaje
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService TDD Tests")
class EmailServiceTDDTest {

    @Mock
    private JavaMailSender mockMailSender;

    private EmailService emailService;

    private final String REMITENTE_TEST = "test@comoencasa.com";

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mockMailSender);
        // Configurar el remitente usando ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "remitente", REMITENTE_TEST);
    }

    @Nested
    @DisplayName("Envío de Email con Nueva Contraseña")
    class EnvioEmailNuevaContrasena {

        @Test
        @DisplayName("RED: Debe fallar cuando el destinatario es nulo")
        void debeFailar_CuandoDestinatarioEsNulo() {
            // RED: Este test debe fallar inicialmente
            assertThatThrownBy(() -> emailService.enviarNuevaContrasena(null, "password123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El email del destinatario no puede ser nulo o vacío");
        }

        @Test
        @DisplayName("RED: Debe fallar cuando el destinatario está vacío")
        void debeFailar_CuandoDestinatarioEstaVacio() {
            // RED: Este test debe fallar inicialmente
            assertThatThrownBy(() -> emailService.enviarNuevaContrasena("", "password123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El email del destinatario no puede ser nulo o vacío");
        }

        @Test
        @DisplayName("RED: Debe fallar cuando la contraseña es nula")
        void debeFailar_CuandoContrasenaEsNula() {
            // RED: Este test debe fallar inicialmente
            assertThatThrownBy(() -> emailService.enviarNuevaContrasena("test@email.com", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La nueva contraseña no puede ser nula o vacía");
        }

        @Test
        @DisplayName("RED: Debe fallar cuando la contraseña está vacía")
        void debeFailar_CuandoContrasenaEstaVacia() {
            // RED: Este test debe fallar inicialmente
            assertThatThrownBy(() -> emailService.enviarNuevaContrasena("test@email.com", ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La nueva contraseña no puede ser nula o vacía");
        }

        @Test
        @DisplayName("GREEN: Debe enviar email correctamente con parámetros válidos")
        void debeEnviarEmail_CuandoParametrosSonValidos() {
            // Arrange
            String destinoEmail = "usuario@test.com";
            String nuevaContrasena = "nuevaPassword123";

            // Act
            emailService.enviarNuevaContrasena(destinoEmail, nuevaContrasena);

            // Assert
            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mockMailSender).send(messageCaptor.capture());

            SimpleMailMessage mensajeEnviado = messageCaptor.getValue();
            assertThat(mensajeEnviado.getFrom()).isEqualTo(REMITENTE_TEST);
            assertThat(mensajeEnviado.getTo()).containsExactly(destinoEmail);
            assertThat(mensajeEnviado.getSubject()).isEqualTo("Recuperación de cuenta - Como En Casa");
            assertThat(mensajeEnviado.getText()).isEqualTo("Tu nueva contraseña es: " + nuevaContrasena);
        }

        @Test
        @DisplayName("GREEN: Debe configurar correctamente el asunto del email")
        void debeConfigurarAsuntoCorrectamente() {
            // Arrange
            String destinoEmail = "usuario@test.com";
            String nuevaContrasena = "password123";

            // Act
            emailService.enviarNuevaContrasena(destinoEmail, nuevaContrasena);

            // Assert
            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mockMailSender).send(messageCaptor.capture());

            SimpleMailMessage mensaje = messageCaptor.getValue();
            assertThat(mensaje.getSubject())
                    .isEqualTo("Recuperación de cuenta - Como En Casa");
        }

        @Test
        @DisplayName("GREEN: Debe incluir la contraseña en el cuerpo del mensaje")
        void debeIncluirContrasenaEnCuerpoMensaje() {
            // Arrange
            String destinoEmail = "usuario@test.com";
            String nuevaContrasena = "miNuevaPassword456";

            // Act
            emailService.enviarNuevaContrasena(destinoEmail, nuevaContrasena);

            // Assert
            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mockMailSender).send(messageCaptor.capture());

            SimpleMailMessage mensaje = messageCaptor.getValue();
            assertThat(mensaje.getText())
                    .contains(nuevaContrasena)
                    .startsWith("Tu nueva contraseña es: ");
        }

        @Test
        @DisplayName("REFACTOR: Debe manejar emails con espacios correctamente")
        void debeManejarEmailsConEspacios() {
            // Arrange
            String destinoEmailConEspacios = "  usuario@test.com  ";
            String nuevaContrasena = "password123";

            // Act & Assert - No debe lanzar excepción
            assertThatCode(() -> emailService.enviarNuevaContrasena(destinoEmailConEspacios, nuevaContrasena))
                    .doesNotThrowAnyException();

            verify(mockMailSender).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("REFACTOR: Debe validar formato básico de email")
        void debeValidarFormatoBasicoEmail() {
            // RED: Este test debe fallar hasta implementar validación
            assertThatThrownBy(() -> emailService.enviarNuevaContrasena("email-invalido", "password123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Formato de email inválido");
        }
    }

    @Nested
    @DisplayName("Manejo de Errores y Excepciones")
    class ManejoErrores {

        @Test
        @DisplayName("Debe manejar excepción del JavaMailSender")
        void debeManejarExcepcionJavaMailSender() {
            // Arrange
            doThrow(new RuntimeException("Error de conexión SMTP"))
                    .when(mockMailSender).send(any(SimpleMailMessage.class));

            // Act & Assert
            assertThatThrownBy(() -> emailService.enviarNuevaContrasena("test@email.com", "password123"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Error de conexión SMTP");
        }

        @Test
        @DisplayName("Debe verificar que se intenta enviar el email una sola vez")
        void debeVerificarUnSoloIntentoEnvio() {
            // Arrange
            String destinoEmail = "usuario@test.com";
            String nuevaContrasena = "password123";

            // Act
            emailService.enviarNuevaContrasena(destinoEmail, nuevaContrasena);

            // Assert
            verify(mockMailSender, times(1)).send(any(SimpleMailMessage.class));
        }
    }

    @Nested
    @DisplayName("Configuración y Propiedades")
    class ConfiguracionPropiedades {

        @Test
        @DisplayName("Debe usar el remitente configurado en las propiedades")
        void debeUsarRemitenteConfigurado() {
            // Arrange
            String destinoEmail = "usuario@test.com";
            String nuevaContrasena = "password123";

            // Act
            emailService.enviarNuevaContrasena(destinoEmail, nuevaContrasena);

            // Assert
            ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
            verify(mockMailSender).send(messageCaptor.capture());

            SimpleMailMessage mensaje = messageCaptor.getValue();
            assertThat(mensaje.getFrom()).isEqualTo(REMITENTE_TEST);
        }

        @Test
        @DisplayName("RED: Debe fallar si el remitente no está configurado")
        void debeFailar_SiRemitenteNoEstaConfigurado() {
            // Arrange
            EmailService emailServiceSinRemitente = new EmailServiceImpl(mockMailSender);
            // No configuramos el remitente (null)

            // Act & Assert
            assertThatThrownBy(() -> emailServiceSinRemitente.enviarNuevaContrasena("test@email.com", "password123"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("El remitente del email no está configurado");
        }
    }

    @Nested
    @DisplayName("Validación de Email")
    class ValidacionEmail {
        @Test
        @DisplayName("Debería validar emails correctos")
        void deberiaValidarEmailsCorrectos() {
            // Convertir a EmailServiceImpl para acceder al método público
            EmailServiceImpl emailServiceImpl = (EmailServiceImpl) emailService;

            assertTrue(emailServiceImpl.esEmailValido("usuario@dominio.com"));
            assertTrue(emailServiceImpl.esEmailValido("usuario.nombre@dominio.com"));
            assertTrue(emailServiceImpl.esEmailValido("usuario_nombre@dominio.com"));
            assertTrue(emailServiceImpl.esEmailValido("usuario+nombre@dominio.com"));
        }

        @Test
        @DisplayName("Debería rechazar emails inválidos")
        void deberiaRechazarEmailsInvalidos() {
            // Convertir a EmailServiceImpl para acceder al método público
            EmailServiceImpl emailServiceImpl = (EmailServiceImpl) emailService;

            assertFalse(emailServiceImpl.esEmailValido(null));
            assertFalse(emailServiceImpl.esEmailValido(""));
            assertFalse(emailServiceImpl.esEmailValido("usuario"));
            assertFalse(emailServiceImpl.esEmailValido("usuario@"));
            assertFalse(emailServiceImpl.esEmailValido("@dominio.com"));
            assertFalse(emailServiceImpl.esEmailValido("usuario@dominio@com"));
        }
    }
}
