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
    }, 10000);

    return () => clearTimeout(timeout);
  }, [navigate]);

  return (
    <div className="pago-exitoso-container">
      <div className="pago-exitoso-card">
        <h2>🎉 ¡Gracias por tu compra!</h2>

        {/* Información del email */}
        <div className="email-info">
          <h4>📧 Información Importante</h4>
          <p>
            Te hemos enviado toda la información de tu pedido a tu correo
            electrónico.
          </p>
          <p>
            Por favor, revisa tu{" "}
            <span className="email-highlight">bandeja de entrada</span> y
            <span className="email-highlight"> carpeta de spam</span> para más
            detalles.
          </p>
        </div>

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
