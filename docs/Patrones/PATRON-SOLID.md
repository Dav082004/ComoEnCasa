# Patrón SOLID - Principios de Diseño Orientado a Objetos

## Descripción General

Los principios SOLID son un conjunto de cinco principios fundamentales para el diseño de software orientado a objetos que promueven la creación de código más mantenible, extensible y comprensible. En el sistema "Como en Casa", estos principios se implementan de forma integral para garantizar una arquitectura robusta y escalable.

## Diagrama de Implementación

```mermaid
graph TD
    A[Principios SOLID] --> B[S - Single Responsibility]
    A --> C[O - Open/Closed]
    A --> D[L - Liskov Substitution]
    A --> E[I - Interface Segregation]
    A --> F[D - Dependency Inversion]

    B --> B1[ProductController]
    B --> B2[ProductService]
    B --> B3[ProductRepository]

    C --> C1[PaymentService Interface]
    C --> C2[PayPalPaymentService]
    C --> C3[CreditCardPaymentService]

    D --> D1[BaseEntity]
    D --> D2[Product extends BaseEntity]
    D --> D3[User extends BaseEntity]

    E --> E1[UserService Interface]
    E --> E2[AuthService Interface]
    E --> E3[EmailService Interface]

    F --> F1[Spring IoC Container]
    F --> F2[Dependency Injection]
    F --> F3[Configuration Classes]

    style A fill:#ff9999
    style B fill:#99ccff
    style C fill:#99ff99
    style D fill:#ffff99
    style E fill:#ff99ff
    style F fill:#99ffff
```

## Implementación de Principios SOLID

### 1. Single Responsibility Principle (SRP)

Cada clase tiene una única responsabilidad y una única razón para cambiar.

**Ejemplo: ProductController**

```java
@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        // Solo se encarga de manejar la petición HTTP
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        // Delegación de la lógica de negocio al servicio
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
}
```

**Ejemplo: ProductService**

```java
@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDTO> getAllProducts() {
        // Solo se encarga de la lógica de negocio
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        // Lógica de negocio y validaciones
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }
}
```

### 2. Open/Closed Principle (OCP)

Las clases están abiertas para extensión pero cerradas para modificación.

**Ejemplo: Sistema de Pagos**

```java
// Interfaz base - cerrada para modificación
public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    boolean validatePayment(PaymentRequest request);
}

// Implementación PayPal - extensión sin modificar la interfaz
@Service
@Slf4j
public class PayPalPaymentService implements PaymentService {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing PayPal payment for amount: {}", request.getAmount());
        // Lógica específica de PayPal
        return PaymentResponse.builder()
                .success(true)
                .transactionId(generatePayPalTransactionId())
                .build();
    }

    @Override
    public boolean validatePayment(PaymentRequest request) {
        // Validaciones específicas de PayPal
        return request.getAmount() > 0 && request.getPayPalToken() != null;
    }
}

// Nueva implementación - extensión sin modificar código existente
@Service
@Slf4j
public class CreditCardPaymentService implements PaymentService {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing credit card payment for amount: {}", request.getAmount());
        // Lógica específica de tarjeta de crédito
        return PaymentResponse.builder()
                .success(true)
                .transactionId(generateCreditCardTransactionId())
                .build();
    }

    @Override
    public boolean validatePayment(PaymentRequest request) {
        // Validaciones específicas de tarjeta de crédito
        return request.getAmount() > 0 &&
               request.getCreditCardNumber() != null &&
               request.getCvv() != null;
    }
}
```

### 3. Liskov Substitution Principle (LSP)

Los objetos de una clase derivada deben poder reemplazar objetos de la clase base sin alterar la funcionalidad.

**Ejemplo: Jerarquía de Entidades**

```java
// Clase base
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// Clase derivada que mantiene el comportamiento de la base
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    // Mantiene el comportamiento de la clase base
    // Puede ser usado en cualquier lugar donde se espere BaseEntity
}
```

### 4. Interface Segregation Principle (ISP)

Los clientes no deben depender de interfaces que no usan.

**Ejemplo: Segregación de Interfaces de Servicio**

```java
// Interfaz específica para operaciones de lectura
public interface UserReadService {
    Optional<UserDTO> findById(Long id);
    List<UserDTO> findAll();
    Optional<UserDTO> findByEmail(String email);
}

// Interfaz específica para operaciones de escritura
public interface UserWriteService {
    UserDTO create(UserDTO userDTO);
    UserDTO update(Long id, UserDTO userDTO);
    void delete(Long id);
}

// Interfaz específica para autenticación
public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    void logout(String token);
}

// Implementación que puede implementar solo las interfaces necesarias
@Service
@Slf4j
public class UserService implements UserReadService, UserWriteService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    // Implementación de otros métodos...
}
```

### 5. Dependency Inversion Principle (DIP)

Los módulos de alto nivel no deben depender de módulos de bajo nivel. Ambos deben depender de abstracciones.

**Ejemplo: Inyección de Dependencias con Spring**

```java
// Abstracción de alto nivel
public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    void processOrder(Long orderId);
}

// Implementación que depende de abstracciones, no de concreciones
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    // Dependencias de abstracciones, no de implementaciones concretas
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final InventoryService inventoryService;

    // Inyección de dependencias a través del constructor
    public OrderServiceImpl(
            OrderRepository orderRepository,
            PaymentService paymentService,
            EmailService emailService,
            InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.emailService = emailService;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Uso de abstracciones - no sabemos las implementaciones concretas
        Order order = mapToEntity(orderDTO);

        // Validar inventario
        if (!inventoryService.validateStock(order.getItems())) {
            throw new InsufficientStockException("Stock insuficiente");
        }

        // Procesar pago
        PaymentResponse paymentResponse = paymentService.processPayment(
                createPaymentRequest(order));

        if (!paymentResponse.isSuccess()) {
            throw new PaymentProcessingException("Error al procesar el pago");
        }

        // Guardar orden
        Order savedOrder = orderRepository.save(order);

        // Enviar confirmación por email
        emailService.sendOrderConfirmation(savedOrder);

        return mapToDTO(savedOrder);
    }
}
```

## Configuración Spring Boot

**Ejemplo: Configuración de Beans**

```java
@Configuration
@EnableJpaRepositories(basePackages = "com.comoencasa_backend.repository")
@ComponentScan(basePackages = "com.comoencasa_backend")
public class ApplicationConfig {

    @Bean
    @Primary
    public PaymentService paymentService() {
        // Configuración flexible basada en propiedades
        return new PayPalPaymentService();
    }

    @Bean
    @Conditional(CreditCardCondition.class)
    public PaymentService creditCardPaymentService() {
        return new CreditCardPaymentService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## Ventajas de la Implementación SOLID

### 🎯 **Mantenibilidad**

- Cada clase tiene una responsabilidad clara
- Los cambios son localizados y predecibles
- Fácil identificación de errores

### 🔧 **Extensibilidad**

- Nuevas funcionalidades sin modificar código existente
- Sistema de plugins y extensiones
- Fácil adición de nuevos tipos de pago

### 🧪 **Testabilidad**

- Dependencias fácilmente mockeable
- Pruebas unitarias aisladas
- Cobertura de código mejorada

### 📈 **Escalabilidad**

- Arquitectura modular y flexible
- Fácil paralelización de desarrollo
- Reutilización de componentes

## Integración con Spring Boot

El framework Spring Boot facilita la implementación de principios SOLID a través de:

- **Inyección de Dependencias**: Automática a través de anotaciones
- **Configuración por Convención**: Reducción de código boilerplate
- **Aspect-Oriented Programming**: Separación de concerns transversales
- **Perfiles de Configuración**: Flexibilidad en diferentes entornos

## Patrones Complementarios

Los principios SOLID se complementan con otros patrones implementados:

- **MVC**: Separación clara de responsabilidades
- **DAO**: Inversión de dependencias en acceso a datos
- **Builder**: Creación de objetos complejos respetando SRP
- **Factory**: Extensibilidad siguiendo OCP

Esta implementación asegura que el sistema "Como en Casa" mantiene un diseño robusto, escalable y mantenible siguiendo las mejores prácticas de la ingeniería de software.
// Lógica específica para tokens de verificación
}

    // Método auxiliar - relacionado con la responsabilidad principal
    public boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) return false;
        int atIndex = email.indexOf('@');
        return atIndex > 0 && atIndex < email.length() - 1;
    }

}

````

**✅ ProductoServiceImpl - Responsabilidad única: Gestión de productos**

```java
@Service
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // ÚNICA RESPONSABILIDAD: Operaciones de productos
    @Override
    public List<Producto> findAllAvailable() {
        return productoRepository.findByDisponibleTrue();
    }

    @Override
    public Producto actualizarStock(Long productoId, Integer nuevaCantidad) {
        // Validaciones específicas de stock
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        if (nuevaCantidad == null || nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser nula o negativa");
        }

        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setCantidad(nuevaCantidad);

            // Lógica de negocio específica de productos
            if (nuevaCantidad > 0) {
                producto.setDisponible(true);
            } else {
                producto.setDisponible(false);
            }

            return productoRepository.save(producto);
        }
        throw new IllegalArgumentException("Producto no encontrado");
    }

    // Método auxiliar - relacionado con la responsabilidad principal
    private void sanitize(Producto p) {
        p.setNombre(StringEscapeUtils.escapeHtml4(StringUtils.trimToEmpty(p.getNombre())));
        p.setDescripcion(StringEscapeUtils.escapeHtml4(StringUtils.trimToEmpty(p.getDescripcion())));
    }
}
````

### 🔹 **2. Open/Closed Principle (OCP)**

> _"Las clases deben estar abiertas para extensión, pero cerradas para modificación"_

#### **📍 Implementación:**

**✅ Sistema de Services extensible sin modificar código existente**

```java
// Interface base - CERRADA para modificación
public interface EmailService {
    void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena);
    void enviarEmailRecuperacion(String destinoEmail, String token);
    void enviarTokenVerificacion(String destinoEmail, String token);
}

// Implementación actual - CERRADA para modificación
@Service
public class EmailServiceImpl implements EmailService {
    // Implementación estable
}

// EXTENSIÓN: Nueva implementación sin modificar la existente
@Service
@Profile("sms")
public class SMSEmailServiceImpl implements EmailService {

    @Override
    public void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena) {
        // Nueva funcionalidad: envío por SMS además de email
        enviarEmail(destinoEmail, nuevaContrasena);
        enviarSMS(destinoEmail, nuevaContrasena);
    }

    // Nuevos métodos sin afectar la interface existente
    private void enviarSMS(String telefono, String mensaje) {
        // Lógica de SMS
    }
}
```

**✅ Sistema de DAO extensible**

```java
// Interface DAO - CERRADA para modificación
public interface CarritoDAO {
    void guardarCarrito(String sessionId, CarritoDTO carrito);
    Optional<CarritoDTO> obtenerCarrito(String sessionId);
    void eliminarCarrito(String sessionId);
}

// Implementación actual con Cache - CERRADA para modificación
@Repository
public class CarritoDAOImpl implements CarritoDAO {
    private final Cache<String, CarritoDTO> carritoCache;
    // Implementación con Guava Cache
}

// EXTENSIÓN: Nueva implementación sin modificar la existente
@Repository
@Profile("database")
public class CarritoDAODatabaseImpl implements CarritoDAO {

    private final CarritoRepository carritoRepository;

    @Override
    public void guardarCarrito(String sessionId, CarritoDTO carrito) {
        // Nueva funcionalidad: persistencia en base de datos
        CarritoEntity entity = toEntity(carrito);
        carritoRepository.save(entity);
    }
}
```

### 🔹 **3. Liskov Substitution Principle (LSP)**

> _"Los objetos de una superclase deben ser reemplazables por objetos de sus subclases sin alterar el funcionamiento"_

#### **📍 Implementación:**

**✅ Substitución de implementaciones de Service**

```java
// Contrato base que todas las implementaciones deben cumplir
public interface ProductoService {
    List<Producto> findAllAvailable();
    Optional<Producto> findById(Long id);
    Producto actualizarStock(Long productoId, Integer nuevaCantidad);
}

// Implementación principal
@Service
@Primary
public class ProductoServiceImpl implements ProductoService {

    @Override
    public List<Producto> findAllAvailable() {
        // Retorna productos disponibles desde BD
        return productoRepository.findByDisponibleTrue();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return productoRepository.findById(id);
    }
}

// Implementación alternativa - SUSTITUIBLE sin romper funcionalidad
@Service
@Profile("cache")
public class ProductoServiceCacheImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final Cache<Long, Producto> productCache;

    @Override
    public List<Producto> findAllAvailable() {
        // Misma funcionalidad, diferente implementación
        return getCachedProducts().stream()
                .filter(Producto::getDisponible)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> findById(Long id) {
        // Mantiene el mismo contrato: validación + retorno
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return Optional.ofNullable(productCache.getIfPresent(id));
    }
}
```

**✅ Uso polimórfico en CarritoService**

```java
@Service
public class CarritoServiceImpl implements CarritoService {

    // Cualquier implementación de ProductoService es válida
    private final ProductoService productoService;

    public CarritoServiceImpl(ProductoService productoService) {
        this.productoService = productoService; // LSP: cualquier implementación funciona
    }

    @Override
    public CarritoDTO agregarProducto(String sessionId, Long productoId, Integer cantidad, String comentarios) {
        // El código funciona igual con ProductoServiceImpl o ProductoServiceCacheImpl
        Optional<Producto> productoOpt = productoService.findById(productoId);
        if (!productoOpt.isPresent()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        // ... resto de lógica
    }
}
```

### 🔹 **4. Interface Segregation Principle (ISP)**

> _"Los clientes no deben ser forzados a depender de interfaces que no usan"_

#### **📍 Implementación:**

**✅ Interfaces específicas en lugar de una interface monolítica**

```java
// ❌ MAL: Interface monolítica (violación de ISP)
// public interface UsuarioServiceMonolitico {
//     Optional<Usuario> buscarPorEmail(String email);
//     void actualizarContrasena(Usuario usuario, String nuevaContrasena);
//     void recuperarCuenta(String email);
//     String generarTokenVerificacion(String email);
//     boolean activarCuenta(String token);
//     List<Usuario> obtenerTodosLosUsuarios(); // ¿Por qué EmailService necesitaría esto?
//     void eliminarUsuario(Long id); // ¿Por qué AuthService necesitaría esto?
// }

// ✅ BIEN: Interfaces segregadas por responsabilidad
public interface UsuarioService {
    // Solo operaciones esenciales de usuario
    Optional<Usuario> buscarPorEmail(String email);
    void actualizarContrasena(Usuario usuario, String nuevaContrasena);
    void recuperarCuenta(String email);
}

public interface VerificationTokenService {
    // Solo operaciones de tokens
    String generarToken(String email);
    String obtenerEmailPorToken(String token);
    void eliminarToken(String token);
    boolean tokenEsValido(String token);
}

// Interface específica para cada tipo de email
public interface EmailService {
    // Solo operaciones de envío de email
    void enviarNuevaContrasena(String destinoEmail, String nuevaContrasena);
    void enviarEmailRecuperacion(String destinoEmail, String token);
    void enviarTokenVerificacion(String destinoEmail, String token);
}
```

**✅ Repositories específicos**

```java
// Interface específica para productos
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByDisponibleTrue();
    List<Producto> findByCategoriaIdAndDisponibleTrue(Long categoriaId);
}

// Interface específica para usuarios
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}

// Interface específica para pedidos
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
}

// Interface específica para comprobantes
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    long countByTipo(TipoComprobante tipo);
    List<Comprobante> findByPedido_Id(Long pedidoId);
    List<Comprobante> findByFechaEmisionBetween(LocalDateTime desde, LocalDateTime hasta);
}
```

### 🔹 **5. Dependency Inversion Principle (DIP)**

> _"Depender de abstracciones, no de concreciones"_

#### **📍 Implementación:**

**✅ Inyección de dependencias con interfaces**

```java
@Service
public class CarritoServiceImpl implements CarritoService {

    // DIP: Depende de abstracciones (interfaces), no de implementaciones concretas
    private final CarritoDAO carritoDAO;           // Interface, no CarritoDAOImpl
    private final ProductoService productoService; // Interface, no ProductoServiceImpl

    // Constructor injection con abstracciones
    public CarritoServiceImpl(CarritoDAO carritoDAO, ProductoService productoService) {
        this.carritoDAO = carritoDAO;
        this.productoService = productoService;
        log.info("CarritoService inicializado con DAO y ProductoService");
    }

    @Override
    public CarritoDTO agregarProducto(String sessionId, Long productoId, Integer cantidad, String comentarios) {
        // Usa abstracciones - no sabe qué implementación específica está usando
        Optional<Producto> productoOpt = productoService.findById(productoId);
        Optional<CarritoDTO> carritoOpt = carritoDAO.obtenerCarrito(sessionId);

        // ... lógica de negocio ...

        carritoDAO.guardarCarrito(sessionId, carrito);
        return carrito;
    }
}
```

**✅ Configuración de Spring permite cambio de implementaciones**

```java
// Configuración para desarrollo (cache en memoria)
@Configuration
@Profile("dev")
public class DevConfig {

    @Bean
    @Primary
    public CarritoDAO carritoDAO() {
        return new CarritoDAOImpl(); // Implementación con cache
    }

    @Bean
    @Primary
    public ProductoService productoService(ProductoRepository repository) {
        return new ProductoServiceImpl(repository); // Implementación estándar
    }
}

// Configuración para producción (base de datos)
@Configuration
@Profile("prod")
public class ProdConfig {

    @Bean
    @Primary
    public CarritoDAO carritoDAO() {
        return new CarritoDAODatabaseImpl(); // Implementación con BD
    }

    @Bean
    @Primary
    public ProductoService productoService(ProductoRepository repository) {
        return new ProductoServiceCacheImpl(repository); // Implementación con cache
    }
}
```

**✅ UsuarioService con múltiples dependencias abstraídas**

```java
@Service
public class UsuarioServiceImpl implements UsuarioService {

    // DIP: Todas las dependencias son abstracciones
    private final UsuarioRepository usuarioRepository;           // Interface JPA
    private final BCryptPasswordEncoder passwordEncoder;         // Interface Spring Security
    private final EmailService emailService;                     // Interface custom
    private final VerificationTokenService verificationTokenService; // Interface custom

    @Autowired
    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailService emailService,
            VerificationTokenService verificationTokenService
    ) {
        // Inyección de abstracciones - fácil testing y cambio de implementaciones
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    @Override
    public void recuperarCuenta(String email) {
        // Usa abstracciones - no implementaciones concretas
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String nuevaPassword = generarPasswordAleatoria();
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuarioRepository.save(usuario);
            emailService.enviarNuevaContrasena(email, nuevaPassword);
        }
    }
}
```

---

## 🔄 Diagrama de Dependencias SOLID

```mermaid
graph TB
    subgraph "🎮 Controllers (DIP)"
        A[CarritoController]
        B[ProductoController]
        C[UsuarioController]
    end

    subgraph "📊 Service Interfaces (ISP + DIP)"
        D[CarritoService]
        E[ProductoService]
        F[UsuarioService]
        G[EmailService]
    end

    subgraph "🔧 Service Implementations (SRP + OCP + LSP)"
        H[CarritoServiceImpl]
        I[ProductoServiceImpl]
        J[UsuarioServiceImpl]
        K[EmailServiceImpl]
    end

    subgraph "🗄️ DAO Interfaces (ISP + DIP)"
        L[CarritoDAO]
        M[ProductoRepository]
        N[UsuarioRepository]
    end

    subgraph "💾 DAO Implementations (SRP + OCP + LSP)"
        O[CarritoDAOImpl]
        P[JPA Implementation]
        Q[JPA Implementation]
    end

    A --> D
    B --> E
    C --> F

    D -.-> H
    E -.-> I
    F -.-> J
    F -.-> G

    G -.-> K

    H --> L
    H --> E
    I --> M
    J --> N
    J --> G

    L -.-> O
    M -.-> P
    N -.-> Q

    style D fill:#e1f5fe
    style E fill:#e1f5fe
    style F fill:#e1f5fe
    style G fill:#e1f5fe
    style L fill:#fff3e0
    style M fill:#fff3e0
    style N fill:#fff3e0
```

---

## ✅ Beneficios de SOLID en el Proyecto

### **🔹 Single Responsibility (SRP):**

- **Mantenimiento fácil**: Cada clase tiene una razón específica para cambiar
- **Testing simplificado**: Tests enfocados en una responsabilidad
- **Debugging mejorado**: Errores localizados en clases específicas

### **🔹 Open/Closed (OCP):**

- **Extensibilidad**: Nuevas funcionalidades sin modificar código existente
- **Estabilidad**: Código base protegido de cambios que introducen bugs
- **Flexibilidad**: Múltiples implementaciones para diferentes entornos

### **🔹 Liskov Substitution (LSP):**

- **Polimorfismo real**: Implementaciones intercambiables sin romper funcionalidad
- **Testing robusto**: Mismas pruebas funcionan con diferentes implementaciones
- **Configuración flexible**: Cambio de implementaciones vía configuración

### **🔹 Interface Segregation (ISP):**

- **Acoplamiento bajo**: Clientes dependen solo de métodos que necesitan
- **Interfaces cohesivas**: Métodos relacionados agrupados lógicamente
- **Evolución independiente**: Interfaces pueden evolucionar sin afectar otros clientes

### **🔹 Dependency Inversion (DIP):**

- **Desacoplamiento**: Módulos de alto nivel independientes de detalles de implementación
- **Testing efectivo**: Fácil mocking de dependencias
- **Inversión de control**: Spring maneja la creación e inyección de dependencias

---

## 🧪 Testing con SOLID

### **📝 Tests facilitados por SRP:**

```java
@ExtendWith(MockitoExtension.class)
class EmailServiceTDDTest {

    @Mock
    private JavaMailSender mockMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    @DisplayName("Debería enviar email de nueva contraseña correctamente")
    void deberiaEnviarEmailNuevaContrasena() {
        // Test enfocado en UNA responsabilidad: envío de emails
        String email = "test@test.com";
        String password = "newPass123";

        emailService.enviarNuevaContrasena(email, password);

        verify(mockMailSender).send(any(SimpleMailMessage.class));
    }
}
```

### **📝 Tests facilitados por DIP:**

```java
@ExtendWith(MockitoExtension.class)
class CarritoServiceTDDTest {

    // Fácil mocking de dependencias abstraídas
    @Mock
    private CarritoDAO carritoDAO;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @Test
    @DisplayName("Debería agregar producto correctamente")
    void deberiaAgregarProducto() {
        // Given
        when(productoService.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoDAO.obtenerCarrito(sessionId)).thenReturn(Optional.empty());

        // When
        CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 2, "");

        // Then
        verify(carritoDAO).guardarCarrito(eq(sessionId), any(CarritoDTO.class));
    }
}
```

---

## 🚀 Conclusión

Los **principios SOLID** en el proyecto "Como en Casa" proporcionan una base sólida que:

- ✅ **Mejora la mantenibilidad** con responsabilidades claras (SRP)
- ✅ **Facilita la extensión** sin modificar código existente (OCP)
- ✅ **Permite substitución** segura de implementaciones (LSP)
- ✅ **Reduce acoplamiento** con interfaces específicas (ISP)
- ✅ **Invierte dependencias** para mayor flexibilidad (DIP)

Esta implementación demuestra un entendimiento profundo de los principios SOLID y su aplicación práctica en un proyecto real, resultando en un código más limpio, testeable y mantenible.
