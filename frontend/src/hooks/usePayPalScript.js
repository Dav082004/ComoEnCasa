// hooks/usePayPalScript.js
import { useEffect } from "react";
import { getPayPalSDKUrl, isPayPalSandbox } from "../config/paypal";

const usePayPalScript = (clientId) => {
  useEffect(() => {
    const scriptId = "paypal-sdk";
    const existingScript = document.getElementById(scriptId);

    if (!existingScript && clientId) {
      const script = document.createElement("script");
      script.src = getPayPalSDKUrl(clientId);
      script.id = scriptId;
      script.async = true;
      script.onload = () => {
        const environment = isPayPalSandbox() ? "sandbox" : "production";
        console.log(`PayPal SDK cargado (${environment}) - Moneda: USD`);
      };
      script.onerror = () => {
        console.error("Error cargando PayPal SDK");
      };
      document.body.appendChild(script);
    }

    // Cleanup function
    return () => {
      // No remover el script para evitar recargas innecesarias
      // El script de PayPal puede ser reutilizado
    };
  }, [clientId]);
};

export default usePayPalScript;
