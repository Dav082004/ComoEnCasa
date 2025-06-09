import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";
import "../styles/VerificarCuenta.css";

function VerificarCuenta() {
  const [estado, setEstado] = useState("verificando");
  const [mensaje, setMensaje] = useState("");
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = searchParams.get("token");

    if (!token) {
      setEstado("error");
      setMensaje("Token de verificación no encontrado en la URL.");
      return;
    }

    // El token se envía como query param (no como parte del path)
    axios
      .get(`http://localhost:8080/api/auth/verificar?token=${token}`)
      .then((response) => {
        console.log("✅ Cuenta verificada:", response.data);
        setEstado("éxito");
        setMensaje("✅ Tu cuenta ha sido verificada con éxito. Serás redirigido al login...");

        setTimeout(() => {
          navigate("/login");
        }, 3000);
      })
      .catch((error) => {
        console.error("❌ Error al verificar cuenta:", error);
        setEstado("error");

        if (error.response && error.response.data) {
          setMensaje(`⚠️ ${error.response.data}`);
        } else {
          setMensaje("Ocurrió un error al verificar tu cuenta.");
        }
      });
  }, [searchParams, navigate]);

  return (
    <div className="verificacion-container">
      <h2>Verificación de Cuenta</h2>
      <p className={`mensaje ${estado}`}>{mensaje}</p>
    </div>
  );
}

export default VerificarCuenta;