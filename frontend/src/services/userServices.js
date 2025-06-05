// src/services/userServices.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/auth",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: false,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      let message = "Error en la solicitud";

      if (status === 403) {
        message = data?.error || "Acceso denegado. Verifica tus permisos.";
      } else if (status === 401) {
        message = data?.error || "Credenciales inválidas.";
      } else if (status === 400) {
        message = data?.error || "Datos de solicitud incorrectos.";
      } else if (status === 404) {
        message = data?.error || "Recurso no encontrado.";
      } else if (status === 500) {
        message = data?.error || data?.message || "Error interno del servidor";
      }

      console.error(`Error ${status}:`, {
        message,
        details: data,
        url: error.config.url,
      });

      return Promise.reject({
        status,
        message,
        details: data || null,
      });
    } else if (error.request) {
      return Promise.reject({
        message: "No se recibió respuesta del servidor. Verifica tu conexión.",
        status: null,
        data: null,
      });
    } else {
      return Promise.reject({
        message: error.message || "Error al configurar la solicitud",
        status: null,
        data: null,
      });
    }
  }
);

export const login = async (email, password) => {
  try {
    const response = await api.post("/login", { email, password });
    if (!response.data.usuario) {
      throw new Error("Respuesta inválida del servidor");
    }
    return response.data;
  } catch (error) {
    const errorMessage =
      error.response?.data?.error || error.message || "Error al iniciar sesión";
    throw new Error(errorMessage);
  }
};

export const register = async (nombreCompleto, email, password) => {
  try {
    const response = await api.post("/registro", {
      nombreCompleto,
      email,
      password,
    });
    return response.data;
  } catch (error) {
    throw new Error(error.message || "Error al registrar usuario");
  }
};

//  Servicio de recuperación de contraseña
export const recuperarCuenta = async (email) => {
  try {
    const response = await api.post("/recuperar", { email });
    return response.data;
  } catch (error) {
    throw new Error(error.message || "Error al recuperar la cuenta");
  }
};

export default api;
