import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/auth",
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor para manejar errores globalmente
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // El servidor respondió con un status code fuera de 2xx
      return Promise.reject({
        message: error.response.data?.error || "Error en la solicitud",
        status: error.response.status,
      });
    } else if (error.request) {
      // La petición fue hecha pero no se recibió respuesta
      return Promise.reject({
        message: "No se recibió respuesta del servidor",
      });
    } else {
      // Error al configurar la petición
      return Promise.reject({ message: error.message });
    }
  }
);

export const login = async (email, password) => {
  try {
    const response = await api.post("/login", { email, password });
    return response.data;
  } catch (error) {
    throw error; // El error ya está formateado por el interceptor
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
    throw error;
  }
};

export default api;
