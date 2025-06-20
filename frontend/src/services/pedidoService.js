import axios from "axios";

const PEDIDOS_URL = "http://localhost:8081/api/pedidos";

export const getAllPedidos = async () => {
  const { data } = await axios.get(PEDIDOS_URL);
  return data;
};

export const getPedidosByUserId = async (userId) => {
  const { data } = await axios.get(`${PEDIDOS_URL}/usuario/${userId}`);
  return data;
};

export const getPedidoById = async (pedidoId) => {
  try {
    const { data } = await axios.get(`${PEDIDOS_URL}/${pedidoId}`);
    return data;
  } catch (error) {
    console.error("Error al obtener detalles del pedido:", error);
    throw error;
  }
};

// Nuevas funcionalidades para actualización de estado
export const actualizarEstadoPedido = async (pedidoId, nuevoEstado) => {
  try {
    const { data } = await axios.put(`${PEDIDOS_URL}/${pedidoId}/estado`, {
      estado: nuevoEstado,
    });
    return data;
  } catch (error) {
    console.error("Error al actualizar estado del pedido:", error);
    throw error;
  }
};

export const actualizarEstadoPedidoForzado = async (
  pedidoId,
  nuevoEstado,
  password
) => {
  try {
    const { data } = await axios.put(
      `${PEDIDOS_URL}/${pedidoId}/estado/forzado`,
      {
        estado: nuevoEstado,
        password: password,
      }
    );
    return data;
  } catch (error) {
    console.error("Error al actualizar estado forzado del pedido:", error);
    throw error;
  }
};

export const getEstadosDisponibles = async () => {
  try {
    const { data } = await axios.get(`${PEDIDOS_URL}/estados`);
    return data;
  } catch (error) {
    console.error("Error al obtener estados disponibles:", error);
    throw error;
  }
};

export const getTransicionesDisponibles = async (estadoActual) => {
  try {
    const { data } = await axios.get(
      `${PEDIDOS_URL}/transiciones/${encodeURIComponent(estadoActual)}`
    );
    return data;
  } catch (error) {
    console.error("Error al obtener transiciones disponibles:", error);
    throw error;
  }
};

export const getAllOrders = getAllPedidos;
