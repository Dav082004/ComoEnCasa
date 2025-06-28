// hooks/usePayPalScript.js
import { useEffect } from "react";

const usePayPalScript = (clientId) => {
  useEffect(() => {
    const scriptId = "paypal-sdk";
    const existingScript = document.getElementById(scriptId);

    if (!existingScript) {
      const script = document.createElement("script");
      script.src = `https://www.paypal.com/sdk/js?client-id=${clientId}&currency=USD`;
      script.id = scriptId;
      script.async = true;
      script.onload = () => {
        console.log("✅ PayPal SDK cargado");
      };
      document.body.appendChild(script);
    }
  }, [clientId]);
};

export default usePayPalScript;
