import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../services/userServices";
import "../styles/CrearCuenta.css";
import { Link } from "react-router-dom";

function CrearCuenta() {
  const [nombreCompleto, setNombreCompleto] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      await register(nombreCompleto, email, password);
      alert("¡Cuenta creada con éxito! Por favor inicia sesión.");
      navigate("/login");
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
          placeholder="Nombre completo"
          value={nombreCompleto}
          onChange={(e) => setNombreCompleto(e.target.value)}
          required
        />

        <input
          type="email"
          placeholder="Correo electrónico"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type="submit" disabled={isLoading}>
          {isLoading ? "Registrando..." : "Registrarme"}
        </button>
      </form>

      <div className="login-redirect">
        ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión aquí</Link>
      </div>
    </div>
  );
}

export default CrearCuenta;
