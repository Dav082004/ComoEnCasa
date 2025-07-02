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

      console.log("Respuesta del servidor:", response.data);
      return response.data;

    } catch (error) {
      console.error("Error en checkout:", error);

      if (error.response) {
        const status = error.response.status;
        const data = error.response.data;

        console.error("Respuesta del servidor:", data);
        console.error("Status:", status);

        // ✅ Lanza el error con mensaje si lo hay
        throw new Error(data?.mensaje || `Error del servidor: ${status}`);
      } else if (error.request) {
        console.error("No se recibió respuesta:", error.request);
        throw new Error("Error de conexión con el servidor");
      } else {
        console.error("Error en la configuración:", error.message);
        throw new Error("Error de configuración: " + error.message);
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
      throw new Error(
        error.response?.data?.mensaje || "Error al simular el pago"
      );
    }
  },
};

export default checkoutService;
