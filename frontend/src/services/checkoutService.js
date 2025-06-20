import axios from "axios";

const API_BASE_URL = "http://localhost:8081/api";

const checkoutService = {
  // Procesar checkout completo
  procesarCheckout: async (checkoutData) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/checkout/procesar`,
        checkoutData,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Error en checkout:", error);
      if (error.response) {
        // El servidor respondió con un código de estado de error
        console.error("Respuesta del servidor:", error.response.data);
        console.error("Status:", error.response.status);
        throw (
          error.response.data || {
            mensaje: `Error del servidor: ${error.response.status}`,
          }
        );
      } else if (error.request) {
        // La petición fue hecha pero no se recibió respuesta
        console.error("No se recibió respuesta:", error.request);
        const connectionError = new Error("Error de conexión con el servidor");
        connectionError.mensaje = "Error de conexión con el servidor";
        throw connectionError;
      } else {
        // Algo pasó en la configuración de la petición
        console.error("Error en la configuración:", error.message);
        const configError = new Error(
          "Error de configuración: " + error.message
        );
        configError.mensaje = "Error de configuración: " + error.message;
        throw configError;
      }
    }
  },

  // Simular pago para testing
  simularPago: async (metodoPago, monto) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/checkout/simular-pago`,
        null,
        {
          params: { metodoPago, monto },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Error simulando pago:", error);
      throw error.response?.data || false;
    }
  },
};

export default checkoutService;
