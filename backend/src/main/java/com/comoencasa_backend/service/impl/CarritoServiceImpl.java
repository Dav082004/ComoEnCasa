package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.dao.CarritoDAO;
import com.comoencasa_backend.dto.CarritoDTO;
import com.comoencasa_backend.dto.CarritoItemDTO;
import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.service.CarritoService;
import com.comoencasa_backend.service.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
/**
 * Implementación del servicio de carrito siguiendo principios SOLID y patrón DAO
 * Maneja las operaciones de carrito con validación de stock y lógica de negocio
 */
public class CarritoServiceImpl implements CarritoService {

    private final CarritoDAO carritoDAO;
    private final ProductoService productoService;

    public CarritoServiceImpl(CarritoDAO carritoDAO, ProductoService productoService) {
        this.carritoDAO = carritoDAO;
        this.productoService = productoService;
        log.info("CarritoService inicializado con DAO y ProductoService");
    }    @Override
    public CarritoDTO agregarProducto(String sessionId, Long productoId, Integer cantidad, String comentarios) {
        // Validaciones de entrada
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID no puede ser nulo");
        }
        if (productoId == null) {
            throw new IllegalArgumentException("Producto ID no puede ser nulo");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a 0");
        }log.debug("Agregando producto al carrito: sessionId={}, productoId={}, cantidad={}, comentarios={}", 
                sessionId, productoId, cantidad, comentarios);        // Validar que el producto existe y tiene stock suficiente
        Optional<Producto> productoOpt = productoService.findById(productoId);
        if (!productoOpt.isPresent()) {
            throw new IllegalArgumentException("Producto no encontrado");
        }

        Producto producto = productoOpt.get();
        if (!producto.getDisponible()) {
            throw new IllegalArgumentException("Producto no disponible");
        }

        // Obtener carrito existente o crear uno nuevo
        Optional<CarritoDTO> carritoOpt = carritoDAO.obtenerCarrito(sessionId);
        CarritoDTO carrito = carritoOpt.orElse(crearCarritoVacio(sessionId));        // Verificar stock disponible
        // Para items existentes: validar solo la nueva cantidad (no suma)
        // Para items nuevos: validar cantidad solicitada
        if (producto.getCantidad() != null && cantidad > producto.getCantidad()) {
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                    producto.getCantidad(), cantidad));
        }// Buscar item existente en carrito o crear nuevo
        CarritoItemDTO item = buscarItemEnCarrito(carrito, productoId);
        if (item != null) {
            // ESTABLECER cantidad exacta (no sumar) - Requerimiento TDD actualizado
            item.setCantidad(cantidad);
            if (comentarios != null && !comentarios.trim().isEmpty()) {
                item.setComentarios(comentarios);
            }
            log.debug("Cantidad establecida en item existente: productoId={}, nuevaCantidad={}", 
                    productoId, item.getCantidad());
        } else {
            // Crear nuevo item
            item = CarritoItemDTO.builder()
                    .productoId(productoId)
                    .nombre(producto.getNombre())
                    .descripcion(producto.getDescripcion())
                    .precioVenta(producto.getPrecioVenta())
                    .imagenUrl(producto.getImagenUrl())
                    .cantidad(cantidad)
                    .comentarios(comentarios)
                    .build();
            
            carrito.addItem(item);
            log.debug("Nuevo item agregado al carrito: productoId={}, cantidad={}", 
                    productoId, cantidad);
        }

        // Recalcular totales y guardar
        carrito.calcularTotales();
        carritoDAO.guardarCarrito(sessionId, carrito);
        
        log.info("Producto agregado exitosamente al carrito: sessionId={}, productoId={}, cantidadTotal={}", 
                sessionId, productoId, item.getCantidad());

        return carrito;
    }    @Override
    public CarritoDTO actualizarCantidad(String sessionId, Long productoId, Integer nuevaCantidad) {
        // Validaciones de entrada
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("El sessionId no puede ser nulo o vacío");
        }
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        if (nuevaCantidad == null || nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser nula o negativa");
        }

        log.debug("Actualizando cantidad en carrito: sessionId={}, productoId={}, nuevaCantidad={}", 
                sessionId, productoId, nuevaCantidad);

        // Si la cantidad es 0, eliminar el producto
        if (nuevaCantidad == 0) {
            return eliminarProducto(sessionId, productoId);
        }

        // Obtener carrito
        Optional<CarritoDTO> carritoOpt = carritoDAO.obtenerCarrito(sessionId);
        if (!carritoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe carrito para la sesión: " + sessionId);
        }

        CarritoDTO carrito = carritoOpt.get();
        CarritoItemDTO item = buscarItemEnCarrito(carrito, productoId);
        if (item == null) {
            throw new IllegalArgumentException("Producto no encontrado en el carrito: " + productoId);
        }

        // Validar stock disponible
        Optional<Producto> productoOpt = productoService.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            if (producto.getCantidad() != null && nuevaCantidad > producto.getCantidad()) {
                throw new IllegalArgumentException(
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                        producto.getCantidad(), nuevaCantidad));
            }
        }

        // Actualizar cantidad
        item.setCantidad(nuevaCantidad);

        // Recalcular totales y guardar
        carrito.calcularTotales();
        carritoDAO.guardarCarrito(sessionId, carrito);

        log.info("Cantidad actualizada en carrito: sessionId={}, productoId={}, nuevaCantidad={}", 
                sessionId, productoId, nuevaCantidad);

        return carrito;
    }    @Override
    public CarritoDTO eliminarProducto(String sessionId, Long productoId) {
        // Validaciones de entrada
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("El sessionId no puede ser nulo o vacío");
        }
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }

        log.debug("Eliminando producto del carrito: sessionId={}, productoId={}", 
                sessionId, productoId);

        // Obtener carrito
        Optional<CarritoDTO> carritoOpt = carritoDAO.obtenerCarrito(sessionId);
        if (!carritoOpt.isPresent()) {
            throw new IllegalArgumentException("No existe carrito para la sesión: " + sessionId);
        }

        CarritoDTO carrito = carritoOpt.get();
        boolean removed = carrito.getItems().removeIf(item -> item.getProductoId().equals(productoId));

        if (!removed) {
            throw new IllegalArgumentException("Producto no encontrado en el carrito: " + productoId);
        }

        // Recalcular totales y guardar
        carrito.calcularTotales();
        carritoDAO.guardarCarrito(sessionId, carrito);

        log.info("Producto eliminado del carrito: sessionId={}, productoId={}", 
                sessionId, productoId);

        return carrito;
    }

    @Override
    public CarritoDTO obtenerCarrito(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            log.warn("SessionId nulo o vacío proporcionado para obtener carrito");
            return crearCarritoVacio(sessionId != null ? sessionId : "");
        }

        log.debug("Obteniendo carrito: sessionId={}", sessionId);
        Optional<CarritoDTO> carritoOpt = carritoDAO.obtenerCarrito(sessionId);
        return carritoOpt.orElse(crearCarritoVacio(sessionId));
    }

    @Override
    public CarritoDTO limpiarCarrito(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("El sessionId no puede ser nulo o vacío");
        }

        log.debug("Limpiando carrito: sessionId={}", sessionId);
        carritoDAO.eliminarCarrito(sessionId);
        
        CarritoDTO carritoVacio = crearCarritoVacio(sessionId);
        carritoDAO.guardarCarrito(sessionId, carritoVacio);
        
        log.info("Carrito limpiado: sessionId={}", sessionId);
        return carritoVacio;
    }    @Override
    public Integer obtenerTotalItems(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return 0;
        }

        Optional<CarritoDTO> carritoOpt = carritoDAO.obtenerCarrito(sessionId);
        if (!carritoOpt.isPresent()) {
            return 0;
        }

        int total = carritoOpt.get().getItems().stream()
                .mapToInt(CarritoItemDTO::getCantidad)
                .sum();

        log.debug("Total de items en carrito: sessionId={}, total={}", sessionId, total);
        return total;
    }

    // Métodos auxiliares privados

    /**
     * Crea un carrito vacío para una nueva sesión
     */
    private CarritoDTO crearCarritoVacio(String sessionId) {
        return new CarritoDTO(sessionId);
    }

    /**
     * Busca un item específico en el carrito por producto ID
     */
    private CarritoItemDTO buscarItemEnCarrito(CarritoDTO carrito, Long productoId) {
        if (carrito.getItems() == null) {
            return null;
        }
        return carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene la cantidad actual de un producto en el carrito
     */
    private Integer obtenerCantidadEnCarrito(CarritoDTO carrito, Long productoId) {
        CarritoItemDTO item = buscarItemEnCarrito(carrito, productoId);
        return item != null ? item.getCantidad() : 0;
    }
}