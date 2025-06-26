import axios from 'axios';

const API_URL = 'http://localhost:8081/api/admin/facturas';

export const generarFactura = async (pedidoId, tipo) => {
  const { data } = await axios.post(
    `${API_URL}/generate`,
    null,
    { params: { pedidoId, tipo } }
  );
  return data;
};

export const listarFacturas = async (filtros = {}) => {
  const params = {};
  Object.entries(filtros).forEach(([key, value]) => {
    if (value) params[key] = value;
  });
  const { data } = await axios.get(API_URL, { params });
  return data;
};

export const exportarFacturaExcel = async (id) => {
  const res = await axios.get(`${API_URL}/${id}/export.xlsx`, {
    responseType: 'blob'
  });
  return res.data;
};

export const exportarFacturaPdf = async (id) => {
  const res = await axios.get(`${API_URL}/${id}/export.pdf`, {
    responseType: 'blob'
  });
  return res.data;
};
