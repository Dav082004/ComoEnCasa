import axios from "axios";

const API_URL = "http://localhost:8081/api/admin/comprobantes/reporte";

export const exportarReporteVentas = async (desde, hasta) => {
  const params = {};
  if (desde) params.desde = desde;
  if (hasta) params.hasta = hasta;

  const res = await axios.get(`${API_URL}/ventas`, {
    params,
    responseType: "blob",
  });

  return res.data;
};
