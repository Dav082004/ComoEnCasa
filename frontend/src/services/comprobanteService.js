import axios from 'axios';


const API_URL = 'http://localhost:8081/api/admin/comprobantes';

export const generarComprobante = async (pedidoId, tipo) => {
  const { data } = await axios.post(
    `${API_URL}/generate`,
    null,
    { params: { pedidoId, tipo } }
  );
  return data;
};

export const listarComprobantes = async (filtros = {}) => {
  
  const params = {};
  Object.entries(filtros).forEach(([key, value]) => {
    if (value) params[key] = value;
  });
  const { data } = await axios.get(API_URL, { params });
  return data;
};

export const exportarExcel = async (id) => {
  const res = await axios.get(`${API_URL}/${id}/export.xlsx`, {
    responseType: 'blob'
  });
  return res.data;
};

export const exportarPdf = async (id) => {
  const res = await axios.get(`${API_URL}/${id}/export.pdf`, {
    responseType: 'blob'
  });
  return res.data;
};

export const exportarReporteVentas = async () => {
  const token = localStorage.getItem("token"); // O como lo guardes

  const res = await axios.get(
    "http://localhost:8081/api/admin/comprobantes/reporte-ventas.xlsx",
    {
      responseType: "blob",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return res.data;
};



