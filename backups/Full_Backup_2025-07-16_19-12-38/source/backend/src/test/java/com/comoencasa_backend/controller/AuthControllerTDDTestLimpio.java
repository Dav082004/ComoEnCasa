package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.LoginRequest;
import com.comoencasa_backend.dto.RegistroRequest;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.UsuarioService;
import com.comoencasa_backend.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests TDD para AuthController
 * 
 * Cubre todos los endpoints de autenticación:
 * - Login
 * - Registro
 * - Verificación de cuenta
 * - Validaciones de entrada
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Tests TDD")
class AuthControllerTDDTestLimpio {

     @Mock
     private UsuarioRepository usuarioRepository;

     @Mock
     private BCryptPasswordEncoder passwordEncoder;

     @Mock
     private EmailService emailService;

     @Mock
     private VerificationTokenService verificationTokenService;

     @Mock
     private UsuarioService usuarioService;

     @InjectMocks
     private AuthController authController;

     @BeforeEach
     void setUp() {
          // Setup inicial si es necesario
     }

     @Nested
     @DisplayName("Tests de Login")
     class LoginTests {

          @Test
          @DisplayName("Login exitoso con credenciales válidas")
          void testLoginExitoso() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("test@example.com");
               loginRequest.setPassword("password123");

               Usuario usuario = new Usuario();
               usuario.setId(1L);
               usuario.setEmail("test@example.com");
               usuario.setPassword("hashedPassword");
               usuario.setNombre("Test User");
               usuario.setActivado(true);

               when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                         .thenReturn(Optional.of(usuario));
               when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                         .thenReturn(true);

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(200, response.getStatusCode().value());
               assertNotNull(response.getBody());
               verify(usuarioRepository).findByEmail(loginRequest.getEmail());
               verify(passwordEncoder).matches(loginRequest.getPassword(), usuario.getPassword());
          }

          @Test
          @DisplayName("Login fallido - usuario no encontrado")
          void testLoginUsuarioNoEncontrado() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("noexiste@example.com");
               loginRequest.setPassword("password123");

               when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                         .thenReturn(Optional.empty());

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(401, response.getStatusCode().value());
               verify(usuarioRepository).findByEmail(loginRequest.getEmail());
               verifyNoInteractions(passwordEncoder);
          }

          @Test
          @DisplayName("Login fallido - contraseña incorrecta")
          void testLoginContrasenaIncorrecta() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("test@example.com");
               loginRequest.setPassword("wrongpassword");

               Usuario usuario = new Usuario();
               usuario.setEmail("test@example.com");
               usuario.setPassword("hashedPassword");
               usuario.setActivado(true);

               when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                         .thenReturn(Optional.of(usuario));
               when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                         .thenReturn(false);

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(401, response.getStatusCode().value());
               verify(usuarioRepository).findByEmail(loginRequest.getEmail());
               verify(passwordEncoder).matches(loginRequest.getPassword(), usuario.getPassword());
          }

          @Test
          @DisplayName("Login fallido - usuario no activado")
          void testLoginUsuarioNoActivado() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("test@example.com");
               loginRequest.setPassword("password123");

               Usuario usuario = new Usuario();
               usuario.setEmail("test@example.com");
               usuario.setPassword("hashedPassword");
               usuario.setActivado(false);

               when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                         .thenReturn(Optional.of(usuario));
               when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                         .thenReturn(true);

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(403, response.getStatusCode().value());
               verify(usuarioRepository).findByEmail(loginRequest.getEmail());
               verify(passwordEncoder).matches(loginRequest.getPassword(), usuario.getPassword());
          }

          @Test
          @DisplayName("Login fallido - datos null")
          void testLoginDatosNull() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail(null);
               loginRequest.setPassword(null);

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verifyNoInteractions(usuarioRepository);
          }

          @Test
          @DisplayName("Login fallido - email vacío")
          void testLoginEmailVacio() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("");
               loginRequest.setPassword("password123");

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verifyNoInteractions(usuarioRepository);
          }

          @Test
          @DisplayName("Login fallido - password vacío")
          void testLoginPasswordVacio() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("test@example.com");
               loginRequest.setPassword("");

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verifyNoInteractions(usuarioRepository);
          }
     }

     @Nested
     @DisplayName("Tests de Registro")
     class RegistroTests {

          @Test
          @DisplayName("Registro exitoso con datos válidos")
          void testRegistroExitoso() {
               // Given
               RegistroRequest registroRequest = new RegistroRequest();
               registroRequest.setNombre("Juan");
               registroRequest.setApellido("Pérez");
               registroRequest.setEmail("juan@example.com");
               registroRequest.setPassword("password123");

               when(usuarioRepository.findByEmail(registroRequest.getEmail()))
                         .thenReturn(Optional.empty());
               when(passwordEncoder.encode(registroRequest.getPassword()))
                         .thenReturn("hashedPassword");

               Usuario usuarioGuardado = new Usuario();
               usuarioGuardado.setId(1L);
               usuarioGuardado.setEmail(registroRequest.getEmail());
               usuarioGuardado.setNombre(registroRequest.getNombre());

               when(usuarioRepository.save(any(Usuario.class)))
                         .thenReturn(usuarioGuardado);
               when(verificationTokenService.generarToken(anyString()))
                         .thenReturn("token123");
               doNothing().when(emailService).enviarTokenVerificacion(anyString(), anyString());

               // When
               ResponseEntity<?> response = authController.registrar(registroRequest);

               // Then
               assertEquals(200, response.getStatusCode().value());
               assertNotNull(response.getBody());
               verify(usuarioRepository).findByEmail(registroRequest.getEmail());
               verify(passwordEncoder).encode(registroRequest.getPassword());
               verify(usuarioRepository).save(any(Usuario.class));
               verify(emailService).enviarTokenVerificacion(anyString(), anyString());
          }

          @Test
          @DisplayName("Registro fallido - email ya existe")
          void testRegistroEmailExistente() {
               // Given
               RegistroRequest registroRequest = new RegistroRequest();
               registroRequest.setNombre("Juan");
               registroRequest.setApellido("Pérez");
               registroRequest.setEmail("existente@example.com");
               registroRequest.setPassword("password123");

               Usuario usuarioExistente = new Usuario();
               usuarioExistente.setEmail("existente@example.com");

               when(usuarioRepository.findByEmail(registroRequest.getEmail()))
                         .thenReturn(Optional.of(usuarioExistente));

               // When
               ResponseEntity<?> response = authController.registrar(registroRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verify(usuarioRepository).findByEmail(registroRequest.getEmail());
               verifyNoInteractions(passwordEncoder);
               verify(usuarioRepository, never()).save(any(Usuario.class));
          }

          @Test
          @DisplayName("Registro fallido - datos inválidos")
          void testRegistroDatosInvalidos() {
               // Given
               RegistroRequest registroRequest = new RegistroRequest();
               registroRequest.setNombre(""); // Nombre vacío
               registroRequest.setApellido("");
               registroRequest.setEmail("email-invalido");
               registroRequest.setPassword("123"); // Password muy corto

               // When
               ResponseEntity<?> response = authController.registrar(registroRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verifyNoInteractions(usuarioRepository);
          }

          @Test
          @DisplayName("Registro fallido - email null")
          void testRegistroEmailNull() {
               // Given
               RegistroRequest registroRequest = new RegistroRequest();
               registroRequest.setNombre("Juan");
               registroRequest.setApellido("Pérez");
               registroRequest.setEmail(null);
               registroRequest.setPassword("password123");

               // When
               ResponseEntity<?> response = authController.registrar(registroRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verifyNoInteractions(usuarioRepository);
          }

          @Test
          @DisplayName("Registro fallido - password null")
          void testRegistroPasswordNull() {
               // Given
               RegistroRequest registroRequest = new RegistroRequest();
               registroRequest.setNombre("Juan");
               registroRequest.setApellido("Pérez");
               registroRequest.setEmail("juan@example.com");
               registroRequest.setPassword(null);

               // When
               ResponseEntity<?> response = authController.registrar(registroRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verifyNoInteractions(usuarioRepository);
          }

          @Test
          @DisplayName("Registro fallido - nombre muy largo")
          void testRegistroNombreMuyLargo() {
               // Given
               RegistroRequest registroRequest = new RegistroRequest();
               registroRequest.setNombre("A".repeat(51)); // Excede el límite de 50
               registroRequest.setApellido("Pérez");
               registroRequest.setEmail("juan@example.com");
               registroRequest.setPassword("password123");

               // When
               ResponseEntity<?> response = authController.registrar(registroRequest);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verifyNoInteractions(usuarioRepository);
          }
     }

     @Nested
     @DisplayName("Tests de Verificación de Cuenta")
     class VerificacionCuentaTests {

          @Test
          @DisplayName("Verificación exitosa con token válido")
          void testVerificacionExitosa() {
               // Given
               String token = "valid-token";
               String email = "test@example.com";

               Usuario usuario = new Usuario();
               usuario.setEmail(email);
               usuario.setActivado(false);

               when(verificationTokenService.obtenerEmailPorToken(token))
                         .thenReturn(email);
               when(usuarioRepository.findByEmail(email))
                         .thenReturn(Optional.of(usuario));
               when(usuarioRepository.save(any(Usuario.class)))
                         .thenReturn(usuario);
               doNothing().when(verificationTokenService).eliminarToken(token);

               // When
               ResponseEntity<?> response = authController.verificarCuenta(token);

               // Then
               assertEquals(200, response.getStatusCode().value());
               assertTrue(usuario.getActivado());
               verify(verificationTokenService).obtenerEmailPorToken(token);
               verify(usuarioRepository).findByEmail(email);
               verify(usuarioRepository).save(usuario);
               verify(verificationTokenService).eliminarToken(token);
          }

          @Test
          @DisplayName("Verificación fallida - token inválido")
          void testVerificacionTokenInvalido() {
               // Given
               String token = "invalid-token";

               when(verificationTokenService.obtenerEmailPorToken(token))
                         .thenReturn(null);

               // When
               ResponseEntity<?> response = authController.verificarCuenta(token);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verify(verificationTokenService).obtenerEmailPorToken(token);
               verifyNoInteractions(usuarioRepository);
          }

          @Test
          @DisplayName("Verificación fallida - usuario no encontrado")
          void testVerificacionUsuarioNoEncontrado() {
               // Given
               String token = "valid-token";
               String email = "noexiste@example.com";

               when(verificationTokenService.obtenerEmailPorToken(token))
                         .thenReturn(email);
               when(usuarioRepository.findByEmail(email))
                         .thenReturn(Optional.empty());

               // When
               ResponseEntity<?> response = authController.verificarCuenta(token);

               // Then
               assertEquals(400, response.getStatusCode().value());
               verify(verificationTokenService).obtenerEmailPorToken(token);
               verify(usuarioRepository).findByEmail(email);
               verify(usuarioRepository, never()).save(any(Usuario.class));
          }

          @Test
          @DisplayName("Verificación - cuenta ya verificada")
          void testVerificacionCuentaYaVerificada() {
               // Given
               String token = "valid-token";
               String email = "test@example.com";

               Usuario usuario = new Usuario();
               usuario.setEmail(email);
               usuario.setActivado(true); // Ya activado

               when(verificationTokenService.obtenerEmailPorToken(token))
                         .thenReturn(email);
               when(usuarioRepository.findByEmail(email))
                         .thenReturn(Optional.of(usuario));

               // When
               ResponseEntity<?> response = authController.verificarCuenta(token);

               // Then
               assertEquals(200, response.getStatusCode().value());
               verify(verificationTokenService).obtenerEmailPorToken(token);
               verify(usuarioRepository).findByEmail(email);
               verify(usuarioRepository, never()).save(any(Usuario.class));
          }
     }

     @Nested
     @DisplayName("Tests de Edge Cases")
     class EdgeCasesTests {

          @Test
          @DisplayName("Login con caracteres especiales en email")
          void testLoginConCaracteresEspeciales() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("test+special@example.com");
               loginRequest.setPassword("password123");

               Usuario usuario = new Usuario();
               usuario.setEmail("test+special@example.com");
               usuario.setPassword("hashedPassword");
               usuario.setActivado(true);

               when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                         .thenReturn(Optional.of(usuario));
               when(passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword()))
                         .thenReturn(true);

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(200, response.getStatusCode().value());
          }

          @Test
          @DisplayName("Manejo de excepción en base de datos")
          void testManejoExcepcionBaseDatos() {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail("test@example.com");
               loginRequest.setPassword("password123");

               when(usuarioRepository.findByEmail(loginRequest.getEmail()))
                         .thenThrow(new RuntimeException("Error de base de datos"));

               // When
               ResponseEntity<?> response = authController.login(loginRequest);

               // Then
               assertEquals(500, response.getStatusCode().value());
          }

          @Test
          @DisplayName("Registro con excepción al enviar email")
          void testRegistroExcepcionEmail() {
               // Given
               RegistroRequest registroRequest = new RegistroRequest();
               registroRequest.setNombre("Juan");
               registroRequest.setApellido("Pérez");
               registroRequest.setEmail("juan@example.com");
               registroRequest.setPassword("password123");

               when(usuarioRepository.findByEmail(registroRequest.getEmail()))
                         .thenReturn(Optional.empty());
               when(passwordEncoder.encode(registroRequest.getPassword()))
                         .thenReturn("hashedPassword");

               Usuario usuarioGuardado = new Usuario();
               usuarioGuardado.setId(1L);

               when(usuarioRepository.save(any(Usuario.class)))
                         .thenReturn(usuarioGuardado);
               when(verificationTokenService.generarToken(anyString()))
                         .thenReturn("token123");
               doThrow(new RuntimeException("Error al enviar email"))
                         .when(emailService).enviarTokenVerificacion(anyString(), anyString());

               // When
               ResponseEntity<?> response = authController.registrar(registroRequest);

               // Then
               assertEquals(500, response.getStatusCode().value());
          }
     }
}
