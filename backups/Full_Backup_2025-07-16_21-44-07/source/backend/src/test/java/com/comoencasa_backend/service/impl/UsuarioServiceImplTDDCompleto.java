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
 * - Registro de usuarios
 * - Validaciones de email
 * - Operaciones CRUD
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl - Tests TDD")
class UsuarioServiceImplTDDTest {

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
        usuarioEjemplo.setVerificado(true);
        usuarioEjemplo.setActivo(true);
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
            // When
            Optional<Usuario> resultado = usuarioService.buscarPorEmail(null);

            // Then
            assertFalse(resultado.isPresent());
            verify(usuarioRepository).findByEmail(null);
        }

        @Test
        @DisplayName("Buscar usuario por email - email vacío")
        void testBuscarPorEmailVacio() {
            // When
            Optional<Usuario> resultado = usuarioService.buscarPorEmail("");

            // Then
            assertFalse(resultado.isPresent());
            verify(usuarioRepository).findByEmail("");
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
            // When
            usuarioService.actualizarContrasena(usuarioEjemplo, null);

            // Then
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
    }

    @Nested
    @DisplayName("Tests de Registro de Usuarios")
    class RegistroUsuarioTests {

        @Test
        @DisplayName("Registro exitoso - usuario válido")
        void testRegistroExitoso() {
            // Given
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre("Ana");
            nuevoUsuario.setApellido("García");
            nuevoUsuario.setEmail("ana.garcia@email.com");
            nuevoUsuario.setPassword("password123");
            nuevoUsuario.setTelefono("987654321");

            when(usuarioRepository.existsByEmail("ana.garcia@email.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encrypted");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevoUsuario);

            // When
            Usuario resultado = usuarioService.registrarUsuario(nuevoUsuario);

            // Then
            assertNotNull(resultado);
            assertEquals("Ana", resultado.getNombre());
            assertEquals("García", resultado.getApellido());
            assertEquals("ana.garcia@email.com", resultado.getEmail());
            assertEquals("$2a$10$encrypted", resultado.getPassword());
            assertFalse(resultado.getVerificado());
            assertTrue(resultado.getActivo());
            
            verify(usuarioRepository).existsByEmail("ana.garcia@email.com");
            verify(passwordEncoder).encode("password123");
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Registro fallido - email ya existe")
        void testRegistroEmailYaExiste() {
            // Given
            Usuario usuarioExistente = new Usuario();
            usuarioExistente.setEmail("juan.perez@email.com");
            usuarioExistente.setPassword("password123");
            
            when(usuarioRepository.existsByEmail("juan.perez@email.com")).thenReturn(true);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.registrarUsuario(usuarioExistente);
            });

            assertEquals("El email ya está registrado", exception.getMessage());
            verify(usuarioRepository).existsByEmail("juan.perez@email.com");
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Registro fallido - email inválido")
        void testRegistroEmailInvalido() {
            // Given
            Usuario usuarioEmailInvalido = new Usuario();
            usuarioEmailInvalido.setEmail("email-invalido");
            usuarioEmailInvalido.setPassword("password123");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.registrarUsuario(usuarioEmailInvalido);
            });

            assertEquals("Email inválido", exception.getMessage());
            verify(usuarioRepository, never()).existsByEmail(any());
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Registro fallido - usuario nulo")
        void testRegistroUsuarioNulo() {
            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.registrarUsuario(null);
            });

            assertEquals("Usuario no puede ser nulo", exception.getMessage());
            verify(usuarioRepository, never()).existsByEmail(any());
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Registro fallido - datos requeridos nulos")
        void testRegistroDatosRequeridosNulos() {
            // Given
            Usuario usuarioIncompleto = new Usuario();
            usuarioIncompleto.setEmail("test@email.com");
            // Password y otros campos faltantes

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.registrarUsuario(usuarioIncompleto);
            });

            assertEquals("Password no puede ser nulo o vacío", exception.getMessage());
            verify(usuarioRepository, never()).existsByEmail(any());
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests de Validación de Email")
    class ValidacionEmailTests {

        @Test
        @DisplayName("Validar email válido")
        void testValidarEmailValido() {
            // When
            boolean resultado = usuarioService.validarEmail("usuario@dominio.com");

            // Then
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Validar email inválido - formato incorrecto")
        void testValidarEmailInvalido() {
            // When
            boolean resultado = usuarioService.validarEmail("email-invalido");

            // Then
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Validar email nulo")
        void testValidarEmailNulo() {
            // When
            boolean resultado = usuarioService.validarEmail(null);

            // Then
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Validar email vacío")
        void testValidarEmailVacio() {
            // When
            boolean resultado = usuarioService.validarEmail("");

            // Then
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Validar email - sin dominio")
        void testValidarEmailSinDominio() {
            // When
            boolean resultado = usuarioService.validarEmail("usuario@");

            // Then
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Validar email - sin @")
        void testValidarEmailSinArroba() {
            // When
            boolean resultado = usuarioService.validarEmail("usuariodominio.com");

            // Then
            assertFalse(resultado);
        }
    }

    @Nested
    @DisplayName("Tests de Recuperación de Contraseña")
    class RecuperacionContrasenaTests {

        @Test
        @DisplayName("Generar token de recuperación exitoso")
        void testGenerarTokenRecuperacionExitoso() {
            // Given
            String email = "juan.perez@email.com";
            when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));

            // When
            String token = usuarioService.generarTokenRecuperacion(email);

            // Then
            assertNotNull(token);
            assertEquals(6, token.length());
            assertTrue(token.matches("\\d{6}"));
            verify(usuarioRepository).findByEmail(email);
            verify(verificationTokenService).saveToken(eq(usuarioEjemplo.getId()), eq(token));
        }

        @Test
        @DisplayName("Generar token de recuperación - usuario no encontrado")
        void testGenerarTokenRecuperacionUsuarioNoEncontrado() {
            // Given
            String email = "noexiste@email.com";
            when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.generarTokenRecuperacion(email);
            });

            assertEquals("Usuario no encontrado con el email: " + email, exception.getMessage());
            verify(usuarioRepository).findByEmail(email);
            verify(verificationTokenService, never()).saveToken(any(), any());
        }

        @Test
        @DisplayName("Validar token de recuperación - token válido")
        void testValidarTokenRecuperacionValido() {
            // Given
            String token = "123456";
            when(verificationTokenService.isTokenValid(usuarioEjemplo.getId(), token)).thenReturn(true);

            // When
            boolean resultado = usuarioService.validarTokenRecuperacion(usuarioEjemplo.getId(), token);

            // Then
            assertTrue(resultado);
            verify(verificationTokenService).isTokenValid(usuarioEjemplo.getId(), token);
        }

        @Test
        @DisplayName("Validar token de recuperación - token inválido")
        void testValidarTokenRecuperacionInvalido() {
            // Given
            String token = "123456";
            when(verificationTokenService.isTokenValid(usuarioEjemplo.getId(), token)).thenReturn(false);

            // When
            boolean resultado = usuarioService.validarTokenRecuperacion(usuarioEjemplo.getId(), token);

            // Then
            assertFalse(resultado);
            verify(verificationTokenService).isTokenValid(usuarioEjemplo.getId(), token);
        }

        @Test
        @DisplayName("Recuperar contraseña exitoso")
        void testRecuperarContrasenaExitoso() {
            // Given
            String email = "juan.perez@email.com";
            String token = "123456";
            String nuevaContrasena = "nuevaPassword123";
            
            when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));
            when(verificationTokenService.isTokenValid(usuarioEjemplo.getId(), token)).thenReturn(true);
            when(passwordEncoder.encode(nuevaContrasena)).thenReturn("$2a$10$encrypted");

            // When
            usuarioService.recuperarContrasena(email, token, nuevaContrasena);

            // Then
            assertEquals("$2a$10$encrypted", usuarioEjemplo.getPassword());
            verify(usuarioRepository).findByEmail(email);
            verify(verificationTokenService).isTokenValid(usuarioEjemplo.getId(), token);
            verify(passwordEncoder).encode(nuevaContrasena);
            verify(usuarioRepository).save(usuarioEjemplo);
            verify(verificationTokenService).deleteToken(usuarioEjemplo.getId(), token);
        }

        @Test
        @DisplayName("Recuperar contraseña - token inválido")
        void testRecuperarContrasenaTokenInvalido() {
            // Given
            String email = "juan.perez@email.com";
            String token = "123456";
            String nuevaContrasena = "nuevaPassword123";
            
            when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));
            when(verificationTokenService.isTokenValid(usuarioEjemplo.getId(), token)).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.recuperarContrasena(email, token, nuevaContrasena);
            });

            assertEquals("Token inválido o expirado", exception.getMessage());
            verify(usuarioRepository).findByEmail(email);
            verify(verificationTokenService).isTokenValid(usuarioEjemplo.getId(), token);
            verify(passwordEncoder, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
            verify(verificationTokenService, never()).deleteToken(any(), any());
        }
    }

    @Nested
    @DisplayName("Tests de Operaciones CRUD")
    class OperacionesCRUDTests {

        @Test
        @DisplayName("Obtener usuario por ID - existente")
        void testObtenerUsuarioPorIdExistente() {
            // Given
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

            // When
            Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1L);

            // Then
            assertTrue(resultado.isPresent());
            assertEquals(usuarioEjemplo, resultado.get());
            verify(usuarioRepository).findById(1L);
        }

        @Test
        @DisplayName("Obtener usuario por ID - no existente")
        void testObtenerUsuarioPorIdNoExistente() {
            // Given
            when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

            // When
            Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(999L);

            // Then
            assertFalse(resultado.isPresent());
            verify(usuarioRepository).findById(999L);
        }

        @Test
        @DisplayName("Actualizar usuario exitoso")
        void testActualizarUsuarioExitoso() {
            // Given
            Usuario usuarioActualizado = new Usuario();
            usuarioActualizado.setId(1L);
            usuarioActualizado.setNombre("Juan Carlos");
            usuarioActualizado.setApellido("Pérez García");
            usuarioActualizado.setTelefono("987654321");
            
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

            // When
            Usuario resultado = usuarioService.actualizarUsuario(1L, usuarioActualizado);

            // Then
            assertNotNull(resultado);
            assertEquals("Juan Carlos", usuarioEjemplo.getNombre());
            assertEquals("Pérez García", usuarioEjemplo.getApellido());
            assertEquals("987654321", usuarioEjemplo.getTelefono());
            verify(usuarioRepository).findById(1L);
            verify(usuarioRepository).save(usuarioEjemplo);
        }

        @Test
        @DisplayName("Actualizar usuario - no encontrado")
        void testActualizarUsuarioNoEncontrado() {
            // Given
            Usuario usuarioActualizado = new Usuario();
            when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.actualizarUsuario(999L, usuarioActualizado);
            });

            assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
            verify(usuarioRepository).findById(999L);
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Eliminar usuario exitoso")
        void testEliminarUsuarioExitoso() {
            // Given
            when(usuarioRepository.existsById(1L)).thenReturn(true);

            // When
            usuarioService.eliminarUsuario(1L);

            // Then
            verify(usuarioRepository).existsById(1L);
            verify(usuarioRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Eliminar usuario - no encontrado")
        void testEliminarUsuarioNoEncontrado() {
            // Given
            when(usuarioRepository.existsById(999L)).thenReturn(false);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                usuarioService.eliminarUsuario(999L);
            });

            assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
            verify(usuarioRepository).existsById(999L);
            verify(usuarioRepository, never()).deleteById(any());
        }
    }
}
