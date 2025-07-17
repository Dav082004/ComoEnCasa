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

          int maxRetries = 3;
          int retryCount = 0;

          while (retryCount < maxRetries) {
               try {
                    return procesarCheckoutInterno(checkoutDTO);
               } catch (org.springframework.dao.DataAccessResourceFailureException e) {
                    retryCount++;
                    log.warn("Error de conexión en checkout (intento {}/{}): {}", retryCount, maxRetries,
                              e.getMessage());

                    if (retryCount >= maxRetries) {
                         log.error("Error de conexión persistente después de {} intentos", maxRetries);
                         throw new RuntimeException(
                                   "Error de conexión con la base de datos. Por favor, intente nuevamente en unos momentos.");
                    }

                    // Esperar un poco antes del siguiente intento
                    try {
                         Thread.sleep(1000 * retryCount); // Backoff exponencial
                    } catch (InterruptedException ie) {
                         Thread.currentThread().interrupt();
                         throw new RuntimeException("Proceso interrumpido");
                    }
               } catch (Exception e) {
                    log.error("Error procesando checkout: {}", e.getMessage(), e);
                    return construirRespuestaError("Error procesando el pedido: " + e.getMessage());
               }
          }

          throw new RuntimeException("Error procesando el pedido después de múltiples intentos");
     }

     private CheckoutResponseDTO procesarCheckoutInterno(CheckoutDTO checkoutDTO) {
          log.info("Iniciando proceso de checkout para usuario ID: {}", checkoutDTO.getUsuarioId());
          try {
               // 1. Validaciones
               validarCheckoutDTO(checkoutDTO);

               // 2. Validar y reservar stock
               validarYReservarStock(checkoutDTO.getItems());

               // 3. Crear el pedido
               Pedido pedido = crearPedido(checkoutDTO);

               // 4. Crear detalles del pedido
               crearDetallesPedido(pedido, checkoutDTO.getItems());

               // 5. Procesar pago
               Pago pago = procesarPagoCheckout(pedido, checkoutDTO);

               // 6. Generar comprobante solo si el pago fue exitoso
               Comprobante comprobante = null;
               if (pago.getEstado() == Pago.EstadoPago.Pagado) {
                    // 6.1. Confirmar reducción de stock definitivamente
                    confirmarReduccionStock(checkoutDTO.getItems());

                    // 6.2. Generar comprobante
                    comprobante = generarComprobante(pedido, checkoutDTO);

                    // 6.3. El pedido permanece en "Pendiente" para verificación manual del pago
                    // No cambiar automáticamente a "En preparación"
                    log.info("Pedido {} creado en estado 'Pendiente' - Requiere verificación manual del pago",
                              pedido.getId());
               } else {
                    // Si el pago falló, revertir la reserva de stock
                    revertirReservaStock(checkoutDTO.getItems());
               }

               // 7. Construir respuesta
               CheckoutResponseDTO response = construirRespuesta(pedido, pago, comprobante);

               log.info("Checkout procesado exitosamente. Pedido ID: {}, Pago ID: {}, Comprobante ID: {}",
                         pedido.getId(), pago.getId(), comprobante != null ? comprobante.getId() : null);

               return response;

          } catch (Exception e) {
               log.error("Error procesando checkout: {}", e.getMessage(), e);
               throw new RuntimeException("Error procesando checkout: " + e.getMessage(), e);
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
          log.info("Validando checkoutDTO: usuarioId={}, items={}, direccionEntrega={}, metodoPago={}, tipoComprobante={}, documento={}",
                    checkoutDTO.getUsuarioId(), checkoutDTO.getItems() != null ? checkoutDTO.getItems().size() : 0,
                    checkoutDTO.getDireccionEntrega(), checkoutDTO.getMetodoPago(), checkoutDTO.getTipoComprobante(),
                    checkoutDTO.getDocumento());

          // Logs adicionales para PayPal
          if ("paypal".equalsIgnoreCase(checkoutDTO.getMetodoPago())) {
               log.info("Validando datos de PayPal: paypalId={}, paypalEmail={}, payerId={}",
                         checkoutDTO.getPaypalId(), checkoutDTO.getPaypalEmail(), checkoutDTO.getPayerId());
          }

          if (checkoutDTO.getUsuarioId() == null) {
               log.error("Validación fallida: usuarioId es null");
               throw new IllegalArgumentException("El ID de usuario es requerido");
          }
          if (checkoutDTO.getItems() == null || checkoutDTO.getItems().isEmpty()) {
               log.error("Validación fallida: items vacío o null");
               throw new IllegalArgumentException("El carrito no puede estar vacío");
          }
          if (checkoutDTO.getDireccionEntrega() == null || checkoutDTO.getDireccionEntrega().trim().isEmpty()) {
               log.error("Validación fallida: direccionEntrega es null o vacía");
               throw new IllegalArgumentException("La dirección de entrega es requerida");
          }
          if (checkoutDTO.getMetodoPago() == null || checkoutDTO.getMetodoPago().trim().isEmpty()) {
               log.error("Validación fallida: metodoPago es null o vacío");
               throw new IllegalArgumentException("El método de pago es requerido");
          }
          if (checkoutDTO.getTipoComprobante() == null || checkoutDTO.getTipoComprobante().trim().isEmpty()) {
               log.error("Validación fallida: tipoComprobante es null o vacío");
               throw new IllegalArgumentException("El tipo de comprobante es requerido");
          }
          if (checkoutDTO.getDocumento() == null || checkoutDTO.getDocumento().trim().isEmpty()) {
               log.error("Validación fallida: documento es null o vacío");
               throw new IllegalArgumentException("El documento (DNI/RUC) es requerido");
          }

          // Validaciones específicas para PayPal
          if ("paypal".equalsIgnoreCase(checkoutDTO.getMetodoPago())) {
               if (checkoutDTO.getPaypalId() == null || checkoutDTO.getPaypalId().trim().isEmpty()) {
                    log.error("Validación fallida para PayPal: paypalId es null o vacío");
                    throw new IllegalArgumentException("El ID de transacción de PayPal es requerido");
               }
               if (checkoutDTO.getPaypalEmail() == null || checkoutDTO.getPaypalEmail().trim().isEmpty()) {
                    log.error("Validación fallida para PayPal: paypalEmail es null o vacío");
                    throw new IllegalArgumentException("El email de PayPal es requerido");
               }
               if (checkoutDTO.getPayerId() == null || checkoutDTO.getPayerId().trim().isEmpty()) {
                    log.error("Validación fallida para PayPal: payerId es null o vacío");
                    throw new IllegalArgumentException("El ID del pagador de PayPal es requerido");
               }
          }

          Optional<Usuario> usuario = usuarioRepository.findById(checkoutDTO.getUsuarioId());
          if (usuario.isEmpty()) {
               log.error("Validación fallida: usuario no encontrado para ID {}", checkoutDTO.getUsuarioId());
               throw new IllegalArgumentException("Usuario no encontrado");
          }
     }

     private Pedido crearPedido(CheckoutDTO checkoutDTO) {
          Usuario usuario = usuarioRepository.findById(checkoutDTO.getUsuarioId()).get();

          // Actualizar documento del usuario si es necesario
          actualizarDocumentoUsuario(usuario, checkoutDTO);

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
                    metodoPago = Pago.MetodoPago.Yape;
                    break;
               case "plin":
                    metodoPago = Pago.MetodoPago.Plin;
                    break;
               case "tarjeta":
                    metodoPago = Pago.MetodoPago.Tarjeta;
                    break;
               case "efectivo":
                    metodoPago = Pago.MetodoPago.Efectivo;
                    break;
               case "paypal":
                    metodoPago = Pago.MetodoPago.PayPal;
                    break;

               default:
                    throw new IllegalArgumentException("Método de pago no válido: " + checkoutDTO.getMetodoPago());
          }

          pago.setMetodo(metodoPago);

          // Para PayPal, si tenemos los datos de la transacción, consideramos el pago
          // como exitoso
          if (metodoPago == Pago.MetodoPago.PayPal) {
               pago.setPaypalEmail(checkoutDTO.getPaypalEmail());
               pago.setPaypalId(checkoutDTO.getPaypalId());
               pago.setPayerId(checkoutDTO.getPayerId());

               // Si tenemos paypalId, significa que PayPal ya aprobó la transacción
               if (checkoutDTO.getPaypalId() != null && !checkoutDTO.getPaypalId().isEmpty()) {
                    pago.setEstado(Pago.EstadoPago.Pagado);
                    log.info("Pago de PayPal marcado como exitoso. PayPal ID: {}, PayPal Email: {}",
                              checkoutDTO.getPaypalId(), checkoutDTO.getPaypalEmail());
               } else {
                    pago.setEstado(Pago.EstadoPago.Rechazado);
                    log.warn("Pago de PayPal rechazado: faltan datos de la transacción");
               }
          } else {
               // Para otros métodos de pago, usar la simulación existente
               boolean pagoExitoso = procesarPago(checkoutDTO.getMetodoPago(), checkoutDTO.getTotal());
               pago.setEstado(pagoExitoso ? Pago.EstadoPago.Pagado : Pago.EstadoPago.Rechazado);
               log.info("Pago con {} procesado. Estado: {}", metodoPago.getDisplayName(),
                         pagoExitoso ? "Exitoso" : "Rechazado");
          }

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
          response.setExitoso(pago.getEstado() == Pago.EstadoPago.Pagado);
          response.setMensaje(pago.getEstado() == Pago.EstadoPago.Pagado ? "¡Pedido procesado exitosamente!"
                    : "Error en el procesamiento del pago");

          return response;
     }

     private CheckoutResponseDTO construirRespuestaError(String mensaje) {
          CheckoutResponseDTO response = new CheckoutResponseDTO();
          response.setExitoso(false);
          response.setMensaje("Error: " + mensaje);
          return response;
     }

     /**
      * Valida que haya suficiente stock y hace una reserva temporal
      */
     private void validarYReservarStock(List<CheckoutDTO.CheckoutItemDTO> items) {
          log.info("Validando stock para {} productos", items.size());

          for (CheckoutDTO.CheckoutItemDTO item : items) {
               Optional<Producto> productoOpt = productoRepository.findById(item.getProductoId());
               if (productoOpt.isEmpty()) {
                    throw new IllegalArgumentException("Producto no encontrado: " + item.getProductoId());
               }

               Producto producto = productoOpt.get();

               // Verificar disponibilidad del producto
               if (!producto.getDisponible()) {
                    throw new IllegalArgumentException("El producto '" + producto.getNombre() + "' no está disponible");
               }

               // Verificar stock suficiente
               if (producto.getCantidad() < item.getCantidad()) {
                    throw new IllegalArgumentException(
                              String.format("Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                                        producto.getNombre(), producto.getCantidad(), item.getCantidad()));
               }

               log.info("Stock validado para {}: {} disponible, {} solicitado",
                         producto.getNombre(), producto.getCantidad(), item.getCantidad());
          }
     }

     /**
      * Confirma la reducción definitiva del stock después de un pago exitoso
      */
     private void confirmarReduccionStock(List<CheckoutDTO.CheckoutItemDTO> items) {
          log.info("Confirmando reducción de stock para {} productos", items.size());

          for (CheckoutDTO.CheckoutItemDTO item : items) {
               Optional<Producto> productoOpt = productoRepository.findById(item.getProductoId());
               if (productoOpt.isPresent()) {
                    Producto producto = productoOpt.get();
                    int nuevaCantidad = producto.getCantidad() - item.getCantidad();

                    // Asegurar que no quede en negativo (doble verificación)
                    if (nuevaCantidad < 0) {
                         log.warn("Intento de reducir stock por debajo de 0 para producto {}: {} - {} = {}",
                                   producto.getId(), producto.getCantidad(), item.getCantidad(), nuevaCantidad);
                         nuevaCantidad = 0;
                    }
                    producto.setCantidad(nuevaCantidad);

                    // NO marcar como no disponible automáticamente cuando se agota el stock
                    // La disponibilidad debe ser controlada manualmente por los administradores
                    if (nuevaCantidad == 0) {
                         log.info("Producto '{}' agotado (stock = 0), pero mantiene disponibilidad para reabastecimiento",
                                   producto.getNombre());
                    }

                    productoRepository.save(producto);

                    log.info("Stock actualizado para '{}': {} -> {} unidades",
                              producto.getNombre(), producto.getCantidad() + item.getCantidad(), nuevaCantidad);
               }
          }
     }

     /**
      * Revierte la reserva de stock en caso de fallo en el pago
      * (En esta implementación simple, no necesitamos hacer nada ya que no
      * reservamos físicamente)
      */
     private void revertirReservaStock(List<CheckoutDTO.CheckoutItemDTO> items) {
          log.info("Revirtiendo reserva de stock para {} productos", items.size());
          // En una implementación más avanzada, aquí revertiríamos cualquier reserva
          // temporal
          // Por ahora, como solo validamos sin reservar, no hay nada que revertir
     }

     /**
      * Actualiza el documento del usuario si ha cambiado o no está configurado
      */
     private void actualizarDocumentoUsuario(Usuario usuario, CheckoutDTO checkoutDTO) {
          String nuevoDocumento = checkoutDTO.getDocumento();
          String tipoComprobante = checkoutDTO.getTipoComprobante();

          // Determinar el tipo de documento según el comprobante
          Usuario.TipoDocumento nuevoTipoDocumento;
          if ("factura".equals(tipoComprobante)) {
               nuevoTipoDocumento = Usuario.TipoDocumento.RUC;
          } else {
               nuevoTipoDocumento = Usuario.TipoDocumento.DNI;
          }

          // Verificar si necesitamos actualizar
          boolean necesitaActualizar = false;

          // Si el usuario no tiene documento configurado
          if (usuario.getNumeroDocumento() == null || usuario.getNumeroDocumento().trim().isEmpty()) {
               necesitaActualizar = true;
               log.info("Usuario {} no tiene documento configurado, actualizando con: {} ({})",
                         usuario.getId(), nuevoDocumento, nuevoTipoDocumento);
          }
          // Si el documento cambió
          else if (!nuevoDocumento.equals(usuario.getNumeroDocumento())) {
               necesitaActualizar = true;
               log.info("Actualizando documento de usuario {} de {} a {} ({})",
                         usuario.getId(), usuario.getNumeroDocumento(), nuevoDocumento, nuevoTipoDocumento);
          }
          // Si el tipo de documento cambió
          else if (!nuevoTipoDocumento.equals(usuario.getTipoDocumento())) {
               necesitaActualizar = true;
               log.info("Actualizando tipo de documento de usuario {} de {} a {}",
                         usuario.getId(), usuario.getTipoDocumento(), nuevoTipoDocumento);
          }

          if (necesitaActualizar) {
               usuario.setNumeroDocumento(nuevoDocumento);
               usuario.setTipoDocumento(nuevoTipoDocumento);
               usuarioRepository.save(usuario);
               log.info("Documento actualizado exitosamente para usuario {}: {} ({})",
                         usuario.getId(), nuevoDocumento, nuevoTipoDocumento);
          }
     }
}
