import React, { useState, useEffect } from "react";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import checkoutService from "../services/checkoutService";
import { toast } from "react-toastify";
import AuthRequiredModal from "../components/AuthRequiredModal";
import usePayPalScript from "../hooks/usePayPalScript";
import PayPalCheckoutButton from "../components/PayPalCheckoutButton";
import { getPayPalClientId } from "../config/paypal";
import "../styles/Checkout.css";
import PlinQR from "../assets/metodo_pago/PlinQR.jpeg";
import YapeQR from "../assets/metodo_pago/YapeQR.jpeg";

const CheckoutSimple = () => {
  const {
    cart,
    getTotalPrice,
    clearCart,
    validateCartStock,
    syncCartWithStock,
    validateAuthForCheckout,
  } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  // Estados principales del formulario
  const [datos, setDatos] = useState({
    distrito: "",
    direccion: "",
    referencia: "",
    tarjeta: "",
    titular: "",
    vencimiento: "",
    cvv: "",
    documento: "",
    tipoComprobante: "boleta",
    metodoPago: "tarjeta",
  });

  // Estados de UI y validación
  const [procesando, setProcesando] = useState(false);
  const [error, setError] = useState("");
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [authModalData, setAuthModalData] = useState({});

  // Validar autenticación al cargar la página
  useEffect(() => {
    const authValidation = validateAuthForCheckout();

    if (!authValidation.isValid) {
      setAuthModalData({
        message: authValidation.message,
        action: authValidation.action,
      });
      setShowAuthModal(true);
    }
  }, [validateAuthForCheckout]);

  usePayPalScript(getPayPalClientId());
  // Constantes de datos
  const distritos = [
    "Ancón",
    "Ate",
    "Barranco",
    "Breña",
    "Carabayllo",
    "Chaclacayo",
    "Chorrillos",
    "Cieneguilla",
    "Comas",
    "El Agustino",
    "Independencia",
    "Jesús María",
    "La Molina",
    "La Victoria",
    "Lima",
    "Lince",
    "Los Olivos",
    "Lurigancho",
    "Lurín",
    "Magdalena del Mar",
    "Miraflores",
    "Pachacámac",
    "Pucusana",
    "Pueblo Libre",
    "Puente Piedra",
    "Punta Hermosa",
    "Punta Negra",
    "Rímac",
    "San Bartolo",
    "San Borja",
    "San Isidro",
    "San Juan de Lurigancho",
    "San Juan de Miraflores",
    "San Luis",
    "San Martín de Porres",
    "San Miguel",
    "Santa Anita",
    "Santa María del Mar",
    "Santa Rosa",
    "Santiago de Surco",
    "Surquillo",
    "Villa El Salvador",
    "Villa María del Triunfo",
  ];

  // Cálculos de precio
  const productos = Object.values(cart);
  const subtotal = getTotalPrice() || 0;
  const costoEnvio = datos.distrito && datos.direccion ? 10.0 : 0;
  const igv = 0; // IGV ya está incluido en los precios de productos
  const total = subtotal + costoEnvio;

  // Estados de validación
  const hasAddress = datos.distrito && datos.direccion.trim();
  const hasValidDocument =
    datos.documento &&
    ((datos.tipoComprobante === "boleta" && /^\d{8}$/.test(datos.documento)) ||
      (datos.tipoComprobante === "factura" &&
        /^\d{11}$/.test(datos.documento)));
  const isFormValid =
    hasAddress && datos.tipoComprobante && hasValidDocument && datos.metodoPago;

  // Funciones de manejo de formulario
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setDatos((prev) => {
      const newDatos = {
        ...prev,
        [name]: type === "checkbox" ? checked : value,
      };

      // Si cambia el documento, verificar si se mantiene válido
      if (name === "documento") {
        const newHasValidDocument =
          value &&
          ((newDatos.tipoComprobante === "boleta" && /^\d{8}$/.test(value)) ||
            (newDatos.tipoComprobante === "factura" && /^\d{11}$/.test(value)));

        // Si el documento se vuelve inválido, resetear método de pago
        if (!newHasValidDocument && newDatos.metodoPago) {
          newDatos.metodoPago = "";
        }
      }

      return newDatos;
    });
  };

  const handleComprobanteChange = (tipo) => {
    setDatos((prev) => ({
      ...prev,
      tipoComprobante: tipo,
      documento: "",
      metodoPago: "", // Resetear método de pago al cambiar tipo de comprobante
    }));
  };

  const handleMetodoPagoChange = (metodo) => {
    // Solo permitir cambio si hay documento válido
    if (!hasValidDocument) {
      toast.warning("Complete primero los datos del comprobante");
      return;
    }

    setDatos((prev) => ({
      ...prev,
      metodoPago: metodo,
    }));
  };

  // Función para manejar el éxito del pago de PayPal
  const handlePayPalSuccess = async (paypalData) => {
    try {
      setProcesando(true);

      const checkoutData = {
        usuarioId: user.id,
        direccionEntrega: datos.direccion,
        distrito: datos.distrito,
        referencia: datos.referencia || "",
        notas: "",
        necesitaFactura: datos.tipoComprobante === "factura",
        subtotal: subtotal,
        costoEnvio: costoEnvio,
        igv: igv,
        total: total,
        fechaEntrega: new Date(
          Date.now() + 3 * 24 * 60 * 60 * 1000
        ).toISOString(),

        metodoPago: "paypal",
        montoPago: total,
        tipoComprobante: datos.tipoComprobante,
        documento: datos.documento,

        // Datos específicos de PayPal
        paypalId: paypalData.paypalId,
        paypalEmail: paypalData.paypalEmail,
        payerId: paypalData.payerId,

        items: productos.map((prod) => ({
          productoId: prod.id,
          nombre: prod.nombre,
          cantidad: prod.quantity,
          precioUnitario: prod.precioVenta || prod.precio,
          personalizacion: prod.comentarios || "",
        })),
      };

      console.log("Enviando checkoutData con PayPal:", checkoutData);

      const response = await checkoutService.procesarCheckout(checkoutData);

      if (response.exitoso) {
        clearCart();
        toast.success("¡Pago con PayPal completado exitosamente!");
        navigate("/pago-exitoso", {
          state: {
            pedidoData: response,
            metodoPago: "paypal",
            paypalData: paypalData,
          },
        });
      } else {
        throw new Error(
          response.mensaje || "Error al procesar el pedido con PayPal"
        );
      }
    } catch (err) {
      console.error("❌ Error en checkout con PayPal:", err);
      setError("Error procesando el pago con PayPal: " + err.message);
      throw err; // Re-lanzar para que PayPalCheckoutButton pueda manejar el estado
    } finally {
      setProcesando(false);
    }
  };

  // Función para manejar errores de PayPal
  const handlePayPalError = (error) => {
    console.error("❌ Error de PayPal manejado:", error);
    setError("Error de PayPal: " + (error.message || "Error desconocido"));
  };

  // Función principal de submit
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setProcesando(true);

    try {
      // 1. Validar autenticación
      if (!user || !user.id) {
        toast.error("Debe iniciar sesión para realizar el pedido");
        navigate("/login");
        return;
      }

      // 2. Validar carrito no vacío
      if (productos.length === 0) {
        toast.error("El carrito está vacío");
        return;
      }

      // 3. Validar datos de dirección
      if (!datos.distrito || datos.distrito.trim() === "") {
        toast.error("Debe seleccionar un distrito");
        setError("Debe seleccionar un distrito para continuar");
        return;
      }

      if (!datos.direccion || datos.direccion.trim() === "") {
        toast.error("Debe ingresar su dirección completa");
        setError("Debe ingresar su dirección completa para continuar");
        return;
      }

      // 4. Validar datos del comprobante
      if (!datos.tipoComprobante || datos.tipoComprobante.trim() === "") {
        toast.error("Debe seleccionar el tipo de comprobante");
        setError("Debe seleccionar si desea boleta o factura");
        return;
      }

      if (!datos.documento || datos.documento.trim() === "") {
        const tipoDoc = datos.tipoComprobante === "boleta" ? "DNI" : "RUC";
        toast.error(`Debe ingresar su ${tipoDoc}`);
        setError(`Debe ingresar su ${tipoDoc} para el comprobante`);
        return;
      }

      // 5. Validar formato del documento
      if (datos.tipoComprobante === "boleta") {
        if (datos.documento.length !== 8 || !/^\d{8}$/.test(datos.documento)) {
          toast.error("El DNI debe tener 8 dígitos");
          setError("El DNI debe tener exactamente 8 dígitos");
          return;
        }
      } else if (datos.tipoComprobante === "factura") {
        if (
          datos.documento.length !== 11 ||
          !/^\d{11}$/.test(datos.documento)
        ) {
          toast.error("El RUC debe tener 11 dígitos");
          setError("El RUC debe tener exactamente 11 dígitos");
          return;
        }
      }

      // 6. Validar método de pago
      if (!datos.metodoPago || datos.metodoPago.trim() === "") {
        toast.error("Debe seleccionar un método de pago");
        setError("Debe seleccionar un método de pago para continuar");
        return;
      }

      // 7. Validar stock
      toast.info("Verificando disponibilidad de productos...", {
        autoClose: 2000,
      });

      const stockErrors = await validateCartStock();
      if (stockErrors.length > 0) {
        const hasChanges = await syncCartWithStock();

        if (hasChanges) {
          toast.warning(
            "Tu carrito ha sido actualizado según la disponibilidad actual",
            { autoClose: 4000 }
          );
        }

        setError(`Problemas de stock detectados:\n${stockErrors.join("\n")}`);
        toast.error(
          "No se puede procesar el pedido debido a problemas de stock",
          { autoClose: 5000 }
        );
        return;
      }

      // 8. Preparar datos del checkout
      const checkoutData = {
        usuarioId: user.id,
        direccionEntrega: datos.direccion,
        distrito: datos.distrito,
        referencia: datos.referencia || "",
        notas: "",
        necesitaFactura: datos.tipoComprobante === "factura",
        subtotal: subtotal,
        costoEnvio: costoEnvio,
        igv: igv,
        total: total,
        fechaEntrega: new Date(
          Date.now() + 3 * 24 * 60 * 60 * 1000
        ).toISOString(),

        // Datos del pago
        metodoPago: datos.metodoPago,
        montoPago: total,

        // Datos del comprobante
        tipoComprobante: datos.tipoComprobante,
        documento: datos.documento,

        // Items del carrito
        items: productos.map((prod) => ({
          productoId: prod.id,
          nombre: prod.nombre,
          cantidad: prod.quantity,
          precioUnitario: prod.precioVenta || prod.precio,
          personalizacion: prod.comentarios || "",
        })),
      };

      console.log("Enviando checkout data:", checkoutData);

      // 9. Procesar checkout
      const response = await checkoutService.procesarCheckout(checkoutData);

      if (response.exitoso) {
        clearCart();
        toast.success("¡Pedido procesado exitosamente!");
        navigate("/pago-exitoso", {
          state: {
            pedidoData: response,
            metodoPago: datos.metodoPago,
          },
        });
      } else {
        const mensajeError = response.mensaje || "Error procesando el pedido";
        setError(mensajeError);
        showStockErrorToast(mensajeError);
      }
    } catch (error) {
      console.error("Error en checkout:", error);
      const mensajeError = error.mensaje || "Error de conexión con el servidor";
      setError(mensajeError);
      showStockErrorToast(mensajeError);
    } finally {
      setProcesando(false);
    }
  };

  // Función auxiliar para mostrar errores de stock
  const showStockErrorToast = (mensaje) => {
    if (
      mensaje.toLowerCase().includes("stock insuficiente") ||
      mensaje.toLowerCase().includes("no disponible") ||
      mensaje.toLowerCase().includes("agotado")
    ) {
      toast.error(`🚫 ${mensaje}`, {
        autoClose: 5000,
        style: {
          backgroundColor: "#f8d7da",
          color: "#721c24",
          border: "1px solid #e74c3c",
        },
      });
    } else {
      toast.error(mensaje);
    }
  };

  // Componentes auxiliares para renderizado
  const renderErrorAlert = () => {
    if (!error) return null;

    const isStockError =
      error.toLowerCase().includes("stock") ||
      error.toLowerCase().includes("disponible") ||
      error.toLowerCase().includes("agotado");

    return (
      <div
        className={`alert ${
          isStockError ? "alert-warning stock-error" : "alert-danger"
        }`}
        role="alert">
        <div className="error-content">
          <strong>
            {isStockError ? "⚠️ Problema de inventario:" : "❌ Error:"}
          </strong>
          <div className="error-message">{error}</div>
          {isStockError && (
            <small className="error-suggestion">
              💡 Sugerencia: Revisa las cantidades en tu carrito o actualiza la
              página.
            </small>
          )}
        </div>
      </div>
    );
  };

  const renderShippingSection = () => (
    <div className="checkout-section">
      <h2>📦 Detalles de Envío</h2>
      <p className="info-envio-text">
        📍 Realizamos entregas únicamente en Lima Metropolitana
      </p>

      <div className="form-row">
        <select
          name="distrito"
          value={datos.distrito}
          onChange={handleChange}
          required
          className="select-full-width">
          <option value="">Seleccionar Distrito de Lima</option>
          {distritos.map((distrito) => (
            <option key={distrito} value={distrito}>
              {distrito}
            </option>
          ))}
        </select>
      </div>

      <div className="form-row">
        <input
          type="text"
          name="direccion"
          placeholder="Ingrese su Dirección Completa"
          value={datos.direccion}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-row">
        <input
          type="text"
          name="referencia"
          placeholder="Referencia (opcional) - Ej: Casa verde, puerta negra"
          value={datos.referencia}
          onChange={handleChange}
        />
      </div>
    </div>
  );

  const renderComprobanteSection = () => (
    <div className="checkout-section">
      <h3>📄 Comprobante de Pago</h3>
      {!hasAddress && (
        <div className="alert alert-info">
          <small>
            📍 Primero complete los datos de envío para seleccionar el
            comprobante
          </small>
        </div>
      )}
      <div className="form-row">
        <div
          className={`comprobante-option ${
            datos.tipoComprobante === "boleta" ? "active" : ""
          } ${!hasAddress ? "disabled" : ""}`}
          onClick={() => hasAddress && handleComprobanteChange("boleta")}>
          <h5>📋 Boleta de Venta</h5>
          <p>Para consumo personal</p>
          <small>Requiere DNI</small>
        </div>
        <div
          className={`comprobante-option ${
            datos.tipoComprobante === "factura" ? "active" : ""
          } ${!hasAddress ? "disabled" : ""}`}
          onClick={() => hasAddress && handleComprobanteChange("factura")}>
          <h5>🏢 Factura</h5>
          <p>Para empresas</p>
          <small>Requiere RUC</small>
        </div>
      </div>

      <div className="form-row">
        <input
          type="number"
          name="documento"
          placeholder={
            !hasAddress
              ? "Complete primero los datos de envío"
              : datos.tipoComprobante === "boleta"
              ? "Ingrese su DNI (8 dígitos)"
              : "Ingrese su RUC (11 dígitos)"
          }
          value={datos.documento}
          onChange={handleChange}
          maxLength={datos.tipoComprobante === "boleta" ? 8 : 11}
          disabled={!hasAddress}
          required
        />
      </div>
    </div>
  );

  const renderPaymentSection = () => (
    <div className="checkout-section">
      <h3>💳 Métodos de Pago</h3>
      {!hasValidDocument && (
        <div className="alert alert-info">
          <small>
            📄 Complete los datos del comprobante para seleccionar el método de
            pago
          </small>
        </div>
      )}

      <div className="form-row">
        {[
          { key: "paypal", icon: "🅿️", label: "PayPal" },
          { key: "yape", icon: "📱", label: "Yape" },
          { key: "plin", icon: "💰", label: "Plin" },
        ].map((metodo) => (
          <div
            key={metodo.key}
            className={`comprobante-option ${
              datos.metodoPago === metodo.key ? "active" : ""
            } ${!hasValidDocument ? "disabled" : ""}`}
            onClick={() =>
              hasValidDocument && handleMetodoPagoChange(metodo.key)
            }>
            <h5>
              {metodo.icon} {metodo.label}
            </h5>
          </div>
        ))}
      </div>

      {/* 🅿️ Pago con PayPal */}
      {datos.metodoPago === "paypal" && hasValidDocument && (
        <PayPalCheckoutButton
          total={total}
          checkoutData={{
            usuarioId: user.id,
            direccionEntrega: datos.direccion,
            distrito: datos.distrito,
            referencia: datos.referencia,
            tipoComprobante: datos.tipoComprobante,
            documento: datos.documento,
          }}
          onSuccess={handlePayPalSuccess}
          onError={handlePayPalError}
          disabled={procesando || productos.length === 0 || !isFormValid}
        />
      )}

      {/* 📱 Yape */}
      {datos.metodoPago === "yape" && hasValidDocument && (
        <div className="payment-info-card">
          <h4>📱 Instrucciones para Yape</h4>
          <p>1. Abre tu app Yape</p>
          <p>
            2. Escanea el código QR o envía a: <strong>123-456-789</strong>
          </p>
          <p>
            3. Monto: <strong>S/. {total.toFixed(2)}</strong>
          </p>
          <div className="qr-placeholder">
            <img src={YapeQR} alt="Código QR Yape" className="qr-code" />
          </div>
        </div>
      )}

      {/* 💰 Plin */}
      {datos.metodoPago === "plin" && hasValidDocument && (
        <div className="payment-info-card">
          <h4>💰 Instrucciones para Plin</h4>
          <p>1. Abre tu app Plin</p>
          <p>
            2. Escanea el código QR o envía a: <strong>123-456-789</strong>
          </p>
          <p>
            3. Monto: <strong>S/. {total.toFixed(2)}</strong>
          </p>
          <div className="qr-placeholder">
            <img src={PlinQR} alt="Código QR PLIN" className="qr-code" />
          </div>
        </div>
      )}
    </div>
  );

  const renderOrderSummary = () => (
    <div className="checkout-summary">
      <h3>📋 Resumen del Pedido</h3>
      {productos.length === 0 ? (
        <div className="carrito-vacio-message">
          <p>🛒 No hay productos en el carrito</p>
        </div>
      ) : (
        <>
          {productos.map((prod) => (
            <div key={prod.id} className="resumen-producto-item">
              <div>
                <div className="resumen-producto-name">{prod.nombre}</div>
                <small className="resumen-producto-quantity">
                  {prod.quantity} x S/.{" "}
                  {(prod.precioVenta || prod.precio).toFixed(2)}
                </small>
                {prod.comentarios && (
                  <div className="producto-nota">💬 {prod.comentarios}</div>
                )}
              </div>
              <div className="resumen-producto-total">
                S/.{" "}
                {((prod.precioVenta || prod.precio) * prod.quantity).toFixed(2)}
              </div>
            </div>
          ))}

          <div className="resumen-totales">
            <div className="resumen-line">
              <span>Subtotal:</span>
              <span>S/. {subtotal.toFixed(2)}</span>
            </div>
            <div className="resumen-line">
              <span>Envío a {datos.distrito || "Lima"}:</span>
              <span>
                {costoEnvio > 0 ? `S/. ${costoEnvio.toFixed(2)}` : "Gratis"}
              </span>
            </div>
            <div className="resumen-total-final">
              <strong>Total a Pagar:</strong>
              <strong>S/. {total.toFixed(2)}</strong>
            </div>
          </div>
        </>
      )}
    </div>
  );

  return (
    <div className="checkout-container">
      <div className="checkout-form">
        <h1>🛒 Finalizar Compra</h1>

        {renderErrorAlert()}

        <form onSubmit={handleSubmit}>
          {renderShippingSection()}
          {renderComprobanteSection()}
          {renderPaymentSection()}

          <button
            type="submit"
            className={`finalizar-btn ${procesando ? "procesando" : ""} ${
              !isFormValid ? "disabled" : ""
            }`}
            disabled={procesando || productos.length === 0 || !isFormValid}>
            {procesando
              ? "⏳ Procesando Pedido..."
              : !isFormValid
              ? "⚠️ Complete todos los datos"
              : "✅ Confirmar y Pagar"}
          </button>
        </form>
      </div>

      {renderOrderSummary()}

      {/* Modal de autenticación requerida */}
      <AuthRequiredModal
        isOpen={showAuthModal}
        onClose={() => {
          setShowAuthModal(false);
          navigate("/carrito");
        }}
        message={authModalData.message}
        action={authModalData.action}
      />
    </div>
  );
};

export default CheckoutSimple;
