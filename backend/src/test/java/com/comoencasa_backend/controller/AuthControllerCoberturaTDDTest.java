package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.LoginRequest;
import com.comoencasa_backend.dto.RegistroRequest;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.VerificationTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests TDD para AuthController
 * Enfocados en aumentar cobertura del 6% al máximo posible
 * Patrón Red-Green-Refactor aplicado estrictamente
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController TDD Tests")
class AuthControllerCoberturaTDDTest {

     private MockMvc mockMvc;

     @Mock
     private UsuarioRepository usuarioRepository;

     @Mock
     private BCryptPasswordEncoder passwordEncoder;

     @Mock
     private EmailService emailService;

     @Mock
     private VerificationTokenService verificationTokenService;

     @InjectMocks
     private AuthController authController;

     @BeforeEach
     void setUp() {
          mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
     }

     private ObjectMapper objectMapper = new ObjectMapper();

     @Nested
     @DisplayName("Tests TDD para POST /api/auth/login")
     class TestsLogin {

          @Test
          @DisplayName("RED: Login debería fallar con datos vacíos")
          void login_DeberiaFallar_ConDatosVacios() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("");
               request.setPassword("");

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isUnauthorized())
                         .andExpect(content().string("Email inválido"));
          }

          @Test
          @DisplayName("RED: Login debería fallar con email inválido")
          void login_DeberiaFallar_ConEmailInvalido() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("email-invalido");
               request.setPassword("password123");

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isUnauthorized())
                         .andExpect(content().string("Email inválido"));
          }

          @Test
          @DisplayName("RED: Login debería fallar con usuario inexistente")
          void login_DeberiaFallar_ConUsuarioInexistente() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("inexistente@test.com");
               request.setPassword("password123");

               when(usuarioRepository.findByEmail("inexistente@test.com"))
                         .thenReturn(Optional.empty());

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isUnauthorized())
                         .andExpect(content().string("Credenciales inválidas"));

               verify(usuarioRepository).findByEmail("inexistente@test.com");
          }

          @Test
          @DisplayName("RED: Login debería fallar con cuenta no activada")
          void login_DeberiaFallar_ConCuentaNoActivada() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("inactivo@test.com");
               request.setPassword("password123");

               Usuario usuarioInactivo = new Usuario();
               usuarioInactivo.setEmail("inactivo@test.com");
               usuarioInactivo.setPassword("hashedPassword");
               usuarioInactivo.setActivado(false);

               when(usuarioRepository.findByEmail("inactivo@test.com"))
                         .thenReturn(Optional.of(usuarioInactivo));

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isForbidden())
                         .andExpect(content().string("La cuenta aún no ha sido verificada."));

               verify(usuarioRepository).findByEmail("inactivo@test.com");
          }

          @Test
          @DisplayName("RED: Login debería fallar con contraseña incorrecta")
          void login_DeberiaFallar_ConContrasenaIncorrecta() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("usuario@test.com");
               request.setPassword("passwordIncorrecto");

               Usuario usuario = new Usuario();
               usuario.setId(1L);
               usuario.setEmail("usuario@test.com");
               usuario.setPassword("hashedCorrectPassword");
               usuario.setActivado(true);

               when(usuarioRepository.findByEmail("usuario@test.com"))
                         .thenReturn(Optional.of(usuario));
               when(passwordEncoder.matches("passwordIncorrecto", "hashedCorrectPassword"))
                         .thenReturn(false);

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isUnauthorized())
                         .andExpect(content().string("Credenciales inválidas"));

               verify(usuarioRepository).findByEmail("usuario@test.com");
               verify(passwordEncoder).matches("passwordIncorrecto", "hashedCorrectPassword");
          }

          @Test
          @DisplayName("GREEN: Login debería ser exitoso con credenciales correctas")
          void login_DeberiaSerExitoso_ConCredencialesCorrectas() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("usuario@test.com");
               request.setPassword("passwordCorrecto");

               Usuario usuario = new Usuario();
               usuario.setId(1L);
               usuario.setNombre("Juan");
               usuario.setApellido("Pérez");
               usuario.setEmail("usuario@test.com");
               usuario.setPassword("hashedCorrectPassword");
               usuario.setActivado(true);
               usuario.setRol(Usuario.Rol.CLIENTE);

               when(usuarioRepository.findByEmail("usuario@test.com"))
                         .thenReturn(Optional.of(usuario));
               when(passwordEncoder.matches("passwordCorrecto", "hashedCorrectPassword"))
                         .thenReturn(true);

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.usuario.id").value(1))
                         .andExpect(jsonPath("$.usuario.nombreCompleto").value("Juan Pérez"))
                         .andExpect(jsonPath("$.usuario.email").value("usuario@test.com"))
                         .andExpect(jsonPath("$.usuario.rol").value("CLIENTE"));

               verify(usuarioRepository).findByEmail("usuario@test.com");
               verify(passwordEncoder).matches("passwordCorrecto", "hashedCorrectPassword");
          }

          @Test
          @DisplayName("REFACTOR: Login debería manejar emails con espacios")
          void login_DeberiaManejarEmailsConEspacios() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("  usuario@test.com  ");
               request.setPassword("password123");
               Usuario usuario = new Usuario();
               usuario.setId(1L);
               usuario.setNombre("Juan");
               usuario.setApellido("Pérez");
               usuario.setEmail("usuario@test.com");
               usuario.setPassword("$2a$10$hashedPassword"); // Contraseña hasheada
               usuario.setActivado(true);
               usuario.setRol(Usuario.Rol.CLIENTE);
               when(usuarioRepository.findByEmail("usuario@test.com"))
                         .thenReturn(Optional.of(usuario));
               when(passwordEncoder.matches(eq("password123"), any(String.class)))
                         .thenReturn(true);

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.usuario.email").value("usuario@test.com"));

               verify(usuarioRepository).findByEmail("usuario@test.com");
          }
     }

     @Nested
     @DisplayName("Tests TDD para POST /api/auth/registro")
     class TestsRegistro {

          @Test
          @DisplayName("RED: Registro debería fallar con email ya existente")
          void registro_DeberiaFallar_ConEmailYaExistente() throws Exception {
               // Given
               RegistroRequest request = new RegistroRequest();
               request.setEmail("existente@test.com");
               request.setNombre("Juan");
               request.setApellido("Pérez");
               request.setPassword("password123");

               when(usuarioRepository.existsByEmail("existente@test.com"))
                         .thenReturn(true);

               // When & Then
               mockMvc.perform(post("/api/auth/registro")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isBadRequest())
                         .andExpect(content().string("El email ya está registrado"));

               verify(usuarioRepository).existsByEmail("existente@test.com");
               verify(usuarioRepository, never()).save(any(Usuario.class));
          }

          @Test
          @DisplayName("GREEN: Registro debería ser exitoso con datos válidos")
          void registro_DeberiaSerExitoso_ConDatosValidos() throws Exception {
               // Given
               RegistroRequest request = new RegistroRequest();
               request.setEmail("nuevo@test.com");
               request.setNombre("María");
               request.setApellido("García");
               request.setPassword("password123");

               when(usuarioRepository.existsByEmail("nuevo@test.com"))
                         .thenReturn(false);
               when(passwordEncoder.encode("password123"))
                         .thenReturn("hashedPassword");
               when(verificationTokenService.generarToken("nuevo@test.com"))
                         .thenReturn("token123");

               // When & Then
               mockMvc.perform(post("/api/auth/registro")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(content().string("Registro exitoso. Revisa tu correo para verificar tu cuenta."));

               verify(usuarioRepository).existsByEmail("nuevo@test.com");
               verify(usuarioRepository).save(argThat(usuario -> usuario.getEmail().equals("nuevo@test.com") &&
                         usuario.getNombre().equals("María") &&
                         usuario.getApellido().equals("García") &&
                         usuario.getPassword().equals("hashedPassword") &&
                         !usuario.getActivado() &&
                         usuario.getRol() == Usuario.Rol.CLIENTE));
               verify(verificationTokenService).generarToken("nuevo@test.com");
               verify(emailService).enviarTokenVerificacion("nuevo@test.com", "token123");
          }

          @Test
          @DisplayName("REFACTOR: Registro debería manejar errores internos")
          void registro_DeberiaManejarErroresInternos() throws Exception {
               // Given
               RegistroRequest request = new RegistroRequest();
               request.setEmail("error@test.com");
               request.setNombre("Test");
               request.setApellido("User");
               request.setPassword("password123");

               when(usuarioRepository.existsByEmail("error@test.com"))
                         .thenReturn(false);
               when(passwordEncoder.encode("password123"))
                         .thenThrow(new RuntimeException("Error interno"));

               // When & Then
               mockMvc.perform(post("/api/auth/registro")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isInternalServerError())
                         .andExpect(content().string("Error interno al registrar usuario"));

               verify(usuarioRepository).existsByEmail("error@test.com");
          }

          @Test
          @DisplayName("REFACTOR: Registro debería sanitizar email con espacios")
          void registro_DeberiaSanitizarEmailConEspacios() throws Exception {
               // Given
               RegistroRequest request = new RegistroRequest();
               request.setEmail("  espacios@test.com  ");
               request.setNombre("Test");
               request.setApellido("User");
               request.setPassword("password123");

               when(usuarioRepository.existsByEmail("espacios@test.com"))
                         .thenReturn(false);
               when(passwordEncoder.encode("password123"))
                         .thenReturn("hashedPassword");
               when(verificationTokenService.generarToken("espacios@test.com"))
                         .thenReturn("token123");

               // When & Then
               mockMvc.perform(post("/api/auth/registro")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk());

               verify(usuarioRepository).existsByEmail("espacios@test.com");
               verify(usuarioRepository).save(argThat(usuario -> usuario.getEmail().equals("espacios@test.com")));
          }
     }

     @Nested
     @DisplayName("Tests TDD para GET /api/auth/verificar")
     class TestsVerificar {

          @Test
          @DisplayName("RED: Verificar debería fallar con token inválido")
          void verificar_DeberiaFallar_ConTokenInvalido() throws Exception {
               // Given
               String tokenInvalido = "token_invalido";
               when(verificationTokenService.obtenerEmailPorToken(tokenInvalido))
                         .thenReturn(null);

               // When & Then
               mockMvc.perform(get("/api/auth/verificar")
                         .param("token", tokenInvalido))
                         .andExpect(status().isBadRequest())
                         .andExpect(content().string("Token inválido o ya expirado."));

               verify(verificationTokenService).obtenerEmailPorToken(tokenInvalido);
          }

          @Test
          @DisplayName("RED: Verificar debería fallar si usuario no existe")
          void verificar_DeberiaFallar_SiUsuarioNoExiste() throws Exception {
               // Given
               String token = "token_valido";
               String email = "inexistente@test.com";

               when(verificationTokenService.obtenerEmailPorToken(token))
                         .thenReturn(email);
               when(usuarioRepository.findByEmail(email))
                         .thenReturn(Optional.empty());

               // When & Then
               mockMvc.perform(get("/api/auth/verificar")
                         .param("token", token))
                         .andExpect(status().isBadRequest())
                         .andExpect(content().string("No se encontró un usuario asociado a este token."));

               verify(verificationTokenService).obtenerEmailPorToken(token);
               verify(usuarioRepository).findByEmail(email);
          }

          @Test
          @DisplayName("GREEN: Verificar debería ser exitoso si cuenta ya estaba verificada")
          void verificar_DeberiaSerExitoso_SiCuentaYaEstabaVerificada() throws Exception {
               // Given
               String token = "token_valido";
               String email = "verificado@test.com";

               Usuario usuarioYaVerificado = new Usuario();
               usuarioYaVerificado.setEmail(email);
               usuarioYaVerificado.setActivado(true);

               when(verificationTokenService.obtenerEmailPorToken(token))
                         .thenReturn(email);
               when(usuarioRepository.findByEmail(email))
                         .thenReturn(Optional.of(usuarioYaVerificado));

               // When & Then
               mockMvc.perform(get("/api/auth/verificar")
                         .param("token", token))
                         .andExpect(status().isOk())
                         .andExpect(content().string("La cuenta ya estaba verificada."));

               verify(verificationTokenService).obtenerEmailPorToken(token);
               verify(usuarioRepository).findByEmail(email);
               verify(usuarioRepository, never()).save(any(Usuario.class));
          }

          @Test
          @DisplayName("GREEN: Verificar debería activar cuenta exitosamente")
          void verificar_DeberiaActivarCuentaExitosamente() throws Exception {
               // Given
               String token = "token_valido";
               String email = "nuevo@test.com";

               Usuario usuarioNoVerificado = new Usuario();
               usuarioNoVerificado.setEmail(email);
               usuarioNoVerificado.setActivado(false);

               when(verificationTokenService.obtenerEmailPorToken(token))
                         .thenReturn(email);
               when(usuarioRepository.findByEmail(email))
                         .thenReturn(Optional.of(usuarioNoVerificado));

               // When & Then
               mockMvc.perform(get("/api/auth/verificar")
                         .param("token", token))
                         .andExpect(status().isOk())
                         .andExpect(content().string("Cuenta activada correctamente. Ya puedes iniciar sesión."));

               verify(verificationTokenService).obtenerEmailPorToken(token);
               verify(usuarioRepository).findByEmail(email);
               verify(usuarioRepository).save(argThat(usuario -> usuario.getActivado()));
               verify(verificationTokenService).eliminarToken(token);
          }

          @Test
          @DisplayName("REFACTOR: Verificar debería manejar tokens con caracteres especiales")
          void verificar_DeberiaManejarTokensConCaracteresEspeciales() throws Exception {
               // Given
               String tokenConCaracteres = "token_123-abc.xyz";
               when(verificationTokenService.obtenerEmailPorToken(tokenConCaracteres))
                         .thenReturn(null);

               // When & Then
               mockMvc.perform(get("/api/auth/verificar")
                         .param("token", tokenConCaracteres))
                         .andExpect(status().isBadRequest());

               verify(verificationTokenService).obtenerEmailPorToken(tokenConCaracteres);
          }
     }

     @Nested
     @DisplayName("Tests de Edge Cases y Validaciones")
     class TestsEdgeCasesYValidaciones {

          @Test
          @DisplayName("REFACTOR: Debería manejar requests con campos nulos")
          void deberiaManejarRequestsConCamposNulos() throws Exception {
               // Given
               LoginRequest loginRequest = new LoginRequest();
               loginRequest.setEmail(null);
               loginRequest.setPassword(null);

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(loginRequest)))
                         .andExpect(status().isUnauthorized())
                         .andExpect(content().string("Email inválido"));
          }

          @Test
          @DisplayName("REFACTOR: Debería manejar diferentes roles de usuario")
          void deberiaManejarDiferentesRolesDeUsuario() throws Exception {
               // Given
               LoginRequest request = new LoginRequest();
               request.setEmail("admin@test.com");
               request.setPassword("adminPassword");
               Usuario usuarioAdmin = new Usuario();
               usuarioAdmin.setId(1L);
               usuarioAdmin.setNombre("Admin");
               usuarioAdmin.setApellido("User");
               usuarioAdmin.setEmail("admin@test.com");
               usuarioAdmin.setPassword("$2a$10$hashedAdminPassword"); // Contraseña hasheada
               usuarioAdmin.setActivado(true);
               usuarioAdmin.setRol(Usuario.Rol.ADMIN);
               when(usuarioRepository.findByEmail("admin@test.com"))
                         .thenReturn(Optional.of(usuarioAdmin));
               when(passwordEncoder.matches(eq("adminPassword"), any(String.class)))
                         .thenReturn(true);

               // When & Then
               mockMvc.perform(post("/api/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk())
                         .andExpect(jsonPath("$.usuario.rol").value("ADMIN"));

               verify(usuarioRepository).findByEmail("admin@test.com");
          }

          @Test
          @DisplayName("REFACTOR: Registro debería configurar campos por defecto correctamente")
          void registro_DeberiaConfigurarCamposPorDefectoCorrectamente() throws Exception {
               // Given
               RegistroRequest request = new RegistroRequest();
               request.setEmail("defecto@test.com");
               request.setNombre("Test");
               request.setApellido("User");
               request.setPassword("password123");

               when(usuarioRepository.existsByEmail("defecto@test.com"))
                         .thenReturn(false);
               when(passwordEncoder.encode("password123"))
                         .thenReturn("hashedPassword");
               when(verificationTokenService.generarToken("defecto@test.com"))
                         .thenReturn("token123");

               // When & Then
               mockMvc.perform(post("/api/auth/registro")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                         .andExpect(status().isOk());

               verify(usuarioRepository).save(argThat(usuario -> usuario.getTelefono().equals("") &&
                         usuario.getDireccion().equals("") &&
                         usuario.getRol() == Usuario.Rol.CLIENTE &&
                         !usuario.getActivado() &&
                         usuario.getFechaRegistro() != null));
          }
     }
}
