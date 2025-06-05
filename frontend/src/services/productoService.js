import axios from "axios";

const API_URL = "http://localhost:8080/api/productos";

export const getProductos = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const getProductoById = async (id) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

export const getProductosByCategoria = async (categoriaId) => {
  const response = await axios.get(`${API_URL}/categoria/${categoriaId}`);
  return response.data;
};
