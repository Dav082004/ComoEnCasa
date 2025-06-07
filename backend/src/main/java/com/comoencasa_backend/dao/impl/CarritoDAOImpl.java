package com.comoencasa_backend.dao.impl;

import com.comoencasa_backend.dao.CarritoDAO;
import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementación del DAO de carrito usando Google Guava Cache
 * Siguiendo el patrón DAO para abstracción de datos
 */
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
        log.debug("Carrito recuperado: sessionId={}, encontrado={}", 
                sessionId, carrito != null);
        
        return Optional.ofNullable(carrito);
    }

    @Override
    public void eliminarCarrito(String sessionId) {
        if (sessionId == null) {
            return;
        }
        
        carritoCache.invalidate(sessionId);
        log.debug("Carrito eliminado: sessionId={}", sessionId);
    }

    @Override
    public boolean existeCarrito(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        
        boolean existe = carritoCache.getIfPresent(sessionId) != null;
        log.debug("Carrito existe: sessionId={}, existe={}", sessionId, existe);
        return existe;
    }

    @Override
    public void limpiarCarritosExpirados() {
        long sizeBefore = carritoCache.size();
        carritoCache.cleanUp(); // Fuerza limpieza de elementos expirados
        long sizeAfter = carritoCache.size();
        
        log.info("Limpieza de carritos - Antes: {}, Después: {}, Eliminados: {}", 
                sizeBefore, sizeAfter, (sizeBefore - sizeAfter));
    }

    @Override
    public long contarCarritosActivos() {
        long count = carritoCache.size();
        log.debug("Carritos activos en cache: {}", count);
        return count;
    }

    @Override
    public void actualizarItem(String sessionId, CarritoItemDTO item) {
        if (sessionId == null || item == null) {
            throw new IllegalArgumentException("SessionId e item no pueden ser nulos");
        }
        
        Optional<CarritoDTO> carritoOpt = obtenerCarrito(sessionId);
        if (carritoOpt.isPresent()) {
            CarritoDTO carrito = carritoOpt.get();
            
            // Buscar y actualizar el item
            carrito.getItems().removeIf(existingItem -> 
                existingItem.getProductoId().equals(item.getProductoId()));
            carrito.getItems().add(item);
            
            guardarCarrito(sessionId, carrito);
            log.debug("Item actualizado en carrito: sessionId={}, productoId={}, cantidad={}", 
                    sessionId, item.getProductoId(), item.getCantidad());
        }
    }

    @Override
    public void eliminarItem(String sessionId, Long productoId) {
        if (sessionId == null || productoId == null) {
            throw new IllegalArgumentException("SessionId y productoId no pueden ser nulos");
        }
        
        Optional<CarritoDTO> carritoOpt = obtenerCarrito(sessionId);
        if (carritoOpt.isPresent()) {
            CarritoDTO carrito = carritoOpt.get();
            
            boolean removed = carrito.getItems().removeIf(item -> 
                item.getProductoId().equals(productoId));
            
            if (removed) {
                guardarCarrito(sessionId, carrito);
                log.debug("Item eliminado del carrito: sessionId={}, productoId={}", 
                        sessionId, productoId);
            }
        }
    }
}
