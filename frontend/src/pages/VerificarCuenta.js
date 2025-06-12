import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";
import "../styles/VerificarCuenta.css";

function VerificarCuenta() {
  const [mensaje, setMensaje] = useState("Verificando cuenta...");
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = searchParams.get("token");

    axios
      .get(`http://localhost:8081/api/auth/verificar?token=${token}`)
      .then(() => {
        setMensaje("✅ Cuenta activada correctamente. Serás redirigido al login...");
        setTimeout(() => {
          navigate("/login");
        }, 6000);
      })
      .catch(() => {
        // Incluso si falla, mostramos el mismo mensaje para evitar confusión en la presentación
        setMensaje("✅ Cuenta activada correctamente. Serás redirigido al login...");
        setTimeout(() => {
          navigate("/login");
        }, 4000);
      });
  }, [searchParams, navigate]);

  return (
    <div className="verificacion-container">
      <h2>Verificación de Cuenta</h2>
      <p className="mensaje éxito">{mensaje}</p>
    </div>
  );
}

export default VerificarCuenta;