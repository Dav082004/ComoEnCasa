import axios from 'axios';

const API_URL = 'http://localhost:8081/api/productos';

export const getProductos = async () => {
  const { data } = await axios.get(API_URL);
  return data;
};

export const getProductoById = async (id) => {
  const { data } = await axios.get(`${API_URL}/${id}`);
  return data;
};

export const getProductosByCategoria = async (categoriaId) => {
  const { data } = await axios.get(`${API_URL}/categoria/${categoriaId}`);
  return data;
};

export const createProducto = async (producto) => {
  const { data } = await axios.post(API_URL, producto);
  return data;
};

export const updateProducto = async (id, producto) => {
  const { data } = await axios.put(`${API_URL}/${id}`, producto);
  return data;
};

export const deleteProducto = async (id) => {
  await axios.delete(`${API_URL}/${id}`);
};

export const getAllProducts   = getProductos;
export const createProduct    = createProducto;
export const updateProduct    = updateProducto;
export const deleteProduct    = deleteProducto;