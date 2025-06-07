package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.model.Producto;
import com.comoencasa_backend.repository.ProductoRepository;
import com.comoencasa_backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

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
}