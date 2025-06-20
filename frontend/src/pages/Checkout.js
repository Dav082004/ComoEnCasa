import React, { useState, useEffect } from "react";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import checkoutService from "../services/checkoutService";
import { toast } from "react-toastify";
import AuthRequiredModal from "../components/AuthRequiredModal";
import "../styles/Checkout.css";

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

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setDatos((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setProcesando(true);

    try {
      // Validar que el usuario esté autenticado
      if (!user || !user.id) {
        toast.error("Debe iniciar sesión para realizar el pedido");
        navigate("/login");
        return;
      }

      // Validar que haya productos en el carrito
      if (productos.length === 0) {
        toast.error("El carrito está vacío");
        return;
      }

      // ✅ NUEVA VALIDACIÓN: Verificar stock antes del checkout
      toast.info("🔍 Verificando disponibilidad de productos...", {
        autoClose: 2000,
      });

      const stockErrors = await validateCartStock();
      if (stockErrors.length > 0) {
        // Intentar sincronizar carrito con stock disponible
        const hasChanges = await syncCartWithStock();

        if (hasChanges) {
          toast.warning(
            "⚠️ Tu carrito ha sido actualizado según la disponibilidad actual",
            {
              autoClose: 4000,
            }
          );
        }

        // Mostrar errores específicos de stock
        setError(`Problemas de stock detectados:\n${stockErrors.join("\n")}`);
        toast.error(
          "❌ No se puede procesar el pedido debido a problemas de stock",
          {
            autoClose: 5000,
          }
        );
        return;
      }

      // Preparar datos del checkout
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
        ).toISOString(), // 3 días desde ahora

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

      // Procesar checkout
      const response = await checkoutService.procesarCheckout(checkoutData);

      if (response.exitoso) {
        // Limpiar carrito
        clearCart();

        // Mostrar mensaje de éxito
        toast.success("¡Pedido procesado exitosamente!");

        // Redirigir a página de éxito con datos del pedido
        navigate("/pago-exitoso", {
          state: {
            pedidoData: response,
            metodoPago: datos.metodoPago,
          },
        });
      } else {
        const mensajeError = response.mensaje || "Error procesando el pedido";
        setError(mensajeError);

        // Verificar si es un error de stock y mostrar mensaje específico
        if (
          mensajeError.toLowerCase().includes("stock insuficiente") ||
          mensajeError.toLowerCase().includes("no disponible") ||
          mensajeError.toLowerCase().includes("agotado")
        ) {
          toast.error(`❌ ${mensajeError}`, {
            autoClose: 5000,
            style: {
              backgroundColor: "#fff3cd",
              color: "#856404",
              border: "1px solid #ffeaa7",
            },
          });
        } else {
          toast.error(mensajeError);
        }
      }
    } catch (error) {
      console.error("Error en checkout:", error);
      const mensajeError = error.mensaje || "Error de conexión con el servidor";
      setError(mensajeError);

      // Verificar si es un error de stock en la excepción
      if (
        mensajeError.toLowerCase().includes("stock insuficiente") ||
        mensajeError.toLowerCase().includes("no disponible") ||
        mensajeError.toLowerCase().includes("agotado")
      ) {
        toast.error(`🚫 ${mensajeError}`, {
          autoClose: 5000,
          style: {
            backgroundColor: "#f8d7da",
            color: "#721c24",
            border: "1px solid #e74c3c",
          },
        });
      } else {
        toast.error(mensajeError);
      }
    } finally {
      setProcesando(false);
    }
  };

  const handleComprobanteChange = (tipo) => {
    setDatos((prev) => ({
      ...prev,
      tipoComprobante: tipo,
      documento: "",
    }));
  };

  const handleMetodoPagoChange = (metodo) => {
    setDatos((prev) => ({
      ...prev,
      metodoPago: metodo,
    }));
  };

  const productos = Object.values(cart);
  const subtotal = getTotalPrice() || 0;
  const costoEnvio = datos.distrito && datos.direccion ? 10.0 : 0;
  const igv = (subtotal + costoEnvio) * 0.18;
  const total = subtotal + costoEnvio + igv;

  return (
    <div className="checkout-container">
      <div className="checkout-form">
        <h1>🛒 Finalizar Compra</h1>

        {error && (
          <div
            className={`alert ${
              error.toLowerCase().includes("stock") ||
              error.toLowerCase().includes("disponible") ||
              error.toLowerCase().includes("agotado")
                ? "alert-warning stock-error"
                : "alert-danger"
            }`}
            role="alert">
            <div className="error-content">
              <strong>
                {error.toLowerCase().includes("stock") ||
                error.toLowerCase().includes("disponible") ||
                error.toLowerCase().includes("agotado")
                  ? "⚠️ Problema de inventario:"
                  : "❌ Error:"}
              </strong>
              <div className="error-message">{error}</div>
              {(error.toLowerCase().includes("stock") ||
                error.toLowerCase().includes("disponible") ||
                error.toLowerCase().includes("agotado")) && (
                <small className="error-suggestion">
                  💡 Sugerencia: Revisa las cantidades en tu carrito o actualiza
                  la página.
                </small>
              )}
            </div>
          </div>
        )}

        <h2>Detalles de Envío - Lima Metropolitana</h2>
        <p className="info-envio-text">
          📍 Realizamos entregas únicamente en Lima Metropolitana
        </p>

        <form onSubmit={handleSubmit}>
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
              placeholder="Ingrese su Dirección"
              value={datos.direccion}
              onChange={handleChange}
              required
            />
          </div>

          <h3>Comprobante de Pago</h3>
          <div className="form-row">
            <div
              className={`comprobante-option ${
                datos.tipoComprobante === "boleta" ? "active" : ""
              }`}
              onClick={() => handleComprobanteChange("boleta")}>
              <h5>Boleta de Venta</h5>
              <p>Para consumo personal</p>
              <small>Requiere DNI</small>
            </div>
            <div
              className={`comprobante-option ${
                datos.tipoComprobante === "factura" ? "active" : ""
              }`}
              onClick={() => handleComprobanteChange("factura")}>
              <h5>Factura</h5>
              <p>Para empresas</p>
              <small>Requiere RUC</small>
            </div>
          </div>

          <div className="form-row">
            <input
              type="text"
              name="documento"
              placeholder={
                datos.tipoComprobante === "boleta"
                  ? "Ingrese su DNI"
                  : "Ingrese su RUC"
              }
              value={datos.documento}
              onChange={handleChange}
              maxLength={datos.tipoComprobante === "boleta" ? 8 : 11}
              required
            />
          </div>

          <h3>Métodos de Pago</h3>
          <div className="form-row">
            {["tarjeta", "yape", "plin"].map((m) => (
              <div
                key={m}
                className={`comprobante-option ${
                  datos.metodoPago === m ? "active" : ""
                }`}
                onClick={() => handleMetodoPagoChange(m)}>
                <h5>
                  {m === "tarjeta" && "💳 Tarjeta"}
                  {m === "yape" && "📱 Yape"}
                  {m === "plin" && "💰 Plin"}
                </h5>
              </div>
            ))}
          </div>

          {datos.metodoPago === "tarjeta" && (
            <>
              <div className="form-row">
                <input
                  type="text"
                  name="tarjeta"
                  placeholder="Número de tarjeta"
                  value={datos.tarjeta}
                  onChange={handleChange}
                  maxLength="19"
                  required
                />
              </div>

              <div className="form-row">
                <input
                  type="text"
                  name="titular"
                  placeholder="Titular de la tarjeta"
                  value={datos.titular}
                  onChange={handleChange}
                  required
                />
                <input
                  type="text"
                  name="vencimiento"
                  placeholder="MM/YY"
                  value={datos.vencimiento}
                  onChange={handleChange}
                  maxLength="5"
                  required
                />
                <input
                  type="text"
                  name="cvv"
                  placeholder="CVV"
                  value={datos.cvv}
                  onChange={handleChange}
                  maxLength="4"
                  required
                />
              </div>
            </>
          )}

          <button
            type="submit"
            className={`finalizar-btn ${procesando ? "procesando" : ""}`}
            disabled={procesando}>
            {procesando ? "⏳ Procesando..." : "🛒 Finalizar Orden"}
          </button>
        </form>
      </div>

      <div className="checkout-summary">
        <h3>Resumen del Pedido</h3>
        {productos.length === 0 ? (
          <p>No hay productos en el carrito</p>
        ) : (
          productos.map((prod) => (
            <div key={prod.id} className="resumen-producto-item">
              <div>
                <div>{prod.nombre}</div>
                <small>
                  {prod.quantity} x S/.{" "}
                  {(prod.precioVenta || prod.precio).toFixed(2)}
                </small>
              </div>
              <div>
                S/.{" "}
                {((prod.precioVenta || prod.precio) * prod.quantity).toFixed(2)}
              </div>
            </div>
          ))
        )}

        <div className="resumen-totales">
          <div>
            <span>Subtotal:</span>
            <span>S/. {subtotal.toFixed(2)}</span>
          </div>
          <div>
            <span>Envío:</span>
            <span>S/. {costoEnvio.toFixed(2)}</span>
          </div>
          <div>
            <span>IGV:</span>
            <span>S/. {igv.toFixed(2)}</span>
          </div>
          <div>
            <strong>Total:</strong>
            <strong>S/. {total.toFixed(2)}</strong>
          </div>
        </div>
      </div>

      {/* Modal de autenticación requerida */}
      <AuthRequiredModal
        isOpen={showAuthModal}
        onClose={() => {
          setShowAuthModal(false);
          navigate("/carrito"); // Redirigir de vuelta al carrito
        }}
        message={authModalData.message}
        action={authModalData.action}
      />
    </div>
  );
};

export default CheckoutSimple;
