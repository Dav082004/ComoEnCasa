import React, { useState } from "react";
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
    } catch (err) {
      setError("Error de conexión con el servidor.");
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
        <button type="submit">Enviar enlace de recuperación</button>
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
