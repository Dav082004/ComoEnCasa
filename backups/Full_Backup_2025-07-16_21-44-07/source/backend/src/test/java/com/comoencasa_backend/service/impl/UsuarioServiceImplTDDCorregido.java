package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests TDD para UsuarioServiceImpl
 * 
 * Cubre todas las operaciones del servicio de usuarios:
 * - Búsqueda por email
 * - Actualización de contraseña
 * - Validaciones básicas
 * - Operaciones CRUD básicas
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl - Tests TDD")
class UsuarioServiceImplTDDCorregido {

     @Mock
     private UsuarioRepository usuarioRepository;

     @Mock
     private BCryptPasswordEncoder passwordEncoder;

     @Mock
     private EmailService emailService;

     @Mock
     private VerificationTokenService verificationTokenService;

     @InjectMocks
     private UsuarioServiceImpl usuarioService;

     private Usuario usuarioEjemplo;

     @BeforeEach
     void setUp() {
          usuarioEjemplo = new Usuario();
          usuarioEjemplo.setId(1L);
          usuarioEjemplo.setNombre("Juan");
          usuarioEjemplo.setApellido("Pérez");
          usuarioEjemplo.setEmail("juan.perez@email.com");
          usuarioEjemplo.setPassword("password123");
          usuarioEjemplo.setTelefono("123456789");
          usuarioEjemplo.setDireccion("Calle 123");
          usuarioEjemplo.setTipoDocumento(Usuario.TipoDocumento.DNI);
          usuarioEjemplo.setNumeroDocumento("12345678");
          usuarioEjemplo.setRol(Usuario.Rol.CLIENTE);
          usuarioEjemplo.setActivado(true);
          usuarioEjemplo.setFechaRegistro(LocalDateTime.now());
     }

     @Nested
     @DisplayName("Tests de Búsqueda por Email")
     class BusquedaEmailTests {

          @Test
          @DisplayName("Buscar usuario por email - existente")
          void testBuscarPorEmailExistente() {
               // Given
               when(usuarioRepository.findByEmail("juan.perez@email.com"))
                         .thenReturn(Optional.of(usuarioEjemplo));

               // When
               Optional<Usuario> resultado = usuarioService.buscarPorEmail("juan.perez@email.com");

               // Then
               assertTrue(resultado.isPresent());
               assertEquals(usuarioEjemplo, resultado.get());
               verify(usuarioRepository).findByEmail("juan.perez@email.com");
          }

          @Test
          @DisplayName("Buscar usuario por email - no existente")
          void testBuscarPorEmailNoExistente() {
               // Given
               when(usuarioRepository.findByEmail("noexiste@email.com"))
                         .thenReturn(Optional.empty());

               // When
               Optional<Usuario> resultado = usuarioService.buscarPorEmail("noexiste@email.com");

               // Then
               assertFalse(resultado.isPresent());
               verify(usuarioRepository).findByEmail("noexiste@email.com");
          }

          @Test
          @DisplayName("Buscar usuario por email - email nulo")
          void testBuscarPorEmailNulo() {
               // Given
               when(usuarioRepository.findByEmail(null))
                         .thenReturn(Optional.empty());

               // When
               Optional<Usuario> resultado = usuarioService.buscarPorEmail(null);

               // Then
               assertFalse(resultado.isPresent());
               verify(usuarioRepository).findByEmail(null);
          }

          @Test
          @DisplayName("Buscar usuario por email - email vacío")
          void testBuscarPorEmailVacio() {
               // Given
               when(usuarioRepository.findByEmail(""))
                         .thenReturn(Optional.empty());

               // When
               Optional<Usuario> resultado = usuarioService.buscarPorEmail("");

               // Then
               assertFalse(resultado.isPresent());
               verify(usuarioRepository).findByEmail("");
          }

          @Test
          @DisplayName("Buscar usuario por email - múltiples llamadas")
          void testBuscarPorEmailMultiplesLlamadas() {
               // Given
               when(usuarioRepository.findByEmail("juan.perez@email.com"))
                         .thenReturn(Optional.of(usuarioEjemplo));

               // When
               Optional<Usuario> resultado1 = usuarioService.buscarPorEmail("juan.perez@email.com");
               Optional<Usuario> resultado2 = usuarioService.buscarPorEmail("juan.perez@email.com");

               // Then
               assertTrue(resultado1.isPresent());
               assertTrue(resultado2.isPresent());
               assertEquals(usuarioEjemplo, resultado1.get());
               assertEquals(usuarioEjemplo, resultado2.get());
               verify(usuarioRepository, times(2)).findByEmail("juan.perez@email.com");
          }
     }

     @Nested
     @DisplayName("Tests de Actualización de Contraseña")
     class ActualizacionContrasenaTests {

          @Test
          @DisplayName("Actualizar contraseña exitoso")
          void testActualizarContrasenaExitoso() {
               // Given
               String nuevaContrasena = "nuevaPassword123";
               String contrasenaEncriptada = "$2a$10$encrypted";
               when(passwordEncoder.encode(nuevaContrasena)).thenReturn(contrasenaEncriptada);

               // When
               usuarioService.actualizarContrasena(usuarioEjemplo, nuevaContrasena);

               // Then
               assertEquals(contrasenaEncriptada, usuarioEjemplo.getPassword());
               verify(passwordEncoder).encode(nuevaContrasena);
               verify(usuarioRepository).save(usuarioEjemplo);
          }

          @Test
          @DisplayName("Actualizar contraseña - usuario nulo")
          void testActualizarContrasenaUsuarioNulo() {
               // When & Then
               assertThrows(NullPointerException.class, () -> {
                    usuarioService.actualizarContrasena(null, "nuevaPassword123");
               });

               verify(passwordEncoder, never()).encode(any());
               verify(usuarioRepository, never()).save(any());
          }

          @Test
          @DisplayName("Actualizar contraseña - contraseña nula")
          void testActualizarContrasenaContrasenaNull() {
               // Given
               String contrasenaEncriptada = "$2a$10$encrypted_null";
               when(passwordEncoder.encode(null)).thenReturn(contrasenaEncriptada);

               // When
               usuarioService.actualizarContrasena(usuarioEjemplo, null);

               // Then
               assertEquals(contrasenaEncriptada, usuarioEjemplo.getPassword());
               verify(passwordEncoder).encode(null);
               verify(usuarioRepository).save(usuarioEjemplo);
          }

          @Test
          @DisplayName("Actualizar contraseña - contraseña vacía")
          void testActualizarContrasenaContrasenaVacia() {
               // Given
               String contrasenaVacia = "";
               String contrasenaEncriptada = "$2a$10$encrypted_empty";
               when(passwordEncoder.encode(contrasenaVacia)).thenReturn(contrasenaEncriptada);

               // When
               usuarioService.actualizarContrasena(usuarioEjemplo, contrasenaVacia);

               // Then
               assertEquals(contrasenaEncriptada, usuarioEjemplo.getPassword());
               verify(passwordEncoder).encode(contrasenaVacia);
               verify(usuarioRepository).save(usuarioEjemplo);
          }

          @Test
          @DisplayName("Actualizar contraseña - error en encriptación")
          void testActualizarContrasenaErrorEncriptacion() {
               // Given
               String nuevaContrasena = "nuevaPassword123";
               when(passwordEncoder.encode(nuevaContrasena)).thenThrow(new RuntimeException("Error en encriptación"));

               // When & Then
               assertThrows(RuntimeException.class, () -> {
                    usuarioService.actualizarContrasena(usuarioEjemplo, nuevaContrasena);
               });

               verify(passwordEncoder).encode(nuevaContrasena);
               verify(usuarioRepository, never()).save(any());
          }

          @Test
          @DisplayName("Actualizar contraseña - error en guardado")
          void testActualizarContrasenaErrorGuardado() {
               // Given
               String nuevaContrasena = "nuevaPassword123";
               String contrasenaEncriptada = "$2a$10$encrypted";
               when(passwordEncoder.encode(nuevaContrasena)).thenReturn(contrasenaEncriptada);
               when(usuarioRepository.save(usuarioEjemplo)).thenThrow(new RuntimeException("Error en base de datos"));

               // When & Then
               assertThrows(RuntimeException.class, () -> {
                    usuarioService.actualizarContrasena(usuarioEjemplo, nuevaContrasena);
               });

               verify(passwordEncoder).encode(nuevaContrasena);
               verify(usuarioRepository).save(usuarioEjemplo);
          }
     }

     @Nested
     @DisplayName("Tests de Integración con Repository")
     class IntegracionRepositoryTests {

          @Test
          @DisplayName("Búsqueda por email - excepción en repository")
          void testBusquedaPorEmailExcepcionRepository() {
               // Given
               when(usuarioRepository.findByEmail("juan.perez@email.com"))
                         .thenThrow(new RuntimeException("Error de conexión a BD"));

               // When & Then
               assertThrows(RuntimeException.class, () -> {
                    usuarioService.buscarPorEmail("juan.perez@email.com");
               });

               verify(usuarioRepository).findByEmail("juan.perez@email.com");
          }

          @Test
          @DisplayName("Actualización de contraseña - excepción en repository save")
          void testActualizacionContrasenaExcepcionRepositorySave() {
               // Given
               String nuevaContrasena = "nuevaPassword123";
               String contrasenaEncriptada = "$2a$10$encrypted";
               when(passwordEncoder.encode(nuevaContrasena)).thenReturn(contrasenaEncriptada);
               when(usuarioRepository.save(usuarioEjemplo)).thenThrow(new RuntimeException("Error al guardar"));

               // When & Then
               assertThrows(RuntimeException.class, () -> {
                    usuarioService.actualizarContrasena(usuarioEjemplo, nuevaContrasena);
               });

               verify(passwordEncoder).encode(nuevaContrasena);
               verify(usuarioRepository).save(usuarioEjemplo);
          }

          @Test
          @DisplayName("Múltiples operaciones en secuencia")
          void testMultiplesOperacionesEnSecuencia() {
               // Given
               String email = "juan.perez@email.com";
               String nuevaContrasena = "nuevaPassword123";
               String contrasenaEncriptada = "$2a$10$encrypted";

               when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));
               when(passwordEncoder.encode(nuevaContrasena)).thenReturn(contrasenaEncriptada);

               // When
               Optional<Usuario> usuarioEncontrado = usuarioService.buscarPorEmail(email);
               assertTrue(usuarioEncontrado.isPresent());

               usuarioService.actualizarContrasena(usuarioEncontrado.get(), nuevaContrasena);

               // Then
               assertEquals(contrasenaEncriptada, usuarioEncontrado.get().getPassword());
               verify(usuarioRepository).findByEmail(email);
               verify(passwordEncoder).encode(nuevaContrasena);
               verify(usuarioRepository).save(usuarioEncontrado.get());
          }
     }

     @Nested
     @DisplayName("Tests de Manejo de Errores")
     class ManejoErroresTests {

          @Test
          @DisplayName("Manejo de excepción NullPointerException")
          void testManejoNullPointerException() {
               // When & Then
               assertThrows(NullPointerException.class, () -> {
                    usuarioService.actualizarContrasena(null, "password");
               });
          }

          @Test
          @DisplayName("Manejo de excepción RuntimeException en encoder")
          void testManejoRuntimeExceptionEncoder() {
               // Given
               when(passwordEncoder.encode("password")).thenThrow(new RuntimeException("Error en encoder"));

               // When & Then
               assertThrows(RuntimeException.class, () -> {
                    usuarioService.actualizarContrasena(usuarioEjemplo, "password");
               });
          }

          @Test
          @DisplayName("Manejo de excepción RuntimeException en repository")
          void testManejoRuntimeExceptionRepository() {
               // Given
               when(usuarioRepository.findByEmail("test@email.com"))
                         .thenThrow(new RuntimeException("Error en base de datos"));

               // When & Then
               assertThrows(RuntimeException.class, () -> {
                    usuarioService.buscarPorEmail("test@email.com");
               });
          }
     }

     @Nested
     @DisplayName("Tests de Performance")
     class PerformanceTests {

          @Test
          @DisplayName("Múltiples llamadas concurrentes buscarPorEmail")
          void testMultiplesLlamadasConcurrentesBuscarPorEmail() {
               // Given
               String email = "juan.perez@email.com";
               when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));

               // When - simular múltiples llamadas
               for (int i = 0; i < 10; i++) {
                    Optional<Usuario> resultado = usuarioService.buscarPorEmail(email);
                    assertTrue(resultado.isPresent());
               }

               // Then
               verify(usuarioRepository, times(10)).findByEmail(email);
          }

          @Test
          @DisplayName("Múltiples actualizaciones de contraseña")
          void testMultiplesActualizacionesContrasena() {
               // Given
               when(passwordEncoder.encode(anyString()))
                         .thenAnswer(invocation -> "$2a$10$" + invocation.getArgument(0));

               // When
               usuarioService.actualizarContrasena(usuarioEjemplo, "password1");
               usuarioService.actualizarContrasena(usuarioEjemplo, "password2");
               usuarioService.actualizarContrasena(usuarioEjemplo, "password3");

               // Then
               verify(passwordEncoder, times(3)).encode(anyString());
               verify(usuarioRepository, times(3)).save(usuarioEjemplo);
               assertEquals("$2a$10$password3", usuarioEjemplo.getPassword());
          }
     }
}
