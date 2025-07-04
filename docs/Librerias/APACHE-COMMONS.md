# 🔧 Apache Commons - Implementación en Como en Casa

## 📋 Descripción General

Apache Commons es una colección de librerías Java reutilizables que proporcionan soluciones para tareas comunes de programación. En el proyecto "Como en Casa", utilizamos múltiples componentes de Apache Commons para validación, manipulación de strings y procesamiento de texto de forma robusta y eficiente.

## 🎯 ¿Qué es Apache Commons?

Apache Commons es un proyecto de la Apache Software Foundation que proporciona componentes Java reutilizables. Estas librerías resuelven problemas comunes de desarrollo y han sido probadas en miles de proyectos empresariales.

### 💼 Beneficios en el Proyecto

- **✅ Validación Robusta**: Validación de emails con estándares RFC 5322
- **🔧 Manipulación Segura**: Operaciones null-safe con strings
- **🛡️ Seguridad**: Escape de caracteres para prevenir ataques XSS
- **📈 Productividad**: Menos código boilerplate, más funcionalidad

## 🔄 Flujo de Información con Apache Commons

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Input Usuario │────│  Apache Commons │────│  Datos Limpios  │
│                 │    │                 │    │                 │
│ • Emails        │    │ • Validator     │    │ • Validados     │
│ • Nombres       │    │ • Lang3         │    │ • Sanitizados   │
│ • Descripciones │    │ • Text          │    │ • Seguros       │
│ • HTML Content  │    │ • StringUtils   │    │ • Formateados   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🏗️ Arquitectura en el Proyecto

```
Controllers
    ↓
  Apache Commons Validator ← Email Validation
    ↓
  Services
    ↓
  Apache Commons Lang3 ← String Manipulation
    ↓
  Apache Commons Text ← HTML Escape
    ↓
  Database
```

## 🛠️ Componentes Utilizados

### 1. 📧 Apache Commons Validator

- **Validación de emails**: Cumple con RFC 5322 estándar
- **Validación de URLs**: Verificación de formato de URLs
- **Validación de datos**: Números, fechas, códigos postales
- **Localización**: Soporte para diferentes locales

### 2. 🔧 Apache Commons Lang3

- **StringUtils**: Operaciones seguras con strings
- **Null-safe operations**: Manejo seguro de valores nulos
- **String manipulation**: Trim, split, join, capitalize
- **Array utilities**: Operaciones con arrays

### 3. 📝 Apache Commons Text

- **HTML escape**: Prevención de ataques XSS
- **String substitution**: Reemplazo de variables en templates
- **Text processing**: Manipulación avanzada de texto
- **Character encoding**: Manejo de codificaciones

## 💻 Implementación en el Proyecto

### 📦 Dependencias Maven

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-validator</artifactId>
    <version>1.7</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.10.0</version>
</dependency>
```

### 🔧 Configuración Principal

#### 1. Validación de Emails en AuthController

```java
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        // Validación de email con Apache Commons
        if (!EmailValidator.getInstance().isValid(request.getEmail())) {
            return ResponseEntity.badRequest()
                .body("Email inválido. Formato requerido: usuario@dominio.com");
        }

        // Validación de campos con StringUtils
        if (StringUtils.isBlank(request.getNombre()) ||
            StringUtils.isBlank(request.getPassword())) {
            return ResponseEntity.badRequest()
                .body("Nombre y contraseña son obligatorios");
        }

        // Limpiar y normalizar datos
        String nombreLimpio = StringUtils.trim(request.getNombre());
        String emailLimpio = StringUtils.lowerCase(StringUtils.trim(request.getEmail()));

        // Continuar con el registro...
    }
}
```

#### 2. Sanitización en ProductoServiceImpl

```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Override
    public Producto create(Producto producto) {
        // Sanitizar datos de entrada
        producto.setNombre(sanitizarTexto(producto.getNombre()));
        producto.setDescripcion(sanitizarTexto(producto.getDescripcion()));
        producto.setImagenUrl(sanitizarUrl(producto.getImagenUrl()));

        return productoRepository.save(producto);
    }

    private String sanitizarTexto(String texto) {
        if (StringUtils.isBlank(texto)) {
            return "";
        }

        // Trim y escape HTML para prevenir XSS
        String textoLimpio = StringUtils.trim(texto);
        return StringEscapeUtils.escapeHtml4(textoLimpio);
    }

    private String sanitizarUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }

        // Limpiar espacios y caracteres especiales
        return StringUtils.trim(url);
    }
}
```

#### 3. Validación en UsuarioServiceImpl

```java
import org.apache.commons.validator.routines.EmailValidator;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Override
    public Usuario create(Usuario usuario) {
        // Validar email con Apache Commons Validator
        if (!EmailValidator.getInstance().isValid(usuario.getEmail())) {
            throw new IllegalArgumentException("Email inválido: " + usuario.getEmail());
        }

        // Normalizar datos
        usuario.setEmail(StringUtils.lowerCase(StringUtils.trim(usuario.getEmail())));
        usuario.setNombre(StringUtils.trim(usuario.getNombre()));
        usuario.setApellido(StringUtils.trim(usuario.getApellido()));

        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean validarEmail(String email) {
        return StringUtils.isNotBlank(email) &&
               EmailValidator.getInstance().isValid(email);
    }
}
```

## 📊 Casos de Uso Específicos

### 1. 📧 Validación de Emails

```java
public class EmailValidationService {

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    public boolean isValidEmail(String email) {
        // Validación completa RFC 5322
        return emailValidator.isValid(email);
    }

    public String normalizeEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }

        // Normalizar: trim + lowercase
        return StringUtils.lowerCase(StringUtils.trim(email));
    }

    public List<String> validateEmails(List<String> emails) {
        return emails.stream()
            .filter(this::isValidEmail)
            .map(this::normalizeEmail)
            .collect(Collectors.toList());
    }
}
```

### 2. 🔧 Manipulación Segura de Strings

```java
public class StringProcessingService {

    public String processUserInput(String input) {
        // Verificar si es null o vacío
        if (StringUtils.isBlank(input)) {
            return "";
        }

        // Limpiar espacios en blanco
        String cleaned = StringUtils.trim(input);

        // Capitalizar primera letra
        cleaned = StringUtils.capitalize(cleaned);

        // Escape HTML para seguridad
        cleaned = StringEscapeUtils.escapeHtml4(cleaned);

        return cleaned;
    }

    public String joinStrings(List<String> strings, String separator) {
        // Filtrar strings vacíos y hacer join
        return strings.stream()
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(separator));
    }

    public boolean isValidName(String name) {
        return StringUtils.isNotBlank(name) &&
               name.length() >= 2 &&
               name.length() <= 50;
    }
}
```

### 3. 🛡️ Seguridad y Sanitización

```java
public class SecurityService {

    public String sanitizeHtml(String html) {
        if (StringUtils.isBlank(html)) {
            return "";
        }

        // Escape completo de HTML
        return StringEscapeUtils.escapeHtml4(html);
    }

    public String sanitizeForDatabase(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }

        // Limpiar y escapar para prevenir SQL injection
        String cleaned = StringUtils.trim(input);
        cleaned = StringUtils.replace(cleaned, "'", "''");

        return cleaned;
    }

    public Map<String, String> sanitizeMap(Map<String, String> inputMap) {
        return inputMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> sanitizeHtml(entry.getValue())
            ));
    }
}
```

## 🧪 Testing con Apache Commons

### 1. 📧 Tests de Validación de Email

```java
@Test
class EmailValidationTest {

    @Test
    void testValidEmails() {
        // Emails válidos
        assertTrue(EmailValidator.getInstance().isValid("user@example.com"));
        assertTrue(EmailValidator.getInstance().isValid("test.email@domain.co.uk"));
        assertTrue(EmailValidator.getInstance().isValid("user+tag@example.org"));
    }

    @Test
    void testInvalidEmails() {
        // Emails inválidos
        assertFalse(EmailValidator.getInstance().isValid("invalid.email"));
        assertFalse(EmailValidator.getInstance().isValid("@example.com"));
        assertFalse(EmailValidator.getInstance().isValid("user@"));
        assertFalse(EmailValidator.getInstance().isValid(""));
        assertFalse(EmailValidator.getInstance().isValid(null));
    }
}
```

### 2. 🔧 Tests de StringUtils

```java
@Test
class StringUtilsTest {

    @Test
    void testNullSafeOperations() {
        // StringUtils es null-safe
        assertThat(StringUtils.isBlank(null)).isTrue();
        assertThat(StringUtils.isBlank("")).isTrue();
        assertThat(StringUtils.isBlank("   ")).isTrue();
        assertThat(StringUtils.isBlank("text")).isFalse();
    }

    @Test
    void testStringManipulation() {
        String input = "  Hello World  ";

        // Trim
        assertThat(StringUtils.trim(input)).isEqualTo("Hello World");

        // Capitalize
        assertThat(StringUtils.capitalize("hello")).isEqualTo("Hello");

        // Join
        List<String> words = Arrays.asList("Hello", "World");
        assertThat(StringUtils.join(words, " ")).isEqualTo("Hello World");
    }
}
```

### 3. 📝 Tests de Text Escaping

```java
@Test
class TextEscapingTest {

    @Test
    void testHtmlEscaping() {
        String dangerous = "<script>alert('XSS')</script>";
        String safe = StringEscapeUtils.escapeHtml4(dangerous);

        assertThat(safe).isEqualTo("&lt;script&gt;alert(&#039;XSS&#039;)&lt;/script&gt;");
        assertThat(safe).doesNotContain("<script>");
    }

    @Test
    void testSpecialCharacters() {
        String input = "Café & Crème";
        String escaped = StringEscapeUtils.escapeHtml4(input);

        assertThat(escaped).isEqualTo("Caf&eacute; &amp; Cr&egrave;me");
    }
}
```

## 🎯 Mejores Prácticas Implementadas

### 1. 🛡️ Validación Defensiva

```java
public class DefensiveValidation {

    public void validateUserInput(String input) {
        // Siempre validar antes de procesar
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException("Input no puede estar vacío");
        }

        // Validar longitud
        if (input.length() > 255) {
            throw new IllegalArgumentException("Input demasiado largo");
        }

        // Procesar solo después de validar
        String processed = processInput(input);
    }

    private String processInput(String input) {
        return StringUtils.trim(StringEscapeUtils.escapeHtml4(input));
    }
}
```

### 2. 🔧 Reutilización de Componentes

```java
@Component
public class ValidationUtils {

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    public boolean isValidEmail(String email) {
        return emailValidator.isValid(email);
    }

    public String sanitizeText(String text) {
        return StringUtils.isBlank(text) ? "" :
               StringEscapeUtils.escapeHtml4(StringUtils.trim(text));
    }

    public boolean hasValidLength(String text, int min, int max) {
        if (StringUtils.isBlank(text)) return false;
        int length = text.length();
        return length >= min && length <= max;
    }
}
```

### 3. 📊 Logging y Monitoreo

```java
@Service
public class AuditableService {

    private final Logger logger = LoggerFactory.getLogger(AuditableService.class);

    public void processUserData(String email, String name) {
        logger.info("Procesando datos de usuario: email={}, nombre={}",
                   StringUtils.abbreviate(email, 20),
                   StringUtils.abbreviate(name, 15));

        if (!EmailValidator.getInstance().isValid(email)) {
            logger.warn("Email inválido detectado: {}",
                       StringUtils.abbreviate(email, 20));
            throw new IllegalArgumentException("Email inválido");
        }

        // Procesar datos...
        logger.info("Datos procesados exitosamente");
    }
}
```

## 📈 Beneficios Alcanzados

### 1. 🛡️ Seguridad Mejorada

- **Prevención XSS**: Escape automático de HTML
- **Validación robusta**: Emails conformes a RFC 5322
- **Input sanitization**: Limpieza de datos de entrada

### 2. 🔧 Código Más Limpio

- **Null-safety**: Operaciones seguras con valores null
- **Menos boilerplate**: Utilidades predefinidas
- **Mejor legibilidad**: Código más expresivo

### 3. 📊 Mejor Rendimiento

- **Validación eficiente**: Algoritmos optimizados
- **Menos errores**: Validación previa previene excepciones
- **Reutilización**: Componentes compartidos

## 🔧 Configuración Avanzada

### 1. 📧 Configuración de EmailValidator

```java
@Configuration
public class ValidationConfig {

    @Bean
    public EmailValidator emailValidator() {
        // Configurar validador con opciones específicas
        return EmailValidator.getInstance(true, true);
    }

    @Bean
    public DomainValidator domainValidator() {
        // Validador de dominios personalizado
        return DomainValidator.getInstance(true);
    }
}
```

### 2. 🔧 Utilidades Personalizadas

```java
public class CustomStringUtils {

    public static String cleanPhoneNumber(String phone) {
        if (StringUtils.isBlank(phone)) {
            return "";
        }

        // Remover caracteres especiales
        String cleaned = StringUtils.replaceChars(phone, "()-. ", "");

        // Validar longitud
        if (cleaned.length() != 9) {
            throw new IllegalArgumentException("Número de teléfono inválido");
        }

        return cleaned;
    }

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "S/ 0.00";
        }

        return String.format("S/ %.2f", amount);
    }
}
```

## 🔮 Roadmap Futuro

### 1. 📈 Validaciones Avanzadas

- **Validación de DNI**: Algoritmo de validación para documentos peruanos
- **Validación de RUC**: Verificación de números de RUC
- **Validación de direcciones**: Integración con APIs de geolocalización

### 2. 🌐 Internacionalización

- **Mensajes localizados**: Mensajes de error en español
- **Formatos locales**: Fechas, números y monedas en formato peruano
- **Validación local**: Reglas específicas para Perú

### 3. 🔧 Optimizaciones

- **Caching**: Cache de validaciones frecuentes
- **Batch processing**: Validación en lotes
- **Performance monitoring**: Métricas de rendimiento

## 🔧 Troubleshooting Común

### 1. 📧 Problemas de Validación de Email

```java
// Problema: Email válido rechazado
// Solución: Verificar configuración de EmailValidator

// Incorrecto
EmailValidator.getInstance(false); // No permite TLD locales

// Correcto
EmailValidator.getInstance(true); // Permite TLD locales como .pe
```

### 2. 🔧 Problemas con StringUtils

```java
// Problema: NullPointerException
// Solución: Usar StringUtils en lugar de métodos String

// Incorrecto
if (text.isEmpty()) { ... } // Falla si text es null

// Correcto
if (StringUtils.isEmpty(text)) { ... } // Null-safe
```

### 3. 📝 Problemas de Encoding

```java
// Problema: Caracteres especiales mal codificados
// Solución: Especificar encoding explícitamente

String texto = "Café & Crème";
String escaped = StringEscapeUtils.escapeHtml4(texto);
// Resultado: "Caf&eacute; &amp; Cr&egrave;me"
```

---

## 📚 Recursos Adicionales

- **[Apache Commons Validator](https://commons.apache.org/proper/commons-validator/)**
- **[Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)**
- **[Apache Commons Text](https://commons.apache.org/proper/commons-text/)**
- **[RFC 5322 Email Specification](https://tools.ietf.org/html/rfc5322)**

---

_Esta documentación forma parte del proyecto "Como en Casa" - Sistema Web para Gestión de Pedidos y Clientes_
