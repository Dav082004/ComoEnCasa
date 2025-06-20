package com.comoencasa_backend.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import com.comoencasa_backend.repository.CategoriaRepository;
import com.comoencasa_backend.service.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Producto> findAllAvailable() {
        return productoRepository.findByDisponibleTrue();
    }

    @Override
    public List<Producto> findAll() {
        // Para el panel de administración - devuelve todos los productos
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        return productoRepository.findById(id);
    }

    @Override
    public List<Producto> findByCategoriaId(Long categoriaId) {
        if (categoriaId == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo");
        }
        return productoRepository.findByCategoriaIdAndDisponibleTrue(categoriaId);
    }

    @Override
    public Producto actualizarStock(Long productoId, Integer nuevaCantidad) {
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

            // Si se agrega stock, asegurar que esté disponible
            if (nuevaCantidad > 0) {
                producto.setDisponible(true);
            } else {
                // Si stock es 0, marcar como no disponible
                producto.setDisponible(false);
            }

            return productoRepository.save(producto);
        } else {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
        }
    }

    @Override
    public Producto cambiarDisponibilidad(Long productoId, Boolean disponible) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        if (disponible == null) {
            throw new IllegalArgumentException("El estado de disponibilidad no puede ser nulo");
        }

        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setDisponible(disponible);
            return productoRepository.save(producto);
        } else {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
        }
    }

    @Override
    public void reducirStock(Long productoId, Integer cantidadVendida) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        if (cantidadVendida == null || cantidadVendida <= 0) {
            throw new IllegalArgumentException("La cantidad vendida debe ser mayor a 0");
        }

        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            int stockActual = producto.getCantidad() != null ? producto.getCantidad() : 0;

            if (stockActual < cantidadVendida) {
                throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stockActual +
                        ", Solicitado: " + cantidadVendida);
            }

            int nuevoStock = stockActual - cantidadVendida;
            producto.setCantidad(nuevoStock);

            // Si el stock llega a 0, marcar como no disponible
            if (nuevoStock == 0) {
                producto.setDisponible(false);
            }

            productoRepository.save(producto);
        } else {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
        }
    }

    @Override
    public Producto create(Producto producto) {
        // Validar que la categoría existe
        if (producto.getCategoriaId() != null && !categoriaRepository.existsById(producto.getCategoriaId())) {
            throw new IllegalArgumentException("No existe la categoría con ID: " + producto.getCategoriaId());
        }

        sanitize(producto);
        Producto saved = productoRepository.save(producto);
        log.info("CREAR producto: {}", saved);
        return saved;
    }

    @Override
    public Producto update(Long id, Producto data) {
        Producto existing = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe producto " + id));

        // Validar que la categoría existe
        if (data.getCategoriaId() != null && !categoriaRepository.existsById(data.getCategoriaId())) {
            throw new IllegalArgumentException("No existe la categoría con ID: " + data.getCategoriaId());
        }

        // sanitiza datos entrantes:
        sanitize(data);
        // actualiza campos:
        existing.setCategoriaId(data.getCategoriaId());
        existing.setNombre(data.getNombre());
        existing.setDescripcion(data.getDescripcion());
        existing.setPrecioVenta(data.getPrecioVenta());
        existing.setCostoProduccion(data.getCostoProduccion());
        existing.setImagenUrl(data.getImagenUrl());
        existing.setCantidad(data.getCantidad());
        existing.setDisponible(data.getCantidad() > 0);
        Producto updated = productoRepository.save(existing);
        log.info("EDITAR producto (ID={}): {}", id, updated);
        return updated;
    }

    @Override
    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe producto " + id);
        }
        productoRepository.deleteById(id);
        log.info("ELIMINAR producto ID={}", id);
    }

    /** Método auxiliar para limpiar y escapar cadenas */
    private void sanitize(Producto p) {
        p.setNombre(StringEscapeUtils.escapeHtml4(StringUtils.trimToEmpty(p.getNombre())));
        p.setDescripcion(StringEscapeUtils.escapeHtml4(StringUtils.trimToEmpty(p.getDescripcion())));
        p.setImagenUrl(StringEscapeUtils.escapeHtml4(StringUtils.trimToEmpty(p.getImagenUrl())));
    }
}