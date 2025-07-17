import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../services/userServices";
import "../styles/CrearCuenta.css";
import { Link } from "react-router-dom";

function CrearCuenta() {
  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [passwordErrors, setPasswordErrors] = useState([]);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const navigate = useNavigate();

  // Validación de contraseña
  const validatePassword = (password) => {
    const errors = [];

    if (password.length < 6) {
      errors.push("La contraseña debe tener al menos 6 caracteres");
    }

    if (!/[A-Z]/.test(password)) {
      errors.push("Debe incluir al menos una letra mayúscula");
    }

    if (!/[0-9]/.test(password)) {
      errors.push("Debe incluir al menos un número");
    }

    return errors;
  };

  // Manejar cambio de contraseña con validación en tiempo real
  const handlePasswordChange = (e) => {
    const newPassword = e.target.value;
    setPassword(newPassword);
    setPasswordErrors(validatePassword(newPassword));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    // Validar contraseña
    const passwordValidationErrors = validatePassword(password);
    if (passwordValidationErrors.length > 0) {
      setPasswordErrors(passwordValidationErrors);
      setError("Por favor, corrige los errores de contraseña");
      setIsLoading(false);
      return;
    }

    // Validar confirmación de contraseña
    if (password !== confirmPassword) {
      setError("Las contraseñas no coinciden");
      setIsLoading(false);
      return;
    }
    try {
      await register(nombre, apellido, email, password);
      setShowSuccessModal(true);

      // Evitar scroll del body cuando el modal está abierto
      document.body.classList.add("modal-open");

      // Redirigir a login después de 5 segundos
      setTimeout(() => {
        document.body.classList.remove("modal-open");
        navigate("/login");
      }, 5000);
    } catch (err) {
      setError(err.message || "Error al registrar usuario");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="crearcuenta-container">
      <h2>Crear Cuenta</h2>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Nombre"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
          required
        />

        <input
          type="text"
          placeholder="Apellido"
          value={apellido}
          onChange={(e) => setApellido(e.target.value)}
          required
        />

        <input
          type="email"
          placeholder="Correo electrónico"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <div className="input-group mb-3">
          <input
            type={showPassword ? "text" : "password"}
            className="form-control"
            placeholder="Contraseña"
            value={password}
            onChange={handlePasswordChange}
            required
          />
          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={() => setShowPassword(!showPassword)}>
            {showPassword ? "Ocultar" : "Mostrar"}
          </button>
        </div>

        {/* Validaciones de contraseña */}
        {password && (
          <div className="password-requirements mb-3">
            <small className="text-muted">Requisitos de contraseña:</small>
            <ul className="list-unstyled mt-2">
              <li
                className={
                  password.length >= 6 ? "text-success" : "text-danger"
                }>
                {password.length >= 6 ? "✓" : "✗"} Al menos 6 caracteres
              </li>
              <li
                className={
                  /[A-Z]/.test(password) ? "text-success" : "text-danger"
                }>
                {/[A-Z]/.test(password) ? "✓" : "✗"} Al menos una letra
                mayúscula
              </li>
              <li
                className={
                  /[0-9]/.test(password) ? "text-success" : "text-danger"
                }>
                {/[0-9]/.test(password) ? "✓" : "✗"} Al menos un número
              </li>
            </ul>
          </div>
        )}

        <div className="input-group mb-3">
          <input
            type={showConfirmPassword ? "text" : "password"}
            className="form-control"
            placeholder="Confirmar contraseña"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}>
            {showConfirmPassword ? "Ocultar" : "Mostrar"}
          </button>
        </div>

        {/* Indicador de coincidencia de contraseñas */}
        {confirmPassword && (
          <div className="mb-3">
            <small
              className={
                password === confirmPassword ? "text-success" : "text-danger"
              }>
              {password === confirmPassword
                ? "✓ Las contraseñas coinciden"
                : "✗ Las contraseñas no coinciden"}
            </small>
          </div>
        )}

        <button
          type="submit"
          className="btn btn-success w-100"
          disabled={
            isLoading ||
            passwordErrors.length > 0 ||
            password !== confirmPassword
          }>
          {isLoading ? "Registrando..." : "Registrarme"}
        </button>
      </form>

      <div className="login-redirect">
        ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión aquí</Link>
      </div>

      {/* Modal de éxito */}
      {showSuccessModal && (
        <div
          className="success-modal-overlay"
          onClick={(e) => {
            // Cerrar modal si se hace clic en el overlay (fuera del modal)
            if (e.target === e.currentTarget) {
              document.body.classList.remove("modal-open");
              navigate("/login");
            }
          }}>
          <div className="success-modal">
            <div className="success-modal-content">
              <div className="success-icon">🎉</div>
              <h3>¡Cuenta creada con éxito!</h3>
              <p>
                Te hemos enviado un correo electrónico de verificación a{" "}
                <strong>{email}</strong>
              </p>
              <p>
                Por favor, revisa tu bandeja de entrada y haz clic en el enlace
                de verificación para activar tu cuenta antes de iniciar sesión.
              </p>
              <div className="countdown-info">
                <p>Serás redirigido al login en 5 segundos...</p>
              </div>
              <button
                className="modal-button"
                onClick={() => {
                  document.body.classList.remove("modal-open");
                  navigate("/login");
                }}>
                Ir al Login ahora
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default CrearCuenta;
