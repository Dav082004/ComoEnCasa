import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/PagoExitoso.css";

function PagoExitoso() {
  const navigate = useNavigate();

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
        <p>Tu pedido ha sido procesado con éxito.</p>
        <p>Serás redirigido automáticamente en unos segundos...</p>
      </div>
    </div>
  );
}

export default PagoExitoso;