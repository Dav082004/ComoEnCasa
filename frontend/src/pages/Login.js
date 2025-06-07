import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../services/userServices";
import { useAuth } from "../context/AuthContext";
import "../styles/Login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { login: authLogin } = useAuth();

const handleSubmit = async (e) => {
  e.preventDefault();
  setError("");
  setIsLoading(true);

  try {
    const data = await login(email, password);

  
    localStorage.setItem("userId", data.usuario.id);
    localStorage.setItem("nombre", data.usuario.nombreCompleto);
    localStorage.setItem("email", data.usuario.email);
    localStorage.setItem("rol", data.usuario.rol);

   
    authLogin(data.usuario);

    navigate("/");
  } catch (err) {
    console.error("Error completo:", {
      message: err.message,
      stack: err.stack,
    });
    setError(err.message);
  } finally {
    setIsLoading(false);
  }
};


  return (
    <div className="login-container">
      <h2>Iniciar Sesión</h2>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit}>
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
          {isLoading ? "Cargando..." : "Iniciar sesión"}
        </button>
      </form>

      <p className="register-link">
        ¿No tienes una cuenta? <Link to="/crear-cuenta">Regístrate aquí</Link>
      </p>

      <div className="olvide-contrasena">
        <p>¿Olvidaste tu contraseña?</p>
        <Link to="/recuperar" className="link-recuperar">
          Recupera tu cuenta aquí
        </Link>
      </div>
    </div>
  );
}

export default Login;
