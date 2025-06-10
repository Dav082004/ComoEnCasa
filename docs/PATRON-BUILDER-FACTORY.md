# 🏗️ Patrones Builder y Factory - Como en Casa

## 📖 Introducción

Los patrones **Builder** y **Factory** son patrones creacionales que facilitan la construcción de objetos complejos de manera flexible y mantenible. En el proyecto "Como en Casa" se implementan estos patrones principalmente para la creación de datos de prueba y objetos de transferencia.

### **🎯 Objetivos:**

- **Builder Pattern**: Construir objetos complejos paso a paso
- **Factory Pattern**: Centralizar la creación de objetos de test
- **Facilitar testing**: Crear datos de prueba consistentes y flexibles
- **Mejorar legibilidad**: Código más expresivo y fácil de entender

---

## 🎯 Implementación en el Proyecto

### 🔹 **1. Builder Pattern para DTOs**

#### **📍 Ubicación:** `dto/CarritoItemDTO.java`

**CarritoItemDTO con @Builder de Lombok**

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {
    private Long productoId;
    private String nombre;
    private String descripcion;
    private Double precioVenta;
    private String imagenUrl;
    private Integer cantidad;
    private String comentarios;
    private Double subtotal;

    // Constructor personalizado con cálculo automático
    public CarritoItemDTO(Long productoId, String nombre, Double precioVenta, Integer cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.cantidad = cantidad;
        this.subtotal = precioVenta * cantidad;
    }
}
```

**Uso del Builder en CarritoService:**

```java
@Service
public class CarritoServiceImpl implements CarritoService {

    @Override
    public CarritoDTO agregarProducto(String sessionId, Long productoId, Integer cantidad, String comentarios) {
        // ... validaciones ...

        // Uso del Builder Pattern para crear CarritoItemDTO
        CarritoItemDTO item = CarritoItemDTO.builder()
                .productoId(productoId)
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precioVenta(producto.getPrecioVenta())
                .imagenUrl(producto.getImagenUrl())
                .cantidad(cantidad)
                .comentarios(comentarios)
                .build();

        carrito.addItem(item);
        return carrito;
    }
}
```

---

### 🔹 **2. Test Data Factory Pattern**

#### **📍 Ubicación:** `test/testutil/TestDataFactory.java`

**Factory Pattern para creación de datos de prueba:**

```java
/**
 * Factory para crear objetos de test siguiendo el patrón Test Data Builder
 * Facilita la creación de datos de prueba consistentes
 */
public class TestDataFactory {

    // BUILDER INTERNO PARA PRODUCTOS
    public static class ProductoTestBuilder {
        private Producto producto;

        public ProductoTestBuilder() {
            this.producto = new Producto();
            // Valores por defecto
            this.producto.setNombre("Producto Test");
            this.producto.setDescripcion("Descripción de producto test");
            this.producto.setPrecioVenta(100.0);
            this.producto.setCostoProduccion(50.0);
            this.producto.setDisponible(true);
            this.producto.setCantidad(10);
            this.producto.setCategoriaId(1L);
        }

        public ProductoTestBuilder conNombre(String nombre) {
            this.producto.setNombre(nombre);
            return this;
        }

        public ProductoTestBuilder conPrecio(Double precio) {
            this.producto.setPrecioVenta(precio);
            return this;
        }

        public ProductoTestBuilder conCosto(Double costo) {
            this.producto.setCostoProduccion(costo);
            return this;
        }

        public ProductoTestBuilder conCategoria(Long categoriaId) {
            this.producto.setCategoriaId(categoriaId);
            return this;
        }

        public ProductoTestBuilder noDisponible() {
            this.producto.setDisponible(false);
            return this;
        }

        public ProductoTestBuilder conCantidad(Integer cantidad) {
            this.producto.setCantidad(cantidad);
            return this;
        }

        public ProductoTestBuilder conId(Long id) {
            this.producto.setId(id);
            return this;
        }

        public Producto build() {
            return this.producto;
        }
    }

    // BUILDER INTERNO PARA USUARIOS
    public static class UsuarioTestBuilder {
        private Usuario usuario;

        public UsuarioTestBuilder() {
            this.usuario = new Usuario();
            // Valores por defecto
            this.usuario.setNombre("Usuario");
            this.usuario.setApellido("Test");
            this.usuario.setEmail("test@test.com");
            this.usuario.setPassword("password123");
            this.usuario.setTelefono("123456789");
            this.usuario.setDireccion("Dirección Test");
            this.usuario.setTipoDocumento(Usuario.TipoDocumento.DNI);
            this.usuario.setNumeroDocumento("12345678");
            this.usuario.setRol(Usuario.Rol.CLIENTE);
            this.usuario.setActivado(true);
        }

        public UsuarioTestBuilder conNombre(String nombre) {
            this.usuario.setNombre(nombre);
            return this;
        }

        public UsuarioTestBuilder conApellido(String apellido) {
            this.usuario.setApellido(apellido);
            return this;
        }

        public UsuarioTestBuilder conNombreCompleto(String nombre, String apellido) {
            this.usuario.setNombre(nombre);
            this.usuario.setApellido(apellido);
            return this;
        }

        public UsuarioTestBuilder conEmail(String email) {
            this.usuario.setEmail(email);
            return this;
        }

        public UsuarioTestBuilder conPassword(String password) {
            this.usuario.setPassword(password);
            return this;
        }

        public UsuarioTestBuilder conRol(Usuario.Rol rol) {
            this.usuario.setRol(rol);
            return this;
        }

        public UsuarioTestBuilder inactivo() {
            this.usuario.setActivado(false);
            return this;
        }

        public UsuarioTestBuilder conId(Long id) {
            this.usuario.setId(id);
            return this;
        }

        public Usuario build() {
            return this.usuario;
        }
    }

    // BUILDER INTERNO PARA CATEGORÍAS
    public static class CategoriaTestBuilder {
        private Categoria categoria;

        public CategoriaTestBuilder() {
            this.categoria = new Categoria();
            this.categoria.setNombre("Categoria Test");
            this.categoria.setDescripcion("Descripción de categoría test");
        }

        public CategoriaTestBuilder conNombre(String nombre) {
            this.categoria.setNombre(nombre);
            return this;
        }

        public CategoriaTestBuilder conDescripcion(String descripcion) {
            this.categoria.setDescripcion(descripcion);
            return this;
        }

        public CategoriaTestBuilder conId(Long id) {
            this.categoria.setId(id);
            return this;
        }

        public Categoria build() {
            return this.categoria;
        }
    }

    // MÉTODOS FACTORY ESTÁTICOS
    public static ProductoTestBuilder unProducto() {
        return new ProductoTestBuilder();
    }

    public static UsuarioTestBuilder unUsuario() {
        return new UsuarioTestBuilder();
    }

    public static CategoriaTestBuilder unaCategoria() {
        return new CategoriaTestBuilder();
    }
}
```

---

### 🔹 **3. Uso en Tests TDD**

#### **📍 Implementación en tests:**

**Ejemplo en CarritoServiceTDDTest:**

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("🧪 Tests TDD - Servicio de Carrito")
class CarritoServiceTDDTest {

    @Test
    @DisplayName("Debería agregar producto correctamente")
    void deberiaAgregarProducto() {
        // Given - Usando Factory Pattern + Builder
        Producto producto = TestDataFactory.unProducto()
                .conId(1L)
                .conNombre("Torta de Chocolate")
                .conPrecio(25.50)
                .conCantidad(5)
                .build();

        when(productoService.findById(1L)).thenReturn(Optional.of(producto));

        // When
        CarritoDTO resultado = carritoService.agregarProducto(sessionId, 1L, 2, "Sin azúcar");

        // Then
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getNombre()).isEqualTo("Torta de Chocolate");
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(2);
        assertThat(resultado.getItems().get(0).getComentarios()).isEqualTo("Sin azúcar");
    }

    @Test
    @DisplayName("Debería validar usuario existente")
    void deberiaValidarUsuarioExistente() {
        // Given - Usando múltiples builders para diferentes escenarios
        Usuario usuarioAdmin = TestDataFactory.unUsuario()
                .conId(1L)
                .conEmail("admin@test.com")
                .conRol(Usuario.Rol.ADMIN)
                .build();

        Usuario usuarioCliente = TestDataFactory.unUsuario()
                .conId(2L)
                .conEmail("cliente@test.com")
                .conRol(Usuario.Rol.CLIENTE)
                .build();

        Usuario usuarioInactivo = TestDataFactory.unUsuario()
                .conId(3L)
                .conEmail("inactivo@test.com")
                .inactivo()
                .build();

        // ... resto del test
    }
}
```

**Ejemplo en ProductoServiceTDDTest:**

```java
@ExtendWith(MockitoExtension.class)
class ProductoServiceTDDTest {

    @Nested
    @DisplayName("🔍 Buscar Productos por Categoría")
    class BuscarPorCategoria {

        @Test
        @DisplayName("Debería retornar productos de categoría específica")
        void deberiaRetornarProductosDeCategoria() {
            // Given - Factory + Builder para múltiples productos
            List<Producto> productos = Arrays.asList(
                TestDataFactory.unProducto()
                    .conId(1L)
                    .conNombre("Torta Red Velvet")
                    .conCategoria(1L)
                    .conPrecio(30.0)
                    .build(),

                TestDataFactory.unProducto()
                    .conId(2L)
                    .conNombre("Cheesecake")
                    .conCategoria(1L)
                    .conPrecio(25.0)
                    .build(),

                TestDataFactory.unProducto()
                    .conId(3L)
                    .conNombre("Pan Integral")
                    .conCategoria(2L)  // Categoría diferente
                    .conPrecio(5.0)
                    .build()
            );

            when(productoRepository.findByCategoriaIdAndDisponibleTrue(1L))
                .thenReturn(productos.stream()
                    .filter(p -> p.getCategoriaId().equals(1L))
                    .collect(Collectors.toList()));

            // When
            List<Producto> resultado = productoService.findByCategoriaId(1L);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).extracting(Producto::getNombre)
                .containsExactly("Torta Red Velvet", "Cheesecake");
        }
    }
}
```

---

## 🔄 Flujo de Patrones Creacionales

### **📊 Diagrama de Flujo:**

```mermaid
graph TB
    subgraph "🏭 FACTORY PATTERN"
        A[TestDataFactory]
        B[unProducto()]
        C[unUsuario()]
        D[unaCategoria()]
    end

    subgraph "🏗️ BUILDER PATTERN"
        E[ProductoTestBuilder]
        F[UsuarioTestBuilder]
        G[CategoriaTestBuilder]
        H[CarritoItemDTO.builder()]
    end

    subgraph "🧪 TEST USAGE"
        I[CarritoServiceTDDTest]
        J[ProductoServiceTDDTest]
        K[UsuarioServiceTDDTest]
        L[EmailServiceTDDTest]
    end

    subgraph "📦 PRODUCTION USAGE"
        M[CarritoServiceImpl]
        N[ProductoServiceImpl]
        O[UsuarioServiceImpl]
    end

    A --> B
    A --> C
    A --> D

    B --> E
    C --> F
    D --> G

    E --> I
    F --> I
    G --> I
    E --> J
    F --> K

    H --> M
    H --> N
    H --> O

    I --> K
    J --> K
    K --> L

    style A fill:#e1f5fe
    style H fill:#f3e5f5
    style I fill:#e8f5e8
```

---

## ✅ Ventajas de los Patrones Builder y Factory

### **🔹 Builder Pattern:**

- **Flexibilidad**: Construcción paso a paso de objetos complejos
- **Legibilidad**: Código más expresivo y fácil de entender
- **Inmutabilidad**: Objetos bien construidos desde el inicio
- **Validación**: Posibilidad de validar durante la construcción

### **🔹 Factory Pattern:**

- **Centralización**: Un punto único para creación de objetos de test
- **Consistencia**: Datos de prueba uniformes en todo el proyecto
- **Mantenibilidad**: Fácil cambio de configuración por defecto
- **Reutilización**: Mismos builders usados en múltiples tests

### **🔹 Combinación Factory + Builder:**

- **Flexibilidad máxima**: Factory proporciona builders configurables
- **API fluida**: Sintaxis natural para creación de datos
- **Escalabilidad**: Fácil agregar nuevos tipos de objetos
- **Testing eficiente**: Datos de prueba complejos con sintaxis simple

---

## 🎯 Mejores Prácticas Implementadas

### **✅ En Test Data Factory:**

- **Valores por defecto sensatos** para todos los campos
- **Métodos de configuración específicos** para cada caso de uso
- **Naming consistente** (conNombre, conPrecio, etc.)
- **Builders anidados** dentro de la factory para organización

### **✅ En Production Builders:**

- **Uso de Lombok @Builder** para simplificar código
- **Constructores personalizados** cuando es necesario
- **Validación en métodos build()** si es requerida
- **Immutabilidad** cuando es posible

### **✅ En Testing:**

- **Un builder por test** para casos específicos
- **Reutilización** de configuraciones base
- **Tests más legibles** con sintaxis fluida
- **Datos aislados** entre diferentes tests

---

## 🧪 Testing de Patrones Creacionales

### **📝 Ejemplo de test para Factory:**

```java
@Test
@DisplayName("Factory debería crear productos con valores por defecto")
void factoryDeberiaCrearProductosConValoresPorDefecto() {
    // When
    Producto producto = TestDataFactory.unProducto().build();

    // Then
    assertThat(producto.getNombre()).isEqualTo("Producto Test");
    assertThat(producto.getPrecioVenta()).isEqualTo(100.0);
    assertThat(producto.getDisponible()).isTrue();
    assertThat(producto.getCantidad()).isEqualTo(10);
}

@Test
@DisplayName("Builder debería permitir configuración personalizada")
void builderDeberiaPermitirConfiguracionPersonalizada() {
    // When
    Producto producto = TestDataFactory.unProducto()
            .conNombre("Torta Personalizada")
            .conPrecio(45.0)
            .conCantidad(3)
            .noDisponible()
            .build();

    // Then
    assertThat(producto.getNombre()).isEqualTo("Torta Personalizada");
    assertThat(producto.getPrecioVenta()).isEqualTo(45.0);
    assertThat(producto.getCantidad()).isEqualTo(3);
    assertThat(producto.getDisponible()).isFalse();
}
```

### **📝 Ejemplo de test para Builder en producción:**

```java
@Test
@DisplayName("CarritoItemDTO builder debería calcular subtotal correctamente")
void carritoItemBuilderDeberiaCalcularSubtotal() {
    // When
    CarritoItemDTO item = CarritoItemDTO.builder()
            .productoId(1L)
            .nombre("Torta")
            .precioVenta(25.0)
            .cantidad(3)
            .build();

    // Then
    assertThat(item.getSubtotal()).isEqualTo(75.0);
    assertThat(item.getNombre()).isEqualTo("Torta");
}
```

---

## 🔧 Tecnologías Utilizadas

### **🏗️ Para Builder Pattern:**

- **Lombok @Builder** - Generación automática de builders
- **Constructores personalizados** - Lógica de construcción específica
- **Validation** - Validación durante construcción

### **🏭 Para Factory Pattern:**

- **Clases estáticas anidadas** - Organización de builders
- **Métodos factory estáticos** - Punto de entrada simple
- **Configuración por defecto** - Valores sensatos para testing

### **🧪 Para Testing:**

- **JUnit 5** - Framework de testing
- **AssertJ** - Aserciones fluidas
- **Mockito** - Mocking para aislamiento

---

## 🚀 Conclusión

Los patrones **Builder** y **Factory** en el proyecto "Como en Casa" proporcionan:

- ✅ **Simplificación de testing** con datos consistentes y flexibles
- ✅ **Mejora en legibilidad** del código de pruebas y producción
- ✅ **Flexibilidad en construcción** de objetos complejos
- ✅ **Mantenibilidad mejorada** con puntos centralizados de creación
- ✅ **Escalabilidad** fácil para nuevos tipos de objetos

Esta implementación demuestra el uso efectivo de patrones creacionales para mejorar la calidad del código, especialmente en el contexto de testing y creación de objetos de transferencia de datos.
