import React, { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../styles/PagoExitoso.css";

function PagoExitoso() {
  const navigate = useNavigate();
  const location = useLocation();
  const { pedidoData } = location.state || {};

  useEffect(() => {
    // Esperar 7 segundos y redirigir al home o al login
    const timeout = setTimeout(() => {
      navigate("/"); // Puedes cambiar esto por "/login" si prefieres
    }, 7000);

    return () => clearTimeout(timeout);
  }, [navigate]);

  return (
    <div className="pago-exitoso-container">
      <div className="pago-exitoso-card">
        <h2>🎉 ¡Gracias por tu compra!</h2>

        {pedidoData ? (
          <div className="pedido-detalles">
            <div className="pedido-info">
              <h3>Detalles del Pedido</h3>
              <p>
                <strong>Número de Pedido:</strong> {pedidoData.numeroPedido}
              </p>
              <p>
                <strong>Total:</strong> S/. {pedidoData.total?.toFixed(2)}
              </p>
              <p>
                <strong>Estado:</strong> {pedidoData.estado}
              </p>
              <p>
                <strong>Método de Pago:</strong> {pedidoData.metodoPago}
              </p>
              <p>
                <strong>Estado del Pago:</strong> {pedidoData.estadoPago}
              </p>
              {pedidoData.fechaEntrega && (
                <p>
                  <strong>Fecha de Entrega:</strong>{" "}
                  {new Date(pedidoData.fechaEntrega).toLocaleDateString()}
                </p>
              )}
            </div>

            {pedidoData.comprobanteId && (
              <div className="comprobante-info">
                <h3>Comprobante</h3>
                <p>
                  <strong>Tipo:</strong> {pedidoData.tipoComprobante}
                </p>
                <p>
                  <strong>Serie:</strong> {pedidoData.numeroSerie}
                </p>
                <p>
                  <strong>Número:</strong> {pedidoData.numeroComprobante}
                </p>
                {pedidoData.urlComprobante && (
                  <a
                    href={`http://localhost:8080${pedidoData.urlComprobante}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="btn btn-primary">
                    📄 Descargar Comprobante
                  </a>
                )}
              </div>
            )}

            <div className="acciones">
              <button
                className="btn btn-secondary"
                onClick={() => navigate("/pedidos")}>
                Ver Mis Pedidos
              </button>
              <button className="btn btn-primary" onClick={() => navigate("/")}>
                Seguir Comprando
              </button>
            </div>
          </div>
        ) : (
          <div>
            <p>Tu pedido ha sido procesado con éxito.</p>
            <p>Serás redirigido automáticamente en unos segundos...</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default PagoExitoso;
