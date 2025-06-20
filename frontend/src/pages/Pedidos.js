import React, { useEffect, useState } from "react";
import { getPedidosByUserId } from "../services/pedidoService";
import "../styles/Pedidos.css";
import "bootstrap/dist/css/bootstrap.min.css";

const Pedidos = () => {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState("todos");

  const getEstadoBadgeClass = (estado) => {
    switch (estado) {
      case "Pendiente":
        return "bg-warning text-dark";
      case "En preparación":
        return "bg-info text-white";
      case "Listo":
        return "bg-primary text-white";
      case "Entregado":
        return "bg-success text-white";
      case "Cancelado":
        return "bg-danger text-white";
      default:
        return "bg-secondary text-white";
    }
  };

  const filtrarPedidos = (pedidos) => {
    if (filtroEstado === "todos") {
      return pedidos;
    }
    return pedidos.filter((pedido) => pedido.estado === filtroEstado);
  };

  const pedidosFiltrados = filtrarPedidos(pedidos);
  const estadosDisponibles = [...new Set(pedidos.map((p) => p.estado))].sort();

  useEffect(() => {
    const fetchPedidos = async () => {
      try {
        setLoading(true);
        const userId = localStorage.getItem("userId");
        if (userId) {
          const data = await getPedidosByUserId(userId);
          setPedidos(data);
        }
      } catch (error) {
        console.error("Error al cargar los pedidos:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchPedidos();
  }, []);

  if (loading) {
    return (
      <div
        className="loading-container d-flex flex-column align-items-center justify-content-center"
        style={{ minHeight: "40vh" }}>
        <div
          className="spinner-border text-pink mb-3"
          role="status"
          style={{ width: "4rem", height: "4rem" }}>
          <span className="visually-hidden">Cargando...</span>
        </div>
        <span className="mt-2" style={{ color: "#d63384", fontWeight: "bold" }}>
          Cargando pedidos...
        </span>
      </div>
    );
  }

  return (
    <div className="pedidos-page py-4">
      <div className="container">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h1 className="mb-0" style={{ color: "#d63384", fontWeight: "bold" }}>
            Mis Pedidos
          </h1>

          {pedidos.length > 0 && (
            <div className="d-flex align-items-center">
              <label htmlFor="filtroEstado" className="me-2 text-muted">
                Filtrar por estado:
              </label>
              <select
                id="filtroEstado"
                className="form-select form-select-sm"
                style={{ width: "auto" }}
                value={filtroEstado}
                onChange={(e) => setFiltroEstado(e.target.value)}>
                <option value="todos">Todos ({pedidos.length})</option>
                {estadosDisponibles.map((estado) => {
                  const count = pedidos.filter(
                    (p) => p.estado === estado
                  ).length;
                  return (
                    <option key={estado} value={estado}>
                      {estado} ({count})
                    </option>
                  );
                })}
              </select>
            </div>
          )}
        </div>

        {pedidosFiltrados.length > 0 ? (
          <div>
            <p className="text-muted mb-3">
              Mostrando {pedidosFiltrados.length} de {pedidos.length} pedidos
            </p>
            {pedidosFiltrados.map((pedido) => (
              <div
                key={pedido.id}
                className="pedido-card p-3 mb-3 bg-white rounded shadow-sm">
                <div className="d-flex justify-content-between align-items-start">
                  <div>
                    <h5 className="mb-2">Pedido #{pedido.id}</h5>
                    <p className="mb-1">
                      <strong>Fecha de creación:</strong>{" "}
                      {new Date(pedido.fechaCreacion).toLocaleDateString(
                        "es-ES",
                        {
                          year: "numeric",
                          month: "long",
                          day: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        }
                      )}
                    </p>
                    {pedido.fechaEntrega && (
                      <p className="mb-1">
                        <strong>Fecha de entrega:</strong>{" "}
                        {new Date(pedido.fechaEntrega).toLocaleDateString(
                          "es-ES",
                          {
                            year: "numeric",
                            month: "long",
                            day: "numeric",
                            hour: "2-digit",
                            minute: "2-digit",
                          }
                        )}
                      </p>
                    )}
                    <p className="mb-1">
                      <strong>Total:</strong> S/{" "}
                      {pedido.costoTotal
                        ? pedido.costoTotal.toFixed(2)
                        : "0.00"}
                    </p>
                    {pedido.direccionEntrega && (
                      <p className="mb-1">
                        <strong>Dirección:</strong> {pedido.direccionEntrega}
                      </p>
                    )}
                  </div>
                  <span
                    className={`badge fs-6 ${getEstadoBadgeClass(
                      pedido.estado
                    )}`}>
                    {pedido.estado}
                  </span>
                </div>

                {pedido.detalles && pedido.detalles.length > 0 && (
                  <div className="mt-3">
                    <h6>Productos:</h6>
                    <ul className="list-unstyled">
                      {pedido.detalles.map((detalle) => (
                        <li
                          key={detalle.id}
                          className="d-flex justify-content-between align-items-center border-bottom pb-2 mb-2">
                          <div>
                            <strong>{detalle.nombreProducto}</strong>
                            {detalle.personalizacion && (
                              <div className="text-muted small">
                                Personalización: {detalle.personalizacion}
                              </div>
                            )}
                          </div>
                          <div className="text-end">
                            <div>
                              {detalle.cantidad} unidad
                              {detalle.cantidad > 1 ? "es" : ""}
                            </div>
                            <div className="text-muted">
                              S/{" "}
                              {detalle.precioUnitario
                                ? detalle.precioUnitario.toFixed(2)
                                : "0.00"}{" "}
                              c/u
                            </div>
                            <div className="fw-bold">
                              S/{" "}
                              {detalle.subtotal
                                ? detalle.subtotal.toFixed(2)
                                : "0.00"}
                            </div>
                          </div>
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <div className="no-pedidos text-center p-4 bg-light rounded shadow-sm">
            <h4 style={{ color: "#d63384" }}>
              {filtroEstado === "todos"
                ? "No tienes pedidos registrados aún."
                : `No tienes pedidos con estado "${filtroEstado}".`}
            </h4>
            <p>
              {filtroEstado === "todos"
                ? "Explora nuestros productos y realiza tu primer pedido."
                : "Cambia el filtro para ver otros pedidos."}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Pedidos;
