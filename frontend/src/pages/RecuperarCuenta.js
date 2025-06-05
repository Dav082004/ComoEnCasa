// src/pages/RecuperarCuenta.js
import React, { useState } from "react";
<<<<<<< HEAD
import "../styles/RecuperarCuenta.css";
=======
import { recuperarCuenta } from "../services/userServices";
import "../styles/recuperarCuenta.css";
>>>>>>> cf7a9a81189303d3cb48829273d52c8f669bb89e

const RecuperarCuenta = () => {
  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensaje("");
    setError("");

    try {
<<<<<<< HEAD
      const response = await fetch("http://localhost:8080/api/auth/recuperar", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        setMensaje("Hemos enviado un enlace de recuperación a tu correo.");
        setEmail("");
      } else {
        const data = await response.json();
        setError(data.message || "No se pudo enviar el correo.");
      }
=======
      const data = await recuperarCuenta(email);
      setMensaje("Hemos enviado tu nueva contraseña al correo proporcionado.");
      setEmail("");
>>>>>>> cf7a9a81189303d3cb48829273d52c8f669bb89e
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
        {mensaje && <p className="recuperar-mensaje">{mensaje}</p>}
        {error && (
          <p className="recuperar-mensaje" style={{ color: "red" }}>
            {error}
          </p>
        )}
      </form>
    </div>
  );
};

export default RecuperarCuenta;
