import React, { useState } from "react";
import { PayPalButtons } from "@paypal/react-paypal-js";
import { toast } from "react-toastify";
import {
  PAYPAL_CONFIG,
  convertPenToUsd,
  getConversionText,
} from "../config/paypal";

const PayPalCheckoutButton = ({
  total,
  checkoutData,
  onSuccess,
  onError,
  disabled = false,
}) => {
  const [processing, setProcessing] = useState(false);

  const createOrder = (data, actions) => {
    const usdAmount = convertPenToUsd(total);
    console.log("Creando orden de PayPal...", {
      totalPEN: total.toFixed(2),
      totalUSD: usdAmount,
    });

    return actions.order.create({
      purchase_units: [
        {
          amount: {
            value: usdAmount,
            currency_code: "USD", // PayPal requiere USD
          },
          description: `Pedido de pastelería - Como en Casa (S/. ${total.toFixed(
            2
          )} PEN)`,
        },
      ],
      application_context: PAYPAL_CONFIG.APPLICATION_CONTEXT,
    });
  };

  const onApprove = async (data, actions) => {
    if (processing) return; // Evitar doble procesamiento

    try {
      setProcessing(true);
      console.log("PayPal onApprove iniciado...", data);

      const details = await actions.order.capture();
      console.log("Detalles de PayPal capturados:", details);

      // Verificar que tenemos los datos necesarios de PayPal
      if (!details.id || !details.payer) {
        throw new Error("Datos incompletos recibidos de PayPal");
      }

      // Verificar que el pago fue aprobado
      if (details.status !== "COMPLETED") {
        throw new Error(`Pago no completado. Estado: ${details.status}`);
      }

      // Preparar datos completos para el checkout
      const paypalData = {
        paypalId: details.id,
        paypalEmail: details.payer.email_address,
        payerId: details.payer.payer_id,
        transactionId: details.purchase_units[0]?.payments?.captures[0]?.id,
        status: details.status,
      };

      console.log("Datos de PayPal preparados:", paypalData);

      // Llamar al callback de éxito con los datos de PayPal
      await onSuccess(paypalData);
    } catch (error) {
      console.error("Error procesando pago de PayPal:", error);
      setProcessing(false);

      // Mostrar error específico
      const errorMessage = error.message || "Error desconocido en PayPal";
      toast.error(`Error en PayPal: ${errorMessage}`);

      if (onError) {
        onError(error);
      }
    }
  };

  const onErrorHandler = (err) => {
    console.error("Error de PayPal SDK:", err);
    setProcessing(false);

    // Filtrar errores comunes que no son críticos
    const isCommonError =
      err.message &&
      (err.message.includes("Target window is closed") ||
        err.message.includes("postrobot_method") ||
        err.message.includes("ERR_BLOCKED_BY_CLIENT") ||
        err.message.includes("disableSetCookie") ||
        err.message.includes("logger/api/logger"));

    // No mostrar toast para errores comunes de red/comunicación
    if (!isCommonError) {
      const errorMessage = err.message || "Error en el SDK de PayPal";
      toast.error(`Error de PayPal: ${errorMessage}`);

      if (onError) {
        onError(err);
      }
    } else {
      // Para errores comunes, solo log en consola (ya no enviamos al callback de error)
      console.warn("Error común de PayPal ignorado:", err.message);
    }
  };

  const onCancel = (data) => {
    console.log("Pago de PayPal cancelado por el usuario:", data);
    setProcessing(false);
    toast.info("Pago cancelado");
  };

  return (
    <div className="paypal-checkout-container">
      <div className="payment-info-card mb-3">
        <h4>PayPal</h4>
        <p>Paga de forma segura con tu cuenta PayPal</p>
        <p>Tus datos están protegidos por PayPal</p>
        <small className="text-muted">
          Total a pagar: <strong>{getConversionText(total)}</strong>
        </small>
        <small className="text-info d-block mt-1">
          * PayPal procesará el pago en USD al tipo de cambio actual
        </small>
      </div>

      {processing && (
        <div className="text-center mb-3">
          <div
            className="spinner-border spinner-border-sm text-primary"
            role="status">
            <span className="visually-hidden">Procesando...</span>
          </div>
          <p className="mt-2">Procesando pago con PayPal...</p>
        </div>
      )}

      <PayPalButtons
        style={{
          layout: "vertical",
          color: "blue",
          shape: "rect",
          label: "pay",
          height: 40,
        }}
        disabled={disabled || processing}
        forceReRender={[total]}
        createOrder={createOrder}
        onApprove={onApprove}
        onError={onErrorHandler}
        onCancel={onCancel}
      />
    </div>
  );
};

export default PayPalCheckoutButton;
