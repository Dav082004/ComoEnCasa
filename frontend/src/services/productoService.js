import axios from "axios";

// Para desarrollo local
const API_URL = "http://localhost:8080/api/productos";

// Si usas un proxy en package.json, puedes usar:
// const API_URL = '/api/productos';

export const obtenerProductos = async () => {
  try {
    const response = await axios.get(API_URL, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error obteniendo productos:", error);
    return [];
  }
};
