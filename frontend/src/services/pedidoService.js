import axios from "axios";

export const getPedidosByUserId = async (userId) => {
  const response = await axios.get(`http://localhost:8080/api/pedidos/usuario/${userId}`);
  return response.data;
};
