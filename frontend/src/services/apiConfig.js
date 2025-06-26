import axios from "axios";

// Configuración base de Axios
const API_BASE_URL = "http://localhost:8081/api";

// Crear instancia de axios para administración
export const adminApi = axios.create({
  baseURL: `${API_BASE_URL}/admin`,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Importante para cookies de sesión
});

// Interceptor para incluir automáticamente información de usuario admin
adminApi.interceptors.request.use(
  (config) => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");

    // Solo proceder si el usuario es administrador
    if (user && user.rol === "ADMIN") {
      // Agregar headers que identifiquen al admin
      config.headers["X-User-Id"] = user.id;
      config.headers["X-User-Email"] = user.email;
      config.headers["X-User-Role"] = user.rol;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores de respuesta
adminApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      let message = "Error en la solicitud";

      if (status === 403) {
        message =
          data?.error ||
          "Acceso denegado. Verifica que tengas permisos de administrador.";
      } else if (status === 401) {
        message =
          data?.error || "No autorizado. Inicia sesión como administrador.";
        // Opcional: redirigir al login
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        if (!user.isAdmin) {
          localStorage.removeItem("user");
        }
      } else if (status === 400) {
        message = data?.error || "Datos de solicitud incorrectos.";
      } else if (status === 404) {
        message = data?.error || "Recurso no encontrado.";
      } else if (status === 500) {
        message = data?.error || data?.message || "Error interno del servidor";
      }

      console.error(`Error ${status}:`, {
        url: error.config?.url,
        method: error.config?.method,
        message,
        fullError: data,
      });

      // Agregar el mensaje al error para uso en componentes
      error.userMessage = message;
    }

    return Promise.reject(error);
  }
);

// Instancia para endpoints públicos
export const publicApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

export default adminApi;
