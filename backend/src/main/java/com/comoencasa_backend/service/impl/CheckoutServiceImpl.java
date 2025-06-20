package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.dto.*;
import com.comoencasa_backend.model.*;
import com.comoencasa_backend.repository.*;
import com.comoencasa_backend.service.CheckoutService;
import com.comoencasa_backend.service.ComprobanteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {
     private final PedidoRepository pedidoRepository;
     private final UsuarioRepository usuarioRepository;
     private final ProductoRepository productoRepository;
     private final DetallePedidoRepository detallePedidoRepository;
     private final PagoRepository pagoRepository;
     private final ComprobanteService comprobanteService;
     private final ComprobanteRepository comprobanteRepository;

     public CheckoutServiceImpl(
               PedidoRepository pedidoRepository,
               UsuarioRepository usuarioRepository,
               ProductoRepository productoRepository,
               DetallePedidoRepository detallePedidoRepository,
               PagoRepository pagoRepository,
               ComprobanteService comprobanteService,
               ComprobanteRepository comprobanteRepository) {
          this.pedidoRepository = pedidoRepository;
          this.usuarioRepository = usuarioRepository;
          this.productoRepository = productoRepository;
          this.detallePedidoRepository = detallePedidoRepository;
          this.pagoRepository = pagoRepository;
          this.comprobanteService = comprobanteService;
          this.comprobanteRepository = comprobanteRepository;
     }

     @Override
     @Transactional
     public CheckoutResponseDTO procesarCheckout(CheckoutDTO checkoutDTO) {
          log.info("Iniciando proceso de checkout para usuario ID: {}", checkoutDTO.getUsuarioId());

          try {
               // 1. Validaciones
               validarCheckoutDTO(checkoutDTO);

               // 2. Crear el pedido
               Pedido pedido = crearPedido(checkoutDTO);

               // 3. Crear detalles del pedido
               crearDetallesPedido(pedido, checkoutDTO.getItems());

               // 4. Procesar pago
               Pago pago = procesarPagoCheckout(pedido, checkoutDTO);

               // 5. Generar comprobante solo si el pago fue exitoso
               Comprobante comprobante = null;
               if (pago.getEstado() == Pago.EstadoPago.PAGADO) {
                    comprobante = generarComprobante(pedido, checkoutDTO);
                    // Actualizar estado del pedido a "En preparación"
                    pedido.setEstado("En preparación");
                    pedidoRepository.save(pedido);
               }

               // 6. Construir respuesta
               CheckoutResponseDTO response = construirRespuesta(pedido, pago, comprobante);

               log.info("Checkout procesado exitosamente. Pedido ID: {}, Pago ID: {}, Comprobante ID: {}",
                         pedido.getId(), pago.getId(), comprobante != null ? comprobante.getId() : null);

               return response;

          } catch (Exception e) {
               log.error("Error procesando checkout: {}", e.getMessage(), e);
               return construirRespuestaError(e.getMessage());
          }
     }

     @Override
     public boolean procesarPago(String metodoPago, BigDecimal monto) {
          // Simulación de procesamiento de pago
          log.info("Procesando pago: método={}, monto={}", metodoPago, monto);

          // Simular diferentes comportamientos según el método
          switch (metodoPago.toLowerCase()) {
               case "yape":
               case "plin":
                    // Para Yape y Plin, simular éxito en 95% de casos
                    return Math.random() < 0.95;
               case "tarjeta":
                    // Para tarjeta, simular éxito en 90% de casos
                    return Math.random() < 0.90;
               case "efectivo":
                    // Efectivo siempre exitoso (se pagará en entrega)
                    return true;
               default:
                    return false;
          }
     }

     private void validarCheckoutDTO(CheckoutDTO checkoutDTO) {
          if (checkoutDTO.getUsuarioId() == null) {
               throw new IllegalArgumentException("El ID de usuario es requerido");
          }
          if (checkoutDTO.getItems() == null || checkoutDTO.getItems().isEmpty()) {
               throw new IllegalArgumentException("El carrito no puede estar vacío");
          }
          if (checkoutDTO.getDireccionEntrega() == null || checkoutDTO.getDireccionEntrega().trim().isEmpty()) {
               throw new IllegalArgumentException("La dirección de entrega es requerida");
          }
          if (checkoutDTO.getMetodoPago() == null || checkoutDTO.getMetodoPago().trim().isEmpty()) {
               throw new IllegalArgumentException("El método de pago es requerido");
          }
          if (checkoutDTO.getTipoComprobante() == null || checkoutDTO.getTipoComprobante().trim().isEmpty()) {
               throw new IllegalArgumentException("El tipo de comprobante es requerido");
          }
          if (checkoutDTO.getDocumento() == null || checkoutDTO.getDocumento().trim().isEmpty()) {
               throw new IllegalArgumentException("El documento (DNI/RUC) es requerido");
          }

          // Validar usuario existe
          Optional<Usuario> usuario = usuarioRepository.findById(checkoutDTO.getUsuarioId());
          if (usuario.isEmpty()) {
               throw new IllegalArgumentException("Usuario no encontrado");
          }
     }

     private Pedido crearPedido(CheckoutDTO checkoutDTO) {
          Usuario usuario = usuarioRepository.findById(checkoutDTO.getUsuarioId()).get();

          Pedido pedido = new Pedido();
          pedido.setUsuario(usuario);
          pedido.setFechaCreacion(LocalDateTime.now());
          pedido.setFechaEntrega(checkoutDTO.getFechaEntrega() != null ? checkoutDTO.getFechaEntrega()
                    : LocalDateTime.now().plusDays(3));
          pedido.setEstado("Pendiente");
          pedido.setSubtotal(checkoutDTO.getSubtotal());
          pedido.setCostoTotal(checkoutDTO.getTotal());

          // Construir dirección completa
          String direccionCompleta = checkoutDTO.getDireccionEntrega();
          if (checkoutDTO.getDistrito() != null && !checkoutDTO.getDistrito().isEmpty()) {
               direccionCompleta += ", " + checkoutDTO.getDistrito();
          }
          if (checkoutDTO.getReferencia() != null && !checkoutDTO.getReferencia().isEmpty()) {
               direccionCompleta += " (" + checkoutDTO.getReferencia() + ")";
          }
          pedido.setDireccionEntrega(direccionCompleta);
          pedido.setNecesitaFactura("factura".equals(checkoutDTO.getTipoComprobante()));

          return pedidoRepository.save(pedido);
     }

     private void crearDetallesPedido(Pedido pedido, List<CheckoutDTO.CheckoutItemDTO> items) {
          List<DetallePedido> detalles = new ArrayList<>();

          for (CheckoutDTO.CheckoutItemDTO item : items) {
               Optional<Producto> productoOpt = productoRepository.findById(item.getProductoId());
               if (productoOpt.isEmpty()) {
                    throw new IllegalArgumentException("Producto no encontrado: " + item.getProductoId());
               }

               Producto producto = productoOpt.get();

               DetallePedido detalle = new DetallePedido();
               detalle.setPedido(pedido);
               detalle.setProducto(producto);
               detalle.setCantidad(item.getCantidad());
               detalle.setPrecioUnitario(item.getPrecioUnitario());
               detalle.setCostoUnitario(BigDecimal.valueOf(producto.getCostoProduccion()));
               detalle.setPersonalizacion(item.getPersonalizacion());

               detalles.add(detalle);
          }

          detallePedidoRepository.saveAll(detalles);
          log.info("Creados {} detalles de pedido para el pedido ID: {}", detalles.size(), pedido.getId());
     }

     private Pago procesarPagoCheckout(Pedido pedido, CheckoutDTO checkoutDTO) {
          Pago pago = new Pago();
          pago.setPedido(pedido);
          pago.setFecha(LocalDateTime.now());
          pago.setMonto(checkoutDTO.getTotal());

          // Convertir string a enum
          Pago.MetodoPago metodoPago;
          switch (checkoutDTO.getMetodoPago().toLowerCase()) {
               case "yape":
                    metodoPago = Pago.MetodoPago.YAPE;
                    break;
               case "plin":
                    metodoPago = Pago.MetodoPago.PLIN;
                    break;
               case "tarjeta":
                    metodoPago = Pago.MetodoPago.TARJETA;
                    break;
               case "efectivo":
                    metodoPago = Pago.MetodoPago.EFECTIVO;
                    break;
               default:
                    throw new IllegalArgumentException("Método de pago no válido: " + checkoutDTO.getMetodoPago());
          }

          pago.setMetodo(metodoPago);

          // Procesar pago
          boolean pagoExitoso = procesarPago(checkoutDTO.getMetodoPago(), checkoutDTO.getTotal());
          pago.setEstado(pagoExitoso ? Pago.EstadoPago.PAGADO : Pago.EstadoPago.RECHAZADO);

          return pagoRepository.save(pago);
     }

     private Comprobante generarComprobante(Pedido pedido, CheckoutDTO checkoutDTO) {
          TipoComprobante tipoComprobante = "factura".equals(checkoutDTO.getTipoComprobante()) ? TipoComprobante.Factura
                    : TipoComprobante.Boleta;

          ComprobanteDTO comprobanteDTO = comprobanteService.generarComprobante(pedido.getId(), tipoComprobante);

          // Necesitamos obtener la entidad Comprobante desde la BD
          return comprobanteRepository.findById(comprobanteDTO.getId()).orElse(null);
     }

     private CheckoutResponseDTO construirRespuesta(Pedido pedido, Pago pago, Comprobante comprobante) {
          CheckoutResponseDTO response = new CheckoutResponseDTO();

          // Datos del pedido
          response.setPedidoId(pedido.getId());
          response.setNumeroPedido(String.format("PED%06d", pedido.getId()));
          response.setEstado(pedido.getEstado());
          response.setTotal(pedido.getCostoTotal());
          response.setFechaCreacion(pedido.getFechaCreacion());
          response.setFechaEntrega(pedido.getFechaEntrega());

          // Datos del pago
          response.setPagoId(pago.getId());
          response.setMetodoPago(pago.getMetodo().getDisplayName());
          response.setEstadoPago(pago.getEstado().getDisplayName());

          // Datos del comprobante
          if (comprobante != null) {
               response.setComprobanteId(comprobante.getId());
               response.setTipoComprobante(comprobante.getTipo().name());
               response.setNumeroSerie(comprobante.getNumeroSerie());
               response.setNumeroComprobante(comprobante.getNumeroComprobante());
               response.setUrlComprobante("/api/comprobantes/" + comprobante.getId() + "/pdf");
          }

          // Estado de la respuesta
          response.setExitoso(pago.getEstado() == Pago.EstadoPago.PAGADO);
          response.setMensaje(pago.getEstado() == Pago.EstadoPago.PAGADO ? "¡Pedido procesado exitosamente!"
                    : "Error en el procesamiento del pago");

          return response;
     }

     private CheckoutResponseDTO construirRespuestaError(String mensaje) {
          CheckoutResponseDTO response = new CheckoutResponseDTO();
          response.setExitoso(false);
          response.setMensaje("Error: " + mensaje);
          return response;
     }
}
