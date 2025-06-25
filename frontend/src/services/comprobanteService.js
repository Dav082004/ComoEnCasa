import { adminApi, publicApi } from "./apiConfig";

export const generarComprobante = async (pedidoId, tipo) => {
  const { data } = await adminApi.post("/comprobantes/generate", null, {
    params: { pedidoId, tipo },
  });
  return data;
};

export const listarComprobantes = async (filtros = {}) => {
  const params = {};
  Object.entries(filtros).forEach(([key, value]) => {
    if (value) params[key] = value;
  });
  const { data } = await adminApi.get("/comprobantes", { params });
  return data;
};

export const exportarExcel = async (id) => {
  const res = await adminApi.get(`/comprobantes/${id}/export.xlsx`, {
    responseType: "blob",
  });
  return res.data;
};

export const exportarPdf = async (id) => {
  const res = await adminApi.get(`/comprobantes/${id}/export.pdf`, {
    responseType: "blob",
  });
  return res.data;
};

export const exportarReporteVentas = async (filtros = {}) => {
  const params = {};
  Object.entries(filtros).forEach(([key, value]) => {
    if (value) params[key] = value;
  });

  console.log("🚀 Exportando reporte de ventas con filtros:", params);

  try {
    // Usar el nuevo endpoint de reportes sin autenticación
    const res = await publicApi.get("/reportes/ventas.xlsx", {
      params,
      responseType: "blob",
    });

    console.log("✅ Reporte generado exitosamente");
    return res.data;
  } catch (error) {
    console.error("❌ Error al exportar reporte:", error);

    // Si falla con el nuevo endpoint, intentar con el original
    console.log("🔄 Intentando con endpoint alternativo...");
    const res = await adminApi.get("/comprobantes/reporte-ventas.xlsx", {
      params,
      responseType: "blob",
    });
    return res.data;
  }
};

// Nueva función específica para exportar solo facturas
export const exportarReporteFacturas = async (filtros = {}) => {
  const params = {};
  Object.entries(filtros).forEach(([key, value]) => {
    if (value) params[key] = value;
  });

  console.log("🚀 Exportando reporte de FACTURAS con filtros:", params);

  try {
    const res = await publicApi.get("/reportes/facturas.xlsx", {
      params,
      responseType: "blob",
    });

    console.log("✅ Reporte de facturas generado exitosamente");
    return res.data;
  } catch (error) {
    console.error("❌ Error al exportar reporte de facturas:", error);
    throw error;
  }
};

// Nueva función específica para exportar solo boletas
export const exportarReporteBoletas = async (filtros = {}) => {
  const params = {};
  Object.entries(filtros).forEach(([key, value]) => {
    if (value) params[key] = value;
  });

  console.log("🚀 Exportando reporte de BOLETAS con filtros:", params);

  try {
    const res = await publicApi.get("/reportes/boletas.xlsx", {
      params,
      responseType: "blob",
    });

    console.log("✅ Reporte de boletas generado exitosamente");
    return res.data;
  } catch (error) {
    console.error("❌ Error al exportar reporte de boletas:", error);
    throw error;
  }
};

// Nueva función para probar la conectividad
export const testReportesEndpoint = async () => {
  try {
    const { data } = await publicApi.get("/reportes/test");
    console.log("✅ Test endpoint:", data);
    return data;
  } catch (error) {
    console.error("❌ Error en test endpoint:", error);
    throw error;
  }
};
