import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import recomendacionService from "../services/recomendacionService";
import "../styles/Recomendacion.css";

const Recomendacion = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [recomendacion, setRecomendacion] = useState("");
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState(""); // 'success' o 'error'
  const [hasExistingRecommendation, setHasExistingRecommendation] =
    useState(false);

  const maxLength = 1000;
  const loadExistingRecommendation = useCallback(async () => {
    try {
      setLoadingData(true);
      const existingRecommendation =
        await recomendacionService.obtenerRecomendacion(user.id);

      if (existingRecommendation) {
        setRecomendacion(existingRecommendation);
        setHasExistingRecommendation(true);
      }
    } catch (error) {
      console.error("Error al cargar recomendación existente:", error);
    } finally {
      setLoadingData(false);
    }
  }, [user]);

  // Redirigir si no hay usuario logueado
  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }

    // Cargar recomendación existente si la hay
    loadExistingRecommendation();
  }, [user, navigate, loadExistingRecommendation]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!recomendacion.trim()) {
      setMessage("Por favor, escribe tu recomendación antes de enviar.");
      setMessageType("error");
      return;
    }

    if (recomendacion.length > maxLength) {
      setMessage(`Tu recomendación no puede exceder ${maxLength} caracteres.`);
      setMessageType("error");
      return;
    }

    try {
      setLoading(true);
      setMessage("");

      await recomendacionService.guardarRecomendacion(
        user.id,
        recomendacion.trim()
      );

      setMessage(
        hasExistingRecommendation
          ? "¡Tu recomendación ha sido actualizada exitosamente!"
          : "¡Gracias por tu recomendación! Ha sido guardada exitosamente."
      );
      setMessageType("success");
      setHasExistingRecommendation(true);

      // Redirigir después de un breve delay
      setTimeout(() => {
        navigate("/");
      }, 2000);
    } catch (error) {
      setMessage(
        "Error al guardar la recomendación. Por favor, inténtalo de nuevo."
      );
      setMessageType("error");
      console.error("Error:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate("/");
  };

  if (loadingData) {
    return (
      <div className="recomendacion-container">
        <div className="recomendacion-card">
          <div className="loading-content">
            <div className="spinner"></div>
            <p>Cargando...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="recomendacion-container">
      <div className="recomendacion-card">
        <div className="recomendacion-header">
          <h2>
            {hasExistingRecommendation
              ? "✏️ Editar tu Recomendación"
              : "✍️ Comparte tu Experiencia"}
          </h2>
          <p className="recomendacion-subtitle">
            Hola, <strong>{user.nombreCompleto}</strong>.
            {hasExistingRecommendation
              ? " Puedes editar tu recomendación anterior aquí."
              : " Nos encantaría conocer tu opinión sobre nuestros productos y servicios."}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="recomendacion-form">
          <div className="form-group">
            <label htmlFor="recomendacion" className="form-label">
              Tu Recomendación:
            </label>
            <textarea
              id="recomendacion"
              value={recomendacion}
              onChange={(e) => setRecomendacion(e.target.value)}
              className="form-textarea"
              placeholder="Cuéntanos qué te pareció nuestro servicio, la calidad de nuestros productos, el proceso de compra, o cualquier aspecto que quieras compartir..."
              rows="8"
              maxLength={maxLength}
            />
            <div className="character-count">
              {recomendacion.length}/{maxLength} caracteres
            </div>
          </div>

          {message && <div className={`message ${messageType}`}>{message}</div>}

          <div className="form-actions">
            <button
              type="button"
              onClick={handleCancel}
              className="btn-secondary"
              disabled={loading}>
              Cancelar
            </button>
            <button
              type="submit"
              className="btn-primary"
              disabled={loading || !recomendacion.trim()}>
              {loading ? (
                <>
                  <span className="btn-spinner"></span>
                  {hasExistingRecommendation
                    ? "Actualizando..."
                    : "Guardando..."}
                </>
              ) : hasExistingRecommendation ? (
                "Actualizar Recomendación"
              ) : (
                "Enviar Recomendación"
              )}
            </button>
          </div>
        </form>

        <div className="recomendacion-info">
          <h4>ℹ️ Información importante:</h4>
          <ul>
            <li>Solo puedes tener una recomendación activa</li>
            <li>Puedes editarla en cualquier momento</li>
            <li>
              Tu recomendación puede aparecer en nuestra sección "Lo que dicen
              nuestros clientes"
            </li>
            <li>Mantén un lenguaje respetuoso y constructivo</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Recomendacion;
