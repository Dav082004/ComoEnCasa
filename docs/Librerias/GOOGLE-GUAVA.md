# 🗂️ Google Guava - Implementación en Como en Casa

## 📋 Descripción General

Google Guava es una biblioteca de utilidades Java desarrollada por Google que proporciona estructuras de datos avanzadas, utilidades para colecciones, caching, y muchas otras funcionalidades. En el proyecto "Como en Casa", Guava se utiliza principalmente para implementar un sistema de cache en memoria eficiente para el manejo de carritos de compra.

## 🎯 ¿Qué es Google Guava?

Google Guava es una biblioteca de código abierto que extiende las funcionalidades básicas de Java con utilidades probadas en producción por Google. Ofrece herramientas para caching, colecciones inmutables, programación funcional, y manejo de strings.

### 💼 Beneficios en el Proyecto

- **⚡ Cache de Alto Rendimiento**: Sistema de cache en memoria con TTL y límites de tamaño
- **🔒 Thread-Safe**: Operaciones concurrentes seguras sin bloqueos
- **📊 Gestión Automática**: Expiration policies y cleanup automático
- **🎯 Eficiencia**: Acceso O(1) a datos del carrito de compras

## 🔄 Flujo de Información con Google Guava

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │────│   Controllers   │────│   Guava Cache   │
│                 │    │                 │    │                 │
│ • Añadir item   │    │ • Validar       │    │ • Almacenar     │
│ • Ver carrito   │    │ • Procesar      │    │ • Recuperar     │
│ • Checkout      │    │ • Responder     │    │ • Expirar       │
│ • Actualizar    │    │ • Cache hit/miss│    │ • Limpiar       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🏗️ Arquitectura del Cache

```
Application Layer
        ↓
   CarritoService
        ↓
    CarritoDAO
        ↓
   ┌─────────────────┐
   │  Guava Cache    │
   ├─────────────────┤
   │ • Key: SessionID│
   │ • Value: Carrito│
   │ • TTL: 2 hours  │
   │ • Size: 1000    │
   │ • Thread-Safe   │
   └─────────────────┘
```

## 🛠️ Configuración Principal

### 📦 Dependencias Maven

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>32.1.2-jre</version>
</dependency>
```

### 🔧 Implementación del Cache

**Archivo:** `CarritoDAOImpl.java`

```java
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CarritoDAOImpl implements CarritoDAO {

    // Cache en memoria con Google Guava
    private final Cache<String, CarritoDTO> carritoCache;

    public CarritoDAOImpl() {
        this.carritoCache = CacheBuilder.newBuilder()
                .maximumSize(1000) // Máximo 1000 carritos en memoria
                .expireAfterAccess(2, TimeUnit.HOURS) // Expira después de 2 horas sin acceso
                .removalListener(notification -> {
                    log.info("Carrito removido del cache: sessionId={}, causa={}",
                            notification.getKey(), notification.getCause());
                })
                .build();

        log.info("CarritoDAO inicializado con cache Guava - Max: 1000, TTL: 2h");
    }

    @Override
    public void guardarCarrito(String sessionId, CarritoDTO carrito) {
        if (sessionId == null || carrito == null) {
            throw new IllegalArgumentException("SessionId y carrito no pueden ser nulos");
        }

        carritoCache.put(sessionId, carrito);
        log.debug("Carrito guardado: sessionId={}, items={}, total={}",
                sessionId, carrito.getTotalItems(), carrito.getTotal());
    }

    @Override
    public Optional<CarritoDTO> obtenerCarrito(String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }

        CarritoDTO carrito = carritoCache.getIfPresent(sessionId);
        if (carrito != null) {
            log.debug("Cache HIT: sessionId={}, items={}", sessionId, carrito.getTotalItems());
            return Optional.of(carrito);
        } else {
            log.debug("Cache MISS: sessionId={}", sessionId);
            return Optional.empty();
        }
    }
}
```

## 💻 Funcionalidades Implementadas

### 1. 🛒 Gestión de Carritos

```java
@Service
@Slf4j
public class CarritoService {

    private final CarritoDAO carritoDAO;

    public CarritoService(CarritoDAO carritoDAO) {
        this.carritoDAO = carritoDAO;
    }

    public CarritoDTO obtenerCarrito(String sessionId) {
        log.debug("Obteniendo carrito para sesión: {}", sessionId);

        return carritoDAO.obtenerCarrito(sessionId)
                .orElseGet(() -> {
                    log.debug("Creando nuevo carrito para sesión: {}", sessionId);
                    return new CarritoDTO();
                });
    }

    public CarritoDTO agregarItem(String sessionId, CarritoItemDTO item) {
        log.info("Agregando item al carrito: sessionId={}, productoId={}, cantidad={}",
                sessionId, item.getProductoId(), item.getCantidad());

        CarritoDTO carrito = obtenerCarrito(sessionId);

        // Verificar si el item ya existe
        Optional<CarritoItemDTO> existingItem = carrito.getItems().stream()
                .filter(i -> i.getProductoId().equals(item.getProductoId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Actualizar cantidad
            CarritoItemDTO existing = existingItem.get();
            existing.setCantidad(existing.getCantidad() + item.getCantidad());
            log.debug("Item actualizado: productoId={}, nuevaCantidad={}",
                     item.getProductoId(), existing.getCantidad());
        } else {
            // Agregar nuevo item
            carrito.getItems().add(item);
            log.debug("Nuevo item agregado: productoId={}, cantidad={}",
                     item.getProductoId(), item.getCantidad());
        }

        // Recalcular totales
        recalcularTotales(carrito);

        // Guardar en cache
        carritoDAO.guardarCarrito(sessionId, carrito);

        return carrito;
    }

    public CarritoDTO removerItem(String sessionId, Long productoId) {
        log.info("Removiendo item del carrito: sessionId={}, productoId={}",
                sessionId, productoId);

        CarritoDTO carrito = obtenerCarrito(sessionId);

        boolean removed = carrito.getItems().removeIf(item ->
            item.getProductoId().equals(productoId));

        if (removed) {
            log.debug("Item removido exitosamente: productoId={}", productoId);
            recalcularTotales(carrito);
            carritoDAO.guardarCarrito(sessionId, carrito);
        } else {
            log.warn("Item no encontrado para remover: productoId={}", productoId);
        }

        return carrito;
    }

    public void limpiarCarrito(String sessionId) {
        log.info("Limpiando carrito: sessionId={}", sessionId);

        CarritoDTO carritoVacio = new CarritoDTO();
        carritoDAO.guardarCarrito(sessionId, carritoVacio);
    }

    private void recalcularTotales(CarritoDTO carrito) {
        BigDecimal total = carrito.getItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(
                    new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        carrito.setTotal(total);
        carrito.setTotalItems(carrito.getItems().size());

        log.debug("Totales recalculados: items={}, total={}",
                 carrito.getTotalItems(), carrito.getTotal());
    }
}
```

### 2. 🔧 Configuración Avanzada del Cache

```java
@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, CarritoDTO> carritoCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(2, TimeUnit.HOURS)
                .expireAfterWrite(4, TimeUnit.HOURS) // Expira después de 4 horas sin importar acceso
                .refreshAfterWrite(1, TimeUnit.HOURS) // Refrescar después de 1 hora
                .recordStats() // Habilitar estadísticas
                .removalListener(this::onRemoval)
                .build();
    }

    private void onRemoval(RemovalNotification<String, CarritoDTO> notification) {
        log.info("Carrito removido: sessionId={}, causa={}, items={}",
                notification.getKey(),
                notification.getCause(),
                notification.getValue() != null ? notification.getValue().getTotalItems() : 0);
    }

    @Bean
    public CacheStatsService cacheStatsService() {
        return new CacheStatsService();
    }
}
```

### 3. 📊 Monitoreo y Estadísticas

```java
@Service
@Slf4j
public class CacheStatsService {

    private final Cache<String, CarritoDTO> carritoCache;

    public CacheStatsService(Cache<String, CarritoDTO> carritoCache) {
        this.carritoCache = carritoCache;
    }

    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public void logCacheStats() {
        CacheStats stats = carritoCache.stats();

        log.info("Cache Stats - Hits: {}, Misses: {}, Hit Rate: {:.2f}%, " +
                "Evictions: {}, Size: {}",
                stats.hitCount(),
                stats.missCount(),
                stats.hitRate() * 100,
                stats.evictionCount(),
                carritoCache.size());
    }

    public CacheStatsDTO getCacheStats() {
        CacheStats stats = carritoCache.stats();

        return CacheStatsDTO.builder()
                .hitCount(stats.hitCount())
                .missCount(stats.missCount())
                .hitRate(stats.hitRate())
                .evictionCount(stats.evictionCount())
                .loadCount(stats.loadCount())
                .averageLoadTime(stats.averageLoadTime())
                .size(carritoCache.size())
                .build();
    }

    public void invalidateAllCaches() {
        log.warn("Invalidando todos los carritos del cache");
        carritoCache.invalidateAll();
    }

    public void invalidateCarrito(String sessionId) {
        log.info("Invalidando carrito: sessionId={}", sessionId);
        carritoCache.invalidate(sessionId);
    }
}
```

## 🎛️ Configuraciones Específicas

### 1. ⚙️ Políticas de Expiración

```java
public class CachePolicyConfig {

    // Expiración basada en tiempo de acceso
    public static Cache<String, CarritoDTO> createAccessBasedCache() {
        return CacheBuilder.newBuilder()
                .expireAfterAccess(2, TimeUnit.HOURS)
                .maximumSize(1000)
                .build();
    }

    // Expiración basada en tiempo de escritura
    public static Cache<String, CarritoDTO> createWriteBasedCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(4, TimeUnit.HOURS)
                .maximumSize(1000)
                .build();
    }

    // Expiración combinada
    public static Cache<String, CarritoDTO> createCombinedCache() {
        return CacheBuilder.newBuilder()
                .expireAfterAccess(2, TimeUnit.HOURS)
                .expireAfterWrite(4, TimeUnit.HOURS)
                .refreshAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(1000)
                .build();
    }
}
```

### 2. 🔄 Refresh y Reload Automático

```java
@Service
public class CacheRefreshService {

    private final Cache<String, CarritoDTO> carritoCache;
    private final CarritoService carritoService;

    public CacheRefreshService(Cache<String, CarritoDTO> carritoCache,
                              CarritoService carritoService) {
        this.carritoCache = carritoCache;
        this.carritoService = carritoService;
    }

    public CarritoDTO getCarritoWithRefresh(String sessionId) {
        try {
            return carritoCache.get(sessionId, () -> {
                log.debug("Cargando carrito desde fuente: sessionId={}", sessionId);
                return carritoService.crearCarritoVacio();
            });
        } catch (Exception e) {
            log.error("Error cargando carrito: sessionId={}", sessionId, e);
            return carritoService.crearCarritoVacio();
        }
    }

    @Scheduled(fixedRate = 60000) // Cada minuto
    public void refreshActiveCarritos() {
        log.debug("Refrescando carritos activos");

        Map<String, CarritoDTO> allCarritos = carritoCache.asMap();

        allCarritos.entrySet().parallelStream()
                .filter(entry -> isActiveCarrito(entry.getValue()))
                .forEach(entry -> {
                    try {
                        carritoCache.refresh(entry.getKey());
                    } catch (Exception e) {
                        log.warn("Error refrescando carrito: sessionId={}",
                                entry.getKey(), e);
                    }
                });
    }

    private boolean isActiveCarrito(CarritoDTO carrito) {
        return carrito != null &&
               carrito.getTotalItems() > 0 &&
               carrito.getUltimaActualizacion() != null &&
               carrito.getUltimaActualizacion().isAfter(LocalDateTime.now().minusHours(1));
    }
}
```

## 🧪 Testing con Google Guava

### 1. 🔍 Tests de Cache

```java
@ExtendWith(MockitoExtension.class)
class CarritoDAOImplTest {

    private CarritoDAOImpl carritoDAO;
    private Cache<String, CarritoDTO> testCache;

    @BeforeEach
    void setUp() {
        testCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build();

        carritoDAO = new CarritoDAOImpl();
        // Inyectar cache de prueba
        ReflectionTestUtils.setField(carritoDAO, "carritoCache", testCache);
    }

    @Test
    void testGuardarYObtenerCarrito() {
        // Given
        String sessionId = "test-session";
        CarritoDTO carrito = new CarritoDTO();
        carrito.setTotalItems(2);
        carrito.setTotal(new BigDecimal("50.00"));

        // When
        carritoDAO.guardarCarrito(sessionId, carrito);
        Optional<CarritoDTO> resultado = carritoDAO.obtenerCarrito(sessionId);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTotalItems()).isEqualTo(2);
        assertThat(resultado.get().getTotal()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        // Given
        String sessionId = "expiring-session";
        CarritoDTO carrito = new CarritoDTO();

        // When
        carritoDAO.guardarCarrito(sessionId, carrito);

        // Verificar que existe
        assertThat(carritoDAO.obtenerCarrito(sessionId)).isPresent();

        // Esperar expiración
        Thread.sleep(61000); // 1 minuto + 1 segundo

        // Then
        assertThat(carritoDAO.obtenerCarrito(sessionId)).isEmpty();
    }

    @Test
    void testCacheStats() {
        // Given
        String sessionId = "stats-session";
        CarritoDTO carrito = new CarritoDTO();

        // When
        carritoDAO.guardarCarrito(sessionId, carrito);
        carritoDAO.obtenerCarrito(sessionId); // Hit
        carritoDAO.obtenerCarrito("non-existent"); // Miss

        // Then
        CacheStats stats = testCache.stats();
        assertThat(stats.hitCount()).isEqualTo(1);
        assertThat(stats.missCount()).isEqualTo(1);
        assertThat(stats.hitRate()).isEqualTo(0.5);
    }
}
```

### 2. 📊 Tests de Rendimiento

```java
@Test
void testCachePerformance() {
    // Given
    Cache<String, CarritoDTO> performanceCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .recordStats()
            .build();

    // When - Escribir 1000 carritos
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < 1000; i++) {
        String sessionId = "session-" + i;
        CarritoDTO carrito = new CarritoDTO();
        carrito.setTotalItems(i % 10);

        performanceCache.put(sessionId, carrito);
    }

    long writeTime = System.currentTimeMillis() - startTime;

    // When - Leer 1000 carritos
    startTime = System.currentTimeMillis();

    for (int i = 0; i < 1000; i++) {
        String sessionId = "session-" + i;
        CarritoDTO carrito = performanceCache.getIfPresent(sessionId);
        assertThat(carrito).isNotNull();
    }

    long readTime = System.currentTimeMillis() - startTime;

    // Then
    log.info("Performance Test - Write: {}ms, Read: {}ms", writeTime, readTime);
    assertThat(writeTime).isLessThan(100); // Menos de 100ms para escribir
    assertThat(readTime).isLessThan(50);   // Menos de 50ms para leer

    CacheStats stats = performanceCache.stats();
    assertThat(stats.hitRate()).isEqualTo(1.0); // 100% hit rate
}
```

## 🎯 Mejores Prácticas Implementadas

### 1. ⚡ Optimización de Rendimiento

```java
@Service
public class OptimizedCarritoService {

    private final Cache<String, CarritoDTO> carritoCache;

    // Usar bulk operations cuando sea posible
    public Map<String, CarritoDTO> obtenerMultiplesCarritos(Set<String> sessionIds) {
        return carritoCache.getAllPresent(sessionIds);
    }

    // Evitar operaciones costosas en el hilo principal
    @Async
    public CompletableFuture<Void> precalentarCache(List<String> sessionIds) {
        sessionIds.parallelStream()
                .forEach(sessionId -> {
                    if (carritoCache.getIfPresent(sessionId) == null) {
                        CarritoDTO carrito = crearCarritoVacio();
                        carritoCache.put(sessionId, carrito);
                    }
                });

        return CompletableFuture.completedFuture(null);
    }

    // Usar weak references para evitar memory leaks
    private final WeakHashMap<String, CarritoDTO> backupCache = new WeakHashMap<>();

    public CarritoDTO obtenerCarritoConFallback(String sessionId) {
        CarritoDTO carrito = carritoCache.getIfPresent(sessionId);

        if (carrito == null) {
            carrito = backupCache.get(sessionId);
            if (carrito != null) {
                carritoCache.put(sessionId, carrito);
            }
        }

        return carrito != null ? carrito : crearCarritoVacio();
    }
}
```

### 2. 🔒 Thread Safety

```java
@Service
public class ThreadSafeCarritoService {

    private final Cache<String, CarritoDTO> carritoCache;
    private final LoadingCache<String, CarritoDTO> loadingCache;

    public ThreadSafeCarritoService() {
        this.carritoCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(2, TimeUnit.HOURS)
                .build();

        this.loadingCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(2, TimeUnit.HOURS)
                .build(new CacheLoader<String, CarritoDTO>() {
                    @Override
                    public CarritoDTO load(String sessionId) {
                        return crearCarritoVacio();
                    }
                });
    }

    // Operaciones atómicas usando get() con Callable
    public CarritoDTO obtenerOCrearCarrito(String sessionId) {
        try {
            return carritoCache.get(sessionId, () -> {
                log.debug("Creando nuevo carrito para sesión: {}", sessionId);
                return crearCarritoVacio();
            });
        } catch (Exception e) {
            log.error("Error obteniendo carrito: {}", sessionId, e);
            return crearCarritoVacio();
        }
    }

    // Operaciones thread-safe usando LoadingCache
    public CarritoDTO obtenerCarritoSeguro(String sessionId) {
        try {
            return loadingCache.get(sessionId);
        } catch (Exception e) {
            log.error("Error cargando carrito: {}", sessionId, e);
            return crearCarritoVacio();
        }
    }
}
```

### 3. 📊 Monitoreo y Alertas

```java
@Service
@Slf4j
public class CacheHealthMonitor {

    private final Cache<String, CarritoDTO> carritoCache;
    private final MeterRegistry meterRegistry;

    public CacheHealthMonitor(Cache<String, CarritoDTO> carritoCache,
                             MeterRegistry meterRegistry) {
        this.carritoCache = carritoCache;
        this.meterRegistry = meterRegistry;
        initializeMetrics();
    }

    private void initializeMetrics() {
        Gauge.builder("cache.size")
                .register(meterRegistry, this, CacheHealthMonitor::getCacheSize);

        Gauge.builder("cache.hit.rate")
                .register(meterRegistry, this, CacheHealthMonitor::getHitRate);

        Gauge.builder("cache.eviction.count")
                .register(meterRegistry, this, CacheHealthMonitor::getEvictionCount);
    }

    public double getCacheSize() {
        return carritoCache.size();
    }

    public double getHitRate() {
        return carritoCache.stats().hitRate();
    }

    public double getEvictionCount() {
        return carritoCache.stats().evictionCount();
    }

    @Scheduled(fixedRate = 60000) // Cada minuto
    public void checkCacheHealth() {
        CacheStats stats = carritoCache.stats();

        // Alertar si hit rate es muy bajo
        if (stats.hitRate() < 0.7) {
            log.warn("Cache hit rate bajo: {:.2f}%", stats.hitRate() * 100);
        }

        // Alertar si hay muchas evictions
        if (stats.evictionCount() > 100) {
            log.warn("Alto número de evictions: {}", stats.evictionCount());
        }

        // Alertar si el cache está lleno
        if (carritoCache.size() > 900) { // 90% del límite
            log.warn("Cache casi lleno: {}/1000", carritoCache.size());
        }
    }
}
```

## 📈 Beneficios Alcanzados

### 1. ⚡ Rendimiento Mejorado

- **Acceso O(1)**: Tiempo constante para operaciones de cache
- **Reducción de latencia**: Eliminación de accesos a base de datos
- **Escalabilidad**: Soporte para miles de sesiones concurrentes

### 2. 💾 Gestión Eficiente de Memoria

- **Límites configurables**: Control preciso del uso de memoria
- **Expiración automática**: Limpieza automática de datos obsoletos
- **Eviction policies**: Estrategias inteligentes de limpieza

### 3. 🔧 Operaciones Thread-Safe

- **Concurrencia segura**: Operaciones atómicas sin bloqueos
- **Consistencia**: Estado consistente bajo alta concurrencia
- **Performance**: Operaciones concurrentes sin degradación

## 🔮 Roadmap Futuro

### 1. 🌐 Cache Distribuido

- **Redis integration**: Migración a cache distribuido
- **Cluster support**: Soporte para múltiples instancias
- **Persistence**: Persistencia de carritos importantes

### 2. 🧠 Cache Inteligente

- **Predictive loading**: Carga predictiva de datos
- **Machine learning**: Optimización basada en patrones de uso
- **Dynamic sizing**: Ajuste automático de límites

### 3. 📊 Analytics Avanzados

- **Detailed metrics**: Métricas granulares de uso
- **Performance insights**: Análisis de rendimiento detallado
- **Cost optimization**: Optimización de costos de memoria

## 🔧 Troubleshooting Común

### 1. 💾 Problemas de Memoria

```java
// Problema: OutOfMemoryError
// Solución: Ajustar límites del cache

Cache<String, CarritoDTO> cache = CacheBuilder.newBuilder()
    .maximumSize(500) // Reducir tamaño máximo
    .expireAfterAccess(1, TimeUnit.HOURS) // Reducir TTL
    .build();
```

### 2. 🔄 Cache Misses Frecuentes

```java
// Problema: Hit rate bajo
// Solución: Ajustar políticas de expiración

Cache<String, CarritoDTO> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(4, TimeUnit.HOURS) // Aumentar TTL
    .recordStats() // Monitorear estadísticas
    .build();
```

### 3. 🚀 Problemas de Rendimiento

```java
// Problema: Operaciones lentas
// Solución: Usar operaciones bulk

// Incorrecto: Operaciones individuales
for (String sessionId : sessionIds) {
    cache.getIfPresent(sessionId);
}

// Correcto: Operación bulk
Map<String, CarritoDTO> result = cache.getAllPresent(sessionIds);
```

---

## 📚 Recursos Adicionales

- **[Google Guava Documentation](https://github.com/google/guava/wiki)**
- **[Guava Cache Explained](https://github.com/google/guava/wiki/CachesExplained)**
- **[Best Practices for Guava Cache](https://github.com/google/guava/wiki/CachesExplained#best-practices)**
- **[Cache Performance Tuning](https://github.com/google/guava/wiki/CachesExplained#performance)**

---

_Esta documentación forma parte del proyecto "Como en Casa" - Sistema Web para Gestión de Pedidos y Clientes_
