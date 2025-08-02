/**
 * Configuración de la aplicación Como en Casa
 * Soporta múltiples entornos: desarrollo, producción y Docker
 */

const getApiBaseUrl = () => {
  // Si existe variable de entorno, usarla (para Docker/producción)
  if (process.env.REACT_APP_API_URL) {
    return process.env.REACT_APP_API_URL;
  }

  // Detección automática para desarrollo local
  const { hostname, protocol } = window.location;

  if (hostname === "localhost" || hostname === "127.0.0.1") {
    return `${protocol}//localhost:8080`;
  }

  // Para producción, usar el mismo host con puerto 8080
  return `${protocol}//${hostname}:8080`;
};

const config = {
  apiBaseUrl: getApiBaseUrl(),
  version: "1.0.0",
  appName: "Como en Casa",
};

// Configuración para desarrollo
if (process.env.NODE_ENV === "development") {
  console.log("🔧 Config loaded:", config);
}

export default config;
