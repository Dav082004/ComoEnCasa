import React, { useState } from "react";

const PasswordConfirmModal = ({
  isOpen,
  onClose,
  onConfirm,
  title = "Confirmar acción",
  message = "Ingresa la contraseña de administrador para continuar:",
}) => {
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!password.trim()) {
      setError("La contraseña es requerida");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await onConfirm(password);
      setPassword("");
      onClose();
    } catch (error) {
      setError("Contraseña incorrecta");
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setPassword("");
    setError("");
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-backdrop">
      <div className="modal-content">
        <h3 className="modal-title">{title}</h3>
        <p>{message}</p>

        <form onSubmit={handleSubmit} className="product-form">
          <div className="form-group">
            <label htmlFor="admin-password" className="form-label">
              Contraseña de Administrador *
            </label>
            <input
              id="admin-password"
              type="password"
              placeholder="Ingresa tu contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="form-input"
              autoFocus
            />
            {error && (
              <div
                style={{ color: "#c33", fontSize: "14px", marginTop: "5px" }}>
                {error}
              </div>
            )}
          </div>

          <div className="modal-actions">
            <button
              type="submit"
              className="theme-button"
              disabled={loading || !password.trim()}>
              {loading ? "Verificando..." : "Confirmar"}
            </button>
            <button
              type="button"
              className="theme-button"
              onClick={handleClose}
              disabled={loading}>
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PasswordConfirmModal;
