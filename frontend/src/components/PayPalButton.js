// src/components/PayPalButton.jsx
import React, { useEffect, useRef } from "react";

const PayPalButton = ({ total, onSuccess }) => {
  const paypalRef = useRef();

  useEffect(() => {
    if (!window.paypal) return;

    window.paypal.Buttons({
      style: {
        layout: "vertical",
        color: "blue",
        shape: "pill",
        label: "pay",
      },
      createOrder: (data, actions) => {
        return actions.order.create({
          purchase_units: [
            {
              amount: {
                value: total.toFixed(2),
              },
            },
          ],
        });
      },
      onApprove: async (data, actions) => {
        const order = await actions.order.capture();
        console.log("✅ Pago aprobado:", order);
        onSuccess(order); // callback a tu lógica de pedido
      },
      onError: (err) => {
        console.error("❌ Error de PayPal:", err);
        alert("Error procesando pago con PayPal.");
      },
    }).render(paypalRef.current);
  }, [total, onSuccess]);

  return <div ref={paypalRef} />;
};

export default PayPalButton;
