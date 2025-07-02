// config/paypal.js
export const PAYPAL_CONFIG = {
  // Client ID para desarrollo (sandbox)
  CLIENT_ID_SANDBOX: "Ad9hQoI_7QEPjeKHvmJpOwNbM3l7-svfCZKpU2BBaPuY9FngdUnpBcRoGx5izWeNdFpGrhQ-PPmmmXF9",
  
  // Client ID para producción (se debe configurar cuando se despliegue)
  CLIENT_ID_PRODUCTION: "TU_CLIENT_ID_DE_PRODUCCION",
  
  // Configuración del SDK
  SDK_OPTIONS: {
    currency: "USD",
    intent: "capture",
    "disable-funding": "credit,card", // Deshabilitar tarjetas de crédito si solo quieres PayPal
  },
  
  // URLs base
  SANDBOX_URL: "https://api.sandbox.paypal.com",
  PRODUCTION_URL: "https://api.paypal.com",
  
  // Configuración de la aplicación
  APPLICATION_CONTEXT: {
    shipping_preference: "NO_SHIPPING",
    user_action: "PAY_NOW",
    brand_name: "Como en Casa - Pastelería",
    landing_page: "LOGIN"
  }
};

// Función para obtener el client ID según el entorno
export const getPayPalClientId = () => {
  const isProduction = process.env.NODE_ENV === 'production';
  return isProduction ? PAYPAL_CONFIG.CLIENT_ID_PRODUCTION : PAYPAL_CONFIG.CLIENT_ID_SANDBOX;
};

// Función para obtener la configuración del SDK
export const getPayPalSDKUrl = (clientId) => {
  const options = new URLSearchParams({
    'client-id': clientId,
    ...PAYPAL_CONFIG.SDK_OPTIONS
  });
  
  return `https://www.paypal.com/sdk/js?${options.toString()}`;
};

// Función para verificar si estamos en modo desarrollo
export const isPayPalSandbox = () => {
  return process.env.NODE_ENV !== 'production';
};

export default PAYPAL_CONFIG;
