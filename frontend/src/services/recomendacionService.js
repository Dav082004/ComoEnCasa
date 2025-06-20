// Servicio para manejar recomendaciones de usuarios
const API_BASE_URL =
  process.env.REACT_APP_API_URL || "http://localhost:8081/api";

export const recomendacionService = {
  // Guardar o actualizar recomendación del usuario
  guardarRecomendacion: async (userId, recomendacion) => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/auth/recomendacion/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ recomendacion }),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Error al guardar la recomendación");
      }

      return await response.text();
    } catch (error) {
      console.error("Error al guardar recomendación:", error);
      throw error;
    }
  },

  // Obtener recomendación del usuario
  obtenerRecomendacion: async (userId) => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/auth/recomendacion/${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        if (response.status === 404) {
          return null; // Usuario no encontrado o sin recomendación
        }
        throw new Error("Error al obtener la recomendación");
      }

      const data = await response.json();
      return data.recomendacion || null;
    } catch (error) {
      console.error("Error al obtener recomendación:", error);
      throw error;
    }
  },

  // Obtener todas las recomendaciones públicas
  obtenerTodasRecomendaciones: async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/recomendaciones`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Error al obtener las recomendaciones");
      }

      const data = await response.json();
      return data;
    } catch (error) {
      console.error("Error al obtener todas las recomendaciones:", error);
      throw error;
    }
  },
};

export default recomendacionService;
