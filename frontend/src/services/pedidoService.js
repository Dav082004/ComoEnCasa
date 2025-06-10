import axios from 'axios';

const PEDIDOS_URL = 'http://localhost:8081/api/pedidos';

export const getAllPedidos = async () => {
  const { data } = await axios.get(PEDIDOS_URL);
  return data;
};

export const getPedidosByUserId = async (userId) => {
  const { data } = await axios.get(`${PEDIDOS_URL}/usuario/${userId}`);
  return data;
};

export const getAllOrders = getAllPedidos;