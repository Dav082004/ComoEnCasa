import React, { useEffect, useState } from "react";
import { getPedidosUsuario } from "../services/pedidoService";
import "../styles/Pedidos.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { getPedidosByUserId } from "../services/pedidoService";

const Pedidos = () => {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPedidos = async () => {
      try {
        setLoading(true);
        const data = await getPedidosUsuario(); // Supón que devuelve los pedidos del usuario autenticado
        setPedidos(data);
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
      <div className="loading-container d-flex flex-column align-items-center justify-content-center" style={{ minHeight: "40vh" }}>
        <div className="spinner-border text-pink mb-3" role="status" style={{ width: "4rem", height: "4rem" }}>
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
        <h1 className="mb-4" style={{ color: "#d63384", fontWeight: "bold" }}>
          Mis Pedidos
        </h1>

        {pedidos.length > 0 ? (
          pedidos.map((pedido) => (
            <div key={pedido.id} className="pedido-card p-3 mb-3 bg-white rounded shadow-sm">
              <h5 className="mb-2">Pedido #{pedido.id}</h5>
              <p><strong>Fecha:</strong> {new Date(pedido.fecha).toLocaleDateString()}</p>
              <p><strong>Total:</strong> S/ {pedido.total.toFixed(2)}</p>
              <p><strong>Estado:</strong> {pedido.estado}</p>
              <ul>
                {pedido.detalles.map((detalle, idx) => (
                  <li key={idx}>
                    {detalle.nombreProducto} - {detalle.cantidad} unidad{detalle.cantidad > 1 ? "es" : ""} - S/ {detalle.precio.toFixed(2)}
                  </li>
                ))}
              </ul>
            </div>
          ))
        ) : (
          <div className="no-pedidos text-center p-4 bg-light rounded shadow-sm">
            <h4 style={{ color: "#d63384" }}>No tienes pedidos registrados aún.</h4>
            <p>Explora nuestros productos y realiza tu primer pedido.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Pedidos;
