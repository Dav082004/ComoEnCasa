import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/AuthRequiredModal.css";

const AuthRequiredModal = ({ isOpen, onClose, message, action }) => {
  const navigate = useNavigate();

  if (!isOpen) return null;

  const handleAction = () => {
    onClose();
    if (action === "login") {
      navigate("/login");
    }
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    <div className="auth-modal-overlay" onClick={handleBackdropClick}>
      <div className="auth-modal">
        <div className="auth-modal-header">
          <h3>🔒 Autenticación Requerida</h3>
          <button className="auth-modal-close" onClick={onClose}>
            ×
          </button>
        </div>

        <div className="auth-modal-body">
          <div className="auth-modal-icon">
            <svg
              width="64"
              height="64"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2">
              <path d="M18 8A6 6 0 0 0 6 8v1a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2V8z" />
              <circle cx="12" cy="15" r="1" />
            </svg>
          </div>

          <p className="auth-modal-message">{message}</p>

          <div className="auth-modal-benefits">
            <h4>Al iniciar sesión podrás:</h4>
            <ul>
              <li>✅ Realizar compras seguras</li>
              <li>✅ Ver tu historial de pedidos</li>
              <li>✅ Guardar tus direcciones favoritas</li>
              <li>✅ Recibir ofertas exclusivas</li>
            </ul>
          </div>
        </div>

        <div className="auth-modal-footer">
          <button className="auth-modal-btn secondary" onClick={onClose}>
            Continuar navegando
          </button>
          <button className="auth-modal-btn primary" onClick={handleAction}>
            {action === "login" ? "Iniciar Sesión" : "Activar Cuenta"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AuthRequiredModal;
