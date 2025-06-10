import React, { useState } from "react";
import { useCart } from "../context/CartContext";
import { useNavigate } from "react-router-dom";
import "../styles/Checkout.css";

const CheckoutSimple = () => {
  const { cart, getTotalPrice } = useCart();
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
    tipoComprobante: "boleta", // boleta o factura
    metodoPago: "tarjeta", // tarjeta, yape, plin
  });

  const [procesando, setProcesando] = useState(false);

  // Lista completa de distritos de Lima Metropolitana
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

  const handleSubmit = (e) => {
    e.preventDefault();
    setProcesando(true);

    // Simulación de procesamiento
    setTimeout(() => {
      alert("¡Pedido procesado exitosamente!");
      setProcesando(false);
      navigate("/");
    }, 2000);
  };

  const handleComprobanteChange = (tipo) => {
    setDatos((prev) => ({
      ...prev,
      tipoComprobante: tipo,
      documento: "", // Limpiar documento al cambiar tipo
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
                  ? "Ingrese su DNI (8 dígitos)"
                  : "Ingrese su RUC (11 dígitos)"
              }
              value={datos.documento}
              onChange={handleChange}
              maxLength={datos.tipoComprobante === "boleta" ? 8 : 11}
              required
            />
          </div>

          <h3>Métodos de Pago</h3>
          <div className="form-row">
            <div
              className={`comprobante-option ${
                datos.metodoPago === "tarjeta" ? "active" : ""
              }`}
              onClick={() => handleMetodoPagoChange("tarjeta")}>
              <h5>💳 Tarjeta</h5>
              <p>Visa, MasterCard, etc.</p>
              <small>Pago seguro</small>
            </div>
            <div
              className={`comprobante-option ${
                datos.metodoPago === "yape" ? "active" : ""
              }`}
              onClick={() => handleMetodoPagoChange("yape")}>
              <h5>📱 Yape</h5>
              <p>Pago móvil rápido</p>
              <small>Instantáneo</small>
            </div>
            <div
              className={`comprobante-option ${
                datos.metodoPago === "plin" ? "active" : ""
              }`}
              onClick={() => handleMetodoPagoChange("plin")}>
              <h5>💰 Plin</h5>
              <p>Transferencia móvil</p>
              <small>Sin comisiones</small>
            </div>
          </div>

          {/* Campos de tarjeta solo si se selecciona tarjeta */}
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

          {/* Información para Yape */}
          {datos.metodoPago === "yape" && (
            <div className="metodo-pago-info">
              <div className="payment-info-card">
                <h4>📱 Pagar con Yape</h4>
                <p>1. Escanea el código QR con tu app Yape</p>
                <p>
                  2. Confirma el monto: <strong>S/. {total.toFixed(2)}</strong>
                </p>
                <p>3. Completa el pago en tu celular</p>
                <div className="qr-placeholder">
                  <div className="qr-code">📱 QR Code</div>
                </div>
              </div>
            </div>
          )}

          {/* Información para Plin */}
          {datos.metodoPago === "plin" && (
            <div className="metodo-pago-info">
              <div className="payment-info-card">
                <h4>💰 Pagar con Plin</h4>
                <p>1. Abre tu app Plin</p>
                <p>
                  2. Envía <strong>S/. {total.toFixed(2)}</strong> al número:
                </p>
                <div className="numero-plin">
                  <strong>987 654 321</strong>
                </div>
                <p>3. Usa como concepto: "Pedido ComoEnCasa"</p>
              </div>
            </div>
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
        <div>
          {productos.length > 0 ? (
            productos.map((prod) => (
              <div key={prod.id} className="resumen-producto-item">
                <div className="resumen-producto-header">
                  <div>
                    <div className="resumen-producto-name">{prod.nombre}</div>
                    <small className="resumen-producto-quantity">
                      {prod.quantity} x S/.{" "}
                      {(prod.precioVenta || prod.precio).toFixed(2)}
                    </small>
                  </div>
                  <div className="resumen-producto-total">
                    S/.{" "}
                    {(
                      (prod.precioVenta || prod.precio || 0) * prod.quantity
                    ).toFixed(2)}
                  </div>
                </div>
                {/* Notas del producto */}
                {(prod.comentarios || prod.nota) && (
                  <div className="producto-nota">
                    <strong>Nota:</strong> {prod.comentarios || prod.nota}
                  </div>
                )}
              </div>
            ))
          ) : (
            <div className="carrito-vacio-message">
              No hay productos en el carrito
            </div>
          )}
        </div>

        <div className="resumen-totales">
          <div className="resumen-subtotal">
            <span>Subtotal:</span>
            <span>S/. {subtotal.toFixed(2)}</span>
          </div>

          <div className="envio-line">
            <span>Envío:</span>
            <span className={costoEnvio === 0 ? "envio-gratis" : ""}>
              {costoEnvio === 0
                ? "Seleccione dirección"
                : `S/. ${costoEnvio.toFixed(2)}`}
            </span>
          </div>

          <div className="resumen-igv">
            <span>IGV (18%):</span>
            <span>S/. {igv.toFixed(2)}</span>
          </div>

          <div className="resumen-total-final">
            <span>Total:</span>
            <span>S/. {total.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutSimple;
