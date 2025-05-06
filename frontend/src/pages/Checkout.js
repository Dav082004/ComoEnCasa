import React from "react";
import "../styles/Checkout.css";

const Checkout = () => {
  return (
    <div className="checkout-container">
      <div className="checkout-form">
        <h2>Detalles de Envío</h2>
        <div className="form-row">
          <select>
            <option>Seleccionar Departamento</option>
          </select>
          <select>
            <option>Seleccionar Distrito</option>
          </select>
        </div>
        <div className="form-row">
          <input type="text" placeholder="Ingrese su Dirección" />
          <input type="text" placeholder="Referencia" />
          <input type="text" placeholder="Número/Dpta/Bloque" />
        </div>

        <h2>Métodos de pago</h2>
        <div className="form-row">
          <input type="text" placeholder="Número de tarjeta" />
        </div>
        <div className="form-row">
          <input type="text" placeholder="Titular De la Tarjeta" />
          <input type="text" placeholder="MM/YY" />
          <input type="text" placeholder="CVV" />
        </div>

        <h2>Guardar Información</h2>
        <div className="form-row">
          <label>
            <input type="checkbox" />
            Guardar mi información para un pago más rápido
          </label>
        </div>
        <button className="finalizar-btn">Finalizar Orden</button>
      </div>

      <div className="checkout-summary">
        <h3>Summary (1 item)</h3>
        <p>
          Subtotal <span>S/.48.00</span>
        </p>
        <p>
          Envío <span>S/.10.00</span>
        </p>
        <p>
          Impuestos <span>-</span>
        </p>
        <hr />
        <p className="total">
          Total <span>S/.58.00</span>
        </p>
        <input type="text" placeholder="Gift card" />
        <button className="apply-btn">Apply</button>
      </div>
    </div>
  );
};

export default Checkout;
