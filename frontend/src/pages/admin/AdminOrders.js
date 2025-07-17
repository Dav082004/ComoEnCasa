import React, { useEffect, useState } from "react";
import "./styles/AdminOrders.css";
import {
  getAllPedidos,
  actualizarEstadoPedido,
  getPedidoById,
  eliminarPedido,
} from "../../services/pedidoService";

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [filteredOrders, setFilteredOrders] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [orderDetails, setOrderDetails] = useState(null);
  const [loadingDetails, setLoadingDetails] = useState(false);
  const [loading, setLoading] = useState(true);
  const [updatingStates, setUpdatingStates] = useState(new Set());

  // Estados disponibles para los pedidos (coinciden con la BD)
  const estadosDisponibles = [
    "Pendiente",
    "En preparación",
    "Entregado",
    "Cancelado",
  ];

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getAllPedidos();
      setOrders(data);
      setFilteredOrders(data);
    } catch (error) {
      console.error("Error al cargar los pedidos:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    const filtered = orders.filter(
      (order) =>
        order.id?.toString().includes(searchTerm) ||
        order.usuarioNombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        order.estado?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredOrders(filtered);
  };

  // Función removida - no se usa

  // Función para obtener el icono correspondiente a cada estado
  const getStateIcon = (estado) => {
    // Sin emojis - solo texto limpio
    return "";
  };

  // Función para obtener el icono correspondiente a cada método de pago (sin emojis)
  const getPaymentIcon = (metodoPago) => {
    return "";
  };

  // Función para obtener transiciones disponibles según el estado actual
  const getAvailableTransitions = (estadoActual) => {
    const transiciones = {
      Pendiente: ["En preparación", "Cancelado"],
      "En preparación": ["Entregado", "Cancelado", "Pendiente"],
      Entregado: ["Pendiente", "En preparación", "Cancelado"],
      Cancelado: ["Pendiente", "En preparación", "Entregado"],
    };

    return transiciones[estadoActual] || [];
  };

  // Función para manejar toggle rápido al siguiente estado lógico
  const handleToggleEstado = async (pedidoId, estadoActual) => {
    const transicionesDisponibles = getAvailableTransitions(estadoActual);

    if (transicionesDisponibles.length === 0) {
      // No debería pasar con la nueva lógica, pero por seguridad
      return;
    }

    // Definir flujo de estados más común/lógico
    const flujoLogico = {
      Pendiente: "En preparación",
      "En preparación": "Entregado",
      // Estados finales no tienen flujo rápido automático
      Entregado: null,
      Cancelado: null,
    };

    const siguienteEstadoLogico = flujoLogico[estadoActual];

    if (
      siguienteEstadoLogico &&
      transicionesDisponibles.includes(siguienteEstadoLogico)
    ) {
      // Usar el flujo lógico si está disponible
      await handleEstadoChange(pedidoId, siguienteEstadoLogico);
    } else {
      // Si no hay flujo lógico, no hacer nada automático
      // El usuario debe usar el hover para ver opciones
      console.log(
        `Estado ${estadoActual}: hover para ver opciones disponibles`
      );
    }
  };

  const handleEstadoChange = async (pedidoId, nuevoEstado) => {
    if (updatingStates.has(pedidoId)) {
      return; // Evitar múltiples actualizaciones simultáneas
    }

    // Encontrar el pedido actual para obtener su estado
    const pedidoActual = orders.find((order) => order.id === pedidoId);
    if (!pedidoActual) {
      console.error("Pedido no encontrado:", pedidoId);
      return;
    }

    const estadoActual = pedidoActual.estado || "Pendiente";

    // Si no hay cambio, no hacer nada
    if (estadoActual === nuevoEstado) {
      return;
    }

    // Realizar cambio directo sin confirmación
    await realizarCambioEstado(pedidoId, nuevoEstado);
  };

  const realizarCambioEstado = async (pedidoId, nuevoEstado) => {
    if (updatingStates.has(pedidoId)) {
      return; // Evitar múltiples actualizaciones simultáneas
    }

    try {
      setUpdatingStates((prev) => new Set(prev).add(pedidoId));

      // Ejecutar la actualización
      await actualizarEstadoPedido(pedidoId, nuevoEstado);

      // Actualizar el estado local
      const updatedOrders = orders.map((order) => {
        if (order.id === pedidoId) {
          const updatedOrder = { ...order, estado: nuevoEstado };

          // Si el nuevo estado es "Entregado", establecer la fecha de entrega actual
          if (nuevoEstado === "Entregado") {
            updatedOrder.fechaEntrega = new Date().toISOString();
          } else {
            // Si no es "Entregado", limpiar la fecha de entrega
            updatedOrder.fechaEntrega = null;
          }

          return updatedOrder;
        }
        return order;
      });

      setOrders(updatedOrders);
      setFilteredOrders(updatedOrders);

      console.log(
        `Estado del pedido ${pedidoId} actualizado a: ${nuevoEstado}`
      );

      // Mostrar notificación de éxito
      showStateChangeSuccess(pedidoId, nuevoEstado);
    } catch (error) {
      console.error("Error al actualizar estado del pedido:", error);
      alert(
        "Error al actualizar el estado del pedido: " +
          (error.response?.data?.message || error.message)
      );
    } finally {
      setUpdatingStates((prev) => {
        const newSet = new Set(prev);
        newSet.delete(pedidoId);
        return newSet;
      });
    }
  };

  const handleDelete = async () => {
    console.log("handleDelete ejecutado. Selected order:", selectedOrder);

    if (!selectedOrder) {
      console.log("No hay pedido seleccionado");
      setShowDeleteModal(false);
      return;
    }

    try {
      console.log("Intentando eliminar pedido ID:", selectedOrder.id);
      await eliminarPedido(selectedOrder.id);

      // Actualizar la lista de pedidos después de eliminar
      const updatedOrders = orders.filter(
        (order) => order.id !== selectedOrder.id
      );
      setOrders(updatedOrders);
      setFilteredOrders(updatedOrders);

      console.log("Pedido eliminado exitosamente");
      alert(`Pedido #${selectedOrder.id} eliminado exitosamente`);
      setShowDeleteModal(false);
      setSelectedOrder(null);
    } catch (error) {
      console.error("Error al eliminar pedido:", error);
      let errorMessage = "Error al eliminar el pedido";

      if (error.response?.data?.error) {
        errorMessage = error.response.data.error;
      } else if (error.response?.status === 400) {
        errorMessage =
          "No se puede eliminar este pedido. Solo se pueden eliminar pedidos en estado 'Pendiente' o 'Cancelado'.";
      }

      alert(errorMessage);
      setShowDeleteModal(false);
    }
  };

  const fetchOrderDetails = async (pedidoId) => {
    try {
      setLoadingDetails(true);
      const details = await getPedidoById(pedidoId);
      setOrderDetails(details);
      setShowDetailsModal(true);
    } catch (error) {
      console.error("Error al cargar detalles del pedido:", error);
      alert("Error al cargar los detalles del pedido");
    } finally {
      setLoadingDetails(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString("es-ES");
  };

  const formatDeliveryDate = (dateString, estado) => {
    if (estado !== "Entregado" || !dateString) {
      return "Pendiente";
    }
    return new Date(dateString).toLocaleDateString("es-ES");
  };

  const formatCurrency = (amount) => {
    if (!amount) return "S/ 0.00";
    return `S/ ${Number(amount).toFixed(2)}`;
  };

  // Función para mostrar feedback visual cuando se cambia estado
  const showStateChangeSuccess = (pedidoId, nuevoEstado) => {
    // Crear elemento de notificación temporal
    const notification = document.createElement("div");
    notification.className = "state-change-notification";
    notification.innerHTML = `
      <div class="notification-content">
        <span class="notification-icon">✅</span>
        <span class="notification-text">Pedido #${pedidoId} → ${nuevoEstado}</span>
      </div>
    `;

    document.body.appendChild(notification);

    // Animar entrada
    setTimeout(() => notification.classList.add("show"), 10);

    // Remover después de 3 segundos
    setTimeout(() => {
      notification.classList.add("hide");
      setTimeout(() => document.body.removeChild(notification), 300);
    }, 3000);
  };

  // Agregar atajos de teclado para navegación rápida
  useEffect(() => {
    const handleKeyPress = (e) => {
      // Solo funciona si hay un pedido seleccionado
      if (!selectedOrder) return;

      // Alt + número para cambio directo de estado
      if (e.altKey) {
        const key = parseInt(e.key);

        if (key >= 1 && key <= estadosDisponibles.length) {
          const nuevoEstado = estadosDisponibles[key - 1];
          handleEstadoChange(selectedOrder.id, nuevoEstado);
          e.preventDefault();
        }
      }

      // Espacio para toggle rápido del pedido seleccionado
      if (e.code === "Space" && e.target.tagName !== "INPUT") {
        handleToggleEstado(selectedOrder.id, selectedOrder.estado);
        e.preventDefault();
      }

      // Escape para deseleccionar pedido
      if (e.code === "Escape") {
        setSelectedOrder(null);
        e.preventDefault();
      }
    };

    window.addEventListener("keydown", handleKeyPress);
    return () => window.removeEventListener("keydown", handleKeyPress);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedOrder]);

  // Componente para selector de estado compacto con dropdown
  const StateSelector = ({
    currentState,
    orderId,
    isUpdating,
    onStateChange,
    estadosDisponibles,
    getStateIcon,
  }) => {
    const [isOpen, setIsOpen] = useState(false);
    const [dropdownPosition, setDropdownPosition] = useState("bottom");
    const selectorRef = React.useRef(null);

    const handleStateClick = (newState) => {
      if (newState !== currentState && !isUpdating) {
        onStateChange(orderId, newState);
      }
      setIsOpen(false);
    };

    const handleToggleDropdown = () => {
      if (isUpdating) return;

      if (!isOpen && selectorRef.current) {
        // Calcular si el dropdown debería abrirse hacia arriba o hacia abajo
        const rect = selectorRef.current.getBoundingClientRect();
        const viewportHeight = window.innerHeight;
        const availableStates = estadosDisponibles.filter(
          (estado) => estado !== currentState
        );
        const dropdownHeight = availableStates.length * 45 + 16; // 45px por opción + padding extra
        const spaceBelow = viewportHeight - rect.bottom;
        const spaceAbove = rect.top;

        // Margen de seguridad más grande para evitar cortes
        const safetyMargin = 80; // Aumentado para mayor seguridad

        // Lógica mejorada: priorizar abrir hacia arriba si está en la parte inferior
        const isInBottomHalf = rect.bottom > viewportHeight * 0.6;

        if (isInBottomHalf && spaceAbove > dropdownHeight + 20) {
          // Si está en la mitad inferior y hay espacio arriba, abrir hacia arriba
          setDropdownPosition("top");
        } else if (
          spaceBelow < dropdownHeight + safetyMargin &&
          spaceAbove > dropdownHeight + 20
        ) {
          // Si no hay suficiente espacio abajo pero sí arriba, abrir hacia arriba
          setDropdownPosition("top");
        } else {
          setDropdownPosition("bottom");
        }
      }

      setIsOpen(!isOpen);
    };

    return (
      <div
        ref={selectorRef}
        className="state-selector-container"
        onClick={(e) => e.stopPropagation()}
        style={{ position: "relative" }}>
        <div
          className={`status-badge status-${currentState
            .toLowerCase()
            .replace(/\s+/g, "-")} ${isUpdating ? "updating" : ""}`}
          onClick={handleToggleDropdown}
          style={{
            cursor: isUpdating ? "not-allowed" : "pointer",
            position: "relative",
            transition: "all 0.2s ease",
            userSelect: "none",
          }}
          title={isUpdating ? "Actualizando..." : "Click para cambiar estado"}>
          {isUpdating ? (
            <span>Actualizando...</span>
          ) : (
            <span>{currentState}</span>
          )}
        </div>

        {isOpen && !isUpdating && (
          <div
            className="state-selector-dropdown"
            style={{
              position: "absolute",
              [dropdownPosition === "top" ? "bottom" : "top"]: "100%",
              left: "0",
              right: "0",
              backgroundColor: "white",
              border: "1px solid #ddd",
              borderRadius: "8px",
              boxShadow: "0 8px 24px rgba(0,0,0,0.2)",
              zIndex: 9999,
              overflow: "hidden",
              [dropdownPosition === "top" ? "marginBottom" : "marginTop"]:
                "6px",
              minWidth: "150px",
              maxHeight: "250px",
              overflowY: "auto",
              transform:
                dropdownPosition === "top" ? "translateY(0)" : "translateY(0)",
            }}>
            {estadosDisponibles
              .filter((estado) => estado !== currentState)
              .map((estado, index, filteredArray) => {
                return (
                  <div
                    key={estado}
                    className="state-option"
                    onClick={() => handleStateClick(estado)}
                    style={{
                      padding: "8px 12px",
                      cursor: "pointer",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      borderBottom:
                        index === filteredArray.length - 1
                          ? "none"
                          : "1px solid #f0f0f0",
                      backgroundColor: "white",
                      transition: "background-color 0.2s",
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.backgroundColor = "#f8f9fa";
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.backgroundColor = "white";
                    }}
                    title={`Cambiar a ${estado}`}>
                    <span
                      className={`status-badge status-${estado
                        .toLowerCase()
                        .replace(/\s+/g, "-")}`}
                      style={{
                        fontSize: "0.75rem",
                        margin: 0,
                        border: "none",
                        display: "inline-block",
                      }}>
                      {estado}
                    </span>
                  </div>
                );
              })}
          </div>
        )}

        {/* Overlay para cerrar dropdown al hacer click fuera */}
        {isOpen && (
          <div
            style={{
              position: "fixed",
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              zIndex: 9998,
            }}
            onClick={() => setIsOpen(false)}
          />
        )}
      </div>
    );
  };

  return (
    <div className="page-container">
      <h2 className="theme-header">Gestión de Pedidos</h2>

      <div className="admin-header">
        <input
          type="text"
          placeholder="Buscar por ID, cliente o estado..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="theme-button" onClick={handleSearch}>
          Buscar
        </button>
      </div>

      {/* Resumen de Estados Actuales */}
      {filteredOrders.length > 0 && (
        <div
          className="states-summary-panel"
          style={{
            background: "#fff",
            padding: "1rem 1.5rem",
            borderRadius: "8px",
            marginBottom: "1.5rem",
            border: "1px solid #f0d6e2",
            boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
          }}>
          <h4
            style={{
              margin: "0 0 1rem 0",
              color: "#ff6ba6",
              fontSize: "0.875rem",
              fontWeight: "600",
              textTransform: "uppercase",
            }}>
            📊 Resumen de Estados Actuales
          </h4>
          <div
            style={{
              display: "flex",
              gap: "1rem",
              flexWrap: "wrap",
              fontSize: "0.9rem",
            }}>
            {estadosDisponibles.map((estado) => {
              const count = filteredOrders.filter(
                (order) => (order.estado || "Pendiente") === estado
              ).length;
              return (
                <span
                  key={estado}
                  className={`status-badge status-${estado
                    .toLowerCase()
                    .replace(/\s+/g, "-")}`}
                  style={{
                    padding: "0.375rem 0.75rem",
                    borderRadius: "20px",
                    fontSize: "0.75rem",
                    fontWeight: "600",
                    display: "flex",
                    alignItems: "center",
                    gap: "0.375rem",
                  }}>
                  {estado}: <strong>{count}</strong>
                </span>
              );
            })}
          </div>
        </div>
      )}

      {loading ? (
        <div
          className="loading-container"
          style={{ padding: "2rem", textAlign: "center" }}>
          <div className="spinner-border text-pink" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p>Cargando pedidos...</p>
        </div>
      ) : (
        <>
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Fecha Creación</th>
                <th>Fecha Entrega</th>
                <th>Estado</th>
                <th>Método Pago</th>
                <th>Total</th>
                <th>Dirección</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map((order) => (
                <tr
                  key={order.id}
                  onClick={(e) => {
                    // Evitar selección si se hace click en elementos interactivos
                    if (
                      e.target.closest(".dropdown") ||
                      e.target.closest("button") ||
                      e.target.closest("select")
                    ) {
                      return;
                    }
                    console.log("Fila clickeada. Seleccionando pedido:", order);
                    setSelectedOrder(order);
                  }}
                  className={
                    selectedOrder?.id === order.id ? "selected-row" : ""
                  }>
                  <td>{order.id}</td>
                  <td>{order.usuarioNombre || "N/A"}</td>
                  <td>{formatDate(order.fechaCreacion)}</td>
                  <td>
                    {formatDeliveryDate(order.fechaEntrega, order.estado)}
                  </td>
                  <td>
                    <StateSelector
                      currentState={order.estado || "Pendiente"}
                      orderId={order.id}
                      isUpdating={updatingStates.has(order.id)}
                      onStateChange={handleEstadoChange}
                      estadosDisponibles={estadosDisponibles}
                      getStateIcon={getStateIcon}
                    />
                  </td>
                  <td>
                    {order.metodoPago ? (
                      <span
                        className={`payment-method ${order.metodoPago?.toLowerCase()}`}>
                        {getPaymentIcon(order.metodoPago)} {order.metodoPago}
                      </span>
                    ) : (
                      <span className="text-muted">Sin pago</span>
                    )}
                  </td>
                  <td>{formatCurrency(order.costoTotal)}</td>
                  <td>{order.direccionEntrega || "N/A"}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="table-footer">
            <div className="selected-info">
              {selectedOrder ? (
                <span>
                  <strong>Seleccionado:</strong> Pedido #{selectedOrder.id} -{" "}
                  {selectedOrder.usuarioNombre} ({selectedOrder.estado})
                </span>
              ) : (
                <span>Haz click en una fila para seleccionar un pedido</span>
              )}
            </div>
            <div className="actions">
              <button
                className="theme-button"
                disabled={!selectedOrder || loadingDetails}
                onClick={() => fetchOrderDetails(selectedOrder?.id)}>
                {loadingDetails ? "Cargando..." : "Ver Detalles"}
              </button>
              <button
                className={`theme-button delete-button ${
                  !selectedOrder ||
                  (selectedOrder.estado !== "Pendiente" &&
                    selectedOrder.estado !== "Cancelado")
                    ? "disabled"
                    : ""
                }`}
                disabled={
                  !selectedOrder ||
                  (selectedOrder.estado !== "Pendiente" &&
                    selectedOrder.estado !== "Cancelado")
                }
                onClick={() => {
                  console.log(
                    "Botón eliminar clickeado. Selected order:",
                    selectedOrder
                  );
                  if (
                    selectedOrder &&
                    (selectedOrder.estado === "Pendiente" ||
                      selectedOrder.estado === "Cancelado")
                  ) {
                    console.log("Abriendo modal de eliminación");
                    setShowDeleteModal(true);
                  } else {
                    console.log(
                      "No se puede eliminar. Estado:",
                      selectedOrder?.estado
                    );
                  }
                }}
                title={
                  !selectedOrder
                    ? "Selecciona un pedido para eliminar"
                    : selectedOrder.estado !== "Pendiente" &&
                      selectedOrder.estado !== "Cancelado"
                    ? "Solo se pueden eliminar pedidos en estado 'Pendiente' o 'Cancelado'"
                    : "Eliminar pedido seleccionado"
                }>
                Eliminar
              </button>
            </div>
          </div>
        </>
      )}

      {/* Modal de Detalles del Pedido */}
      {showDetailsModal && orderDetails && (
        <div className="modal-backdrop">
          <div className="modal-content order-details-modal">
            <div className="modal-header">
              <h3>Detalles del Pedido #{orderDetails.id}</h3>
              <button
                className="modal-close"
                onClick={() => {
                  setShowDetailsModal(false);
                  setOrderDetails(null);
                }}
                aria-label="Cerrar">
                ×
              </button>
            </div>

            <div className="modal-body">
              {/* Información General del Pedido */}
              <div className="detail-section">
                <h4>📋 Información General</h4>
                <div className="detail-grid">
                  <div className="detail-item">
                    <label>Pedido #:</label>
                    <span className="highlight-id">#{orderDetails.id}</span>
                  </div>
                  <div className="detail-item">
                    <label>Estado Actual:</label>
                    <span
                      className={`status-badge status-${(
                        orderDetails.estado || "Pendiente"
                      )
                        .toLowerCase()
                        .replace(/\s+/g, "-")}`}>
                      {orderDetails.estado || "Pendiente"}
                    </span>
                  </div>
                  <div className="detail-item">
                    <label>Tipo de Comprobante:</label>
                    <span>
                      {orderDetails.necesitaFactura ? "Factura" : "Boleta"}
                    </span>
                  </div>
                  <div className="detail-item">
                    <label>Método de Pago:</label>
                    <span>
                      {orderDetails.metodoPago ? (
                        <span
                          className={`payment-method ${orderDetails.metodoPago?.toLowerCase()}`}>
                          {getPaymentIcon(orderDetails.metodoPago)}{" "}
                          {orderDetails.metodoPago}
                        </span>
                      ) : (
                        <span className="text-muted">
                          Sin información de pago
                        </span>
                      )}
                    </span>
                  </div>
                </div>
              </div>

              {/* Información del Cliente y Entrega */}
              <div className="detail-section">
                <h4>👤 Cliente y Entrega</h4>
                <div className="detail-grid">
                  <div className="detail-item">
                    <label>Cliente:</label>
                    <span>{orderDetails.usuarioNombre || "N/A"}</span>
                  </div>
                  <div className="detail-item">
                    <label>Dirección de Entrega:</label>
                    <span>{orderDetails.direccionEntrega || "N/A"}</span>
                  </div>
                </div>
              </div>

              {/* Fechas Importantes */}
              <div className="detail-section">
                <h4>📅 Fechas Importantes</h4>
                <div className="detail-grid">
                  <div className="detail-item">
                    <label>Fecha de Creación:</label>
                    <span>{formatDate(orderDetails.fechaCreacion)}</span>
                  </div>
                  <div className="detail-item">
                    <label>Fecha de Entrega:</label>
                    <span>
                      {orderDetails.estado === "Entregado" &&
                      orderDetails.fechaEntrega
                        ? formatDate(orderDetails.fechaEntrega)
                        : "Pendiente"}
                    </span>
                  </div>
                </div>
              </div>

              {/* Productos del Pedido */}
              <div className="detail-section">
                <h4>🛍️ Productos Solicitados</h4>
                {orderDetails.detalles && orderDetails.detalles.length > 0 ? (
                  <div className="products-table">
                    <table>
                      <thead>
                        <tr>
                          <th>Producto</th>
                          <th>Cant.</th>
                          <th>Precio Unit.</th>
                          <th>Personalización</th>
                          <th>Subtotal</th>
                        </tr>
                      </thead>
                      <tbody>
                        {orderDetails.detalles.map((detalle, index) => (
                          <tr key={index}>
                            <td className="product-name">
                              {detalle.nombreProducto}
                            </td>
                            <td className="quantity">{detalle.cantidad}</td>
                            <td>{formatCurrency(detalle.precioUnitario)}</td>
                            <td className="customization">
                              {detalle.personalizacion || "Sin personalización"}
                            </td>
                            <td className="subtotal">
                              {formatCurrency(detalle.subtotal)}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p className="no-products">
                    No se encontraron productos para este pedido.
                  </p>
                )}
              </div>

              {/* Resumen Financiero */}
              <div className="detail-section">
                <h4>💰 Resumen Financiero</h4>
                <div className="cost-summary">
                  <div className="cost-item">
                    <label>Subtotal (Productos):</label>
                    <span>{formatCurrency(orderDetails.subtotal)}</span>
                  </div>
                  <div className="cost-item">
                    <label>Costo de Envío:</label>
                    <span>
                      {formatCurrency(
                        orderDetails.costoTotal - orderDetails.subtotal
                      )}
                    </span>
                  </div>
                  <div className="cost-separator"></div>
                  <div className="cost-item total">
                    <label>Total a Pagar:</label>
                    <span>{formatCurrency(orderDetails.costoTotal)}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="modal-actions">
              <button
                className="theme-button"
                onClick={() => {
                  setShowDetailsModal(false);
                  setOrderDetails(null);
                }}>
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}

      {showDeleteModal && (
        <div className="modal-backdrop">
          <div className="modal-content delete-confirm">
            <h3>⚠️ Confirmar Eliminación</h3>
            <div className="delete-warning">
              <p>
                ¿Está seguro de que desea eliminar el pedido #
                {selectedOrder?.id}?
              </p>
              <div className="order-info">
                <p>
                  <strong>Cliente:</strong> {selectedOrder?.usuarioNombre}
                </p>
                <p>
                  <strong>Estado:</strong> {selectedOrder?.estado}
                </p>
                <p>
                  <strong>Total:</strong>{" "}
                  {formatCurrency(selectedOrder?.costoTotal)}
                </p>
                <p>
                  <strong>Fecha:</strong>{" "}
                  {formatDate(selectedOrder?.fechaCreacion)}
                </p>
              </div>
              <p className="warning-text">
                <strong>⚠️ Advertencia:</strong> Esta acción no se puede
                deshacer. Solo se pueden eliminar pedidos en estado "Pendiente"
                o "Cancelado".
              </p>
            </div>
            <div className="modal-actions">
              <button
                className="theme-button delete-button"
                onClick={() => {
                  console.log("Botón 'Sí, Eliminar' clickeado");
                  handleDelete();
                }}>
                Sí, Eliminar
              </button>
              <button
                className="theme-button"
                onClick={() => setShowDeleteModal(false)}>
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminOrders;
