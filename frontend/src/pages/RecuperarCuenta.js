// src/pages/RecuperarCuenta.js
import React, { useState } from "react";

import { recuperarCuenta } from "../services/userServices";
import "../styles/RecuperarCuenta.css";

const RecuperarCuenta = () => {
  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensaje("");
    setError("");

    try {
      await recuperarCuenta(email);
      setMensaje("Hemos enviado tu nueva contraseña al correo proporcionado.");
      setEmail("");
    } catch (err) {
      setError(err.message || "Error al recuperar la cuenta.");
    }
  };

  return (
    <div className="recuperar-container">
      <h2>Recuperar Contraseña</h2>
      <form className="recuperar-form" onSubmit={handleSubmit}>
        <label htmlFor="email">Correo electrónico asociado:</label>
        <input
          type="email"
          id="email"
          placeholder="Ej. usuario@correo.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <button type="submit">Recuperar contraseña</button>
        {mensaje && <p className="recuperar-mensaje success">{mensaje}</p>}
        {error && <p className="recuperar-mensaje error">{error}</p>}
      </form>
    </div>
  );
};

export default RecuperarCuenta;
