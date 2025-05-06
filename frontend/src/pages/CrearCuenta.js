import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/CrearCuenta.css";

function CrearCuenta() {
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    alert("Cuenta creada (simulada). Redirigiendo al inicio...");
    navigate("/");
  };

  return (
    <div className="crearcuenta-container">
      <h2>Crear Cuenta</h2>
      <form onSubmit={handleSubmit}>
        <input type="text" placeholder="Nombre completo" required />
        <input type="email" placeholder="Correo electrónico" required />
        <input type="password" placeholder="Contraseña" required />
        <button type="submit">Registrarme</button>
      </form>
      <p>
        ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión aquí</Link>
      </p>
    </div>
  );
}

export default CrearCuenta;
