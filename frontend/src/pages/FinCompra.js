// src/pages/FinCompra.js

import { useState } from "react";
import { useCart } from "../context/CartContext";    // Importa tu hook de contexto
import "../styles/FinCompra.css";

const FinCompra = () => {
  const { cart } = useCart();                        // Accede al carrito desde el contexto
  const [datos, setDatos] = useState({
    departamento: "",
    distrito: "",
    direccion: "",
    referencia: "",
    numero: "",
    tarjeta: "",
    vencimiento: "",
    cvv: "",
    guardar: false,
    giftcard: ""
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setDatos((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    alert("Orden finalizada correctamente.");
  };

  // Usa directamente el array del contexto
  const productos = Object.values(cart);
  const subtotal = productos.reduce((acc, p) => acc + p.precio * p.quantity, 0);
  const igv      = subtotal * 0.18;
  const total    = subtotal + igv;

  return (
    <div className="container fincompra-grid">
      <form onSubmit={handleSubmit} className="checkout-form">
        <h2>Detalles de Envío</h2>
        <div className="form-row">
          <select name="departamento" value={datos.departamento} onChange={handleChange} required>
            <option value="">Seleccionar Departamento</option>
            <option value="Lima">Lima</option>
            <option value="Arequipa">Arequipa</option>
          </select>
          <select name="distrito" value={datos.distrito} onChange={handleChange} required>
            <option value="">Seleccionar Distrito</option>
            <option value="Miraflores">Miraflores</option>
            <option value="Surco">Surco</option>
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
          <input
            type="text"
            name="referencia"
            placeholder="Referencia"
            value={datos.referencia}
            onChange={handleChange}
          />
          <input
            type="text"
            name="numero"
            placeholder="Número/Dpto/Bloque"
            value={datos.numero}
            onChange={handleChange}
          />
        </div>

        <h2>Métodos de Pago</h2>
        <div className="form-row">
          <input
            type="text"
            name="tarjeta"
            placeholder="Número de tarjeta"
            value={datos.tarjeta}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="vencimiento"
            placeholder="MM/YY"
            value={datos.vencimiento}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="cvv"
            placeholder="CVV"
            value={datos.cvv}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-row">
          <label>
            <input
              type="checkbox"
              name="guardar"
              checked={datos.guardar}
              onChange={handleChange}
            />
            Guardar mi información para una compra más rápida
          </label>
        </div>

        <div className="form-row">
          <input
            type="text"
            name="giftcard"
            placeholder="Código de regalo"
            value={datos.giftcard}
            onChange={handleChange}
          />
          <button type="button">Aplicar</button>
        </div>

        <button type="submit" className="submit-button">
          Finalizar Orden
        </button>
      </form>

      <aside className="resumen-compra">
        <h3>Resumen del Pedido</h3>
        {productos.map((prod) => (
          <div key={prod.id} className="resumen-item">
            <span>{prod.nombre}</span>
            <span>
              {prod.quantity} x S/. {prod.precio.toFixed(2)}
            </span>
          </div>
        ))}
        <hr />
        <p>Subtotal: S/. {subtotal.toFixed(2)}</p>
        <p>IGV (18%): S/. {igv.toFixed(2)}</p>
        <h4>Total: S/. {total.toFixed(2)}</h4>
      </aside>
    </div>
  );
};

export default FinCompra;
