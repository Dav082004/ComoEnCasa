package com.comoencasa_backend.controller;

import com.comoencasa_backend.dto.LoginRequest;
import com.comoencasa_backend.dto.RegistroRequest;
import com.comoencasa_backend.model.Usuario;
import com.comoencasa_backend.repository.UsuarioRepository;
import com.comoencasa_backend.service.EmailService;
import com.comoencasa_backend.service.VerificationTokenService;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    // ✅ NUEVO: servicios para enviar correo y manejar tokens de verificación
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public AuthController(UsuarioRepository usuarioRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          EmailService emailService,
                          VerificationTokenService verificationTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // === Validaciones con Apache Commons ===
        String email = StringUtils.trimToNull(loginRequest.getEmail());
        String password = StringUtils.trimToNull(loginRequest.getPassword());
        
        // Log del intento de login (sin exponer información sensible)
        logger.info("Intento de login para email: {}", maskEmail(email));

        // Validar formato de email
        if (email == null || !EmailValidator.getInstance().isValid(email)) {
            logger.warn("Intento de login con email inválido: {}", maskEmail(email));
            return ResponseEntity.status(401).body(createErrorResponse("Email inválido"));
        }

        // Validar que la contraseña no esté vacía
        if (password == null || password.isEmpty()) {
            logger.warn("Intento de login sin contraseña para email: {}", maskEmail(email));
            return ResponseEntity.status(401).body(createErrorResponse("Contraseña requerida"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            logger.warn("Login fallido: usuario no encontrado para email: {}", maskEmail(email));
            return ResponseEntity.status(401).body(createErrorResponse("Credenciales inválidas"));
        }

        Usuario usuario = usuarioOpt.get();

        System.out.println("Usuario encontrado: " + usuario.getEmail());
        System.out.println("Nombre completo: " + usuario.getNombre() + " " + usuario.getApellido());
        System.out.println("Hash almacenado: " + usuario.getPassword());
        System.out.println("Activado?: " + usuario.getActivado());

        if (!usuario.getActivado()) {
            return ResponseEntity.status(403).body("La cuenta aún no ha sido verificada.");
        }

        boolean coincide = passwordEncoder.matches(
                loginRequest.getPassword(),
                usuario.getPassword()
        );

        if (!coincide) {
            System.out.println("❌ Contraseña incorrecta");
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        // Login exitoso
        logger.info("Login exitoso para usuario ID: {} ({})", usuario.getId(), maskEmail(usuario.getEmail()));
        
        return ResponseEntity.ok(Map.of(
                "usuario", Map.of(
                        "id", usuario.getId(),
                        "nombreCompleto", usuario.getNombre() + " " + usuario.getApellido(),
                        "email", usuario.getEmail(),
                        "rol", usuario.getRol().name()
                )
        ));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest registroRequest) {
        String email = null;
        try {
            // === Debug: Log del objeto recibido ===
            logger.info("RegistroRequest recibido - correo: {}, nombre: {}, apellido: {}", 
                registroRequest.getCorreo(), registroRequest.getNombre(), registroRequest.getApellido());
            
            // === Validaciones con Apache Commons ===
            email = StringUtils.trimToNull(registroRequest.getEmail());
            String nombre = StringUtils.trimToNull(registroRequest.getNombre());
            String apellido = StringUtils.trimToNull(registroRequest.getApellido());
            String password = StringUtils.trimToNull(registroRequest.getPassword());
            String telefono = StringUtils.trimToNull(registroRequest.getTelefono());
            String direccion = StringUtils.trimToNull(registroRequest.getDireccion());
            String numeroDocumento = StringUtils.trimToNull(registroRequest.getNumeroDocumento());


            if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
                return ResponseEntity.badRequest().body(createErrorResponse("El email ya está registrado"));

            }

            // Validar longitud mínima de contraseña
            if (password.length() < 6) {
                logger.warn("Registro fallido: contraseña muy corta para email: {}", maskEmail(email));
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("La contraseña debe tener al menos 6 caracteres"));
            }

            // Verificar si el email ya existe
            if (usuarioRepository.existsByEmail(email)) {
                logger.warn("Registro fallido: email ya existe: {}", maskEmail(email));
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("El email ya está registrado"));
            }

            // Crear nuevo usuario con todos los campos
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(passwordEncoder.encode(password));
            
            // Campos opcionales con valores por defecto si están vacíos
            nuevoUsuario.setTelefono(StringUtils.isBlank(telefono) ? "" : telefono);
            nuevoUsuario.setDireccion(StringUtils.isBlank(direccion) ? "" : direccion);
            nuevoUsuario.setNumeroDocumento(StringUtils.isBlank(numeroDocumento) ? "" : numeroDocumento);
            
            // Establecer tipo de documento si está disponible
            if (registroRequest.getTipoDocumento() != null) {
                nuevoUsuario.setTipoDocumento(registroRequest.getTipoDocumento());
            } else {
                // Valor por defecto
                nuevoUsuario.setTipoDocumento(Usuario.TipoDocumento.DNI);
            }
            
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());
            nuevoUsuario.setRol(Usuario.Rol.CLIENTE);

            nuevoUsuario.setActivado(false); // ❗ Inicia como NO activado

            usuarioRepository.save(nuevoUsuario);

            // ✅ Generamos token y lo enviamos por correo
            String token = verificationTokenService.generarToken(nuevoUsuario.getEmail());
            emailService.enviarTokenVerificacion(nuevoUsuario.getEmail(), token);

            return ResponseEntity.ok(createSuccessResponse(
                    nuevoUsuario,
                    "Registro exitoso. Revisa tu correo para verificar tu cuenta."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse("Error interno al registrar usuario"));
        }
    }

    // ✅ NUEVO: Endpoint para activar cuenta usando el token
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarCuenta(@RequestParam("token") String token) {
        String email = verificationTokenService.obtenerEmailPorToken(token);

        if (email == null) {
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No se encontró un usuario para este token");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setActivado(true);
        usuarioRepository.save(usuario);

        verificationTokenService.eliminarToken(token);
        return ResponseEntity.ok("Cuenta verificada correctamente. Ya puedes iniciar sesión.");
    }

    // Métodos auxiliares
    
    private String maskEmail(String email) {
        if (email == null) return "unknown";
        return email.replaceAll("(.{3}).*(@.*)", "$1***$2");
    }

    private Map<String, Object> createSuccessResponse(Usuario usuario, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("usuario", createUserData(usuario));
        return response;
    }

    private Map<String, Object> createErrorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        return response;
    }

    private Map<String, Object> createUserData(Usuario usuario) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", usuario.getId());
        userData.put("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
        userData.put("email", usuario.getEmail());
        userData.put("fechaRegistro", usuario.getFechaRegistro());
        userData.put("rol", usuario.getRol().name());
        return userData;
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", usuario.getId());
        userData.put("nombre", usuario.getNombre());
        userData.put("apellido", usuario.getApellido());
        userData.put("email", usuario.getEmail());
        userData.put("telefono", usuario.getTelefono());
        userData.put("direccion", usuario.getDireccion());

        return ResponseEntity.ok(userData);
    }
    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        logger.info("Intento de actualización de perfil para usuario ID: {}", id);
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            logger.warn("Actualización de perfil fallida: usuario no encontrado ID: {}", id);
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();
        String emailAnterior = usuario.getEmail();

        // Verificar si el nuevo correo ya existe para otro usuario
        String nuevoEmail = StringUtils.trimToNull((String) datos.get("email"));
        if (nuevoEmail != null && !StringUtils.equals(nuevoEmail, usuario.getEmail())) {
            // Validar formato del nuevo email
            if (!EmailValidator.getInstance().isValid(nuevoEmail)) {
                logger.warn("Actualización fallida: email inválido {} para usuario ID: {}", 
                    maskEmail(nuevoEmail), id);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Formato de email inválido"));
            }
            
            Optional<Usuario> emailExistente = usuarioRepository.findByEmail(nuevoEmail);
            if (emailExistente.isPresent() && !emailExistente.get().getId().equals(usuario.getId())) {
                logger.warn("Actualización fallida: email {} ya existe para usuario ID: {}", 
                    maskEmail(nuevoEmail), id);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El correo ya está en uso por otro usuario"));
            }
            usuario.setEmail(nuevoEmail);
        }

        // Actualizar otros datos con validación
        String nombre = StringUtils.trimToNull((String) datos.get("nombre"));
        String apellido = StringUtils.trimToNull((String) datos.get("apellido"));
        
        if (nombre != null) usuario.setNombre(nombre);
        if (apellido != null) usuario.setApellido(apellido);
        
        usuario.setTelefono(StringUtils.defaultString((String) datos.get("telefono"), ""));
        usuario.setDireccion(StringUtils.defaultString((String) datos.get("direccion"), ""));

        // Actualizar contraseña si se envió
        if (datos.containsKey("nuevaContrasena") && datos.get("nuevaContrasena") != null) {
            String nuevaPass = StringUtils.trimToNull(datos.get("nuevaContrasena").toString());
            if (nuevaPass != null && nuevaPass.length() >= 6) {
                usuario.setPassword(passwordEncoder.encode(nuevaPass));
                logger.info("Contraseña actualizada para usuario ID: {}", id);
            } else if (nuevaPass != null) {
                logger.warn("Actualización fallida: contraseña muy corta para usuario ID: {}", id);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
            }
        }

        usuarioRepository.save(usuario);

        logger.info("Perfil actualizado exitosamente para usuario ID: {} (email: {} -> {})", 
            id, maskEmail(emailAnterior), maskEmail(usuario.getEmail()));

        return ResponseEntity.ok(Map.of("message", "Perfil actualizado correctamente"));
    }
}
