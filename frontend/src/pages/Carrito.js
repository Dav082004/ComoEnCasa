// src/pages/Carrito.js
import React from "react";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import "../styles/Carrito.css";

const Carrito = () => {
  const { cart, addToCart, removeFromCart } = useCart();
  const navigate = useNavigate();

  // Convierte el objeto cart a array
  const productos = Object.values(cart);

  // Modificar cantidad suma o resta la cantidad, si llega a 0 elimina el producto
  const modificarCantidad = (id, delta) => {
    const producto = productos.find((item) => item.id === id);
    if (!producto) return;

    const nuevaCantidad = producto.cantidad + delta;
    if (nuevaCantidad <= 0) {
      removeFromCart(id);
    } else {
      addToCart(producto, delta);
    }
  };

  // Calcular subtotal, IGV y total
  const subtotal = productos.reduce(
    (acc, item) => acc + item.precioVenta * item.cantidad,
    0
  );
  const igv = subtotal * 0.18;
  const total = subtotal + igv;

  if (productos.length === 0) {
    return <p>Tu carrito está vacío.</p>;
  }

  return (
    <div className="carrito-container">
      <div className="carrito-lista">
        {productos.map((prod) => (
          <div className="carrito-item" key={prod.id}>
            <img src={prod.imagenUrl} alt={prod.nombre} className="carrito-img" />
            <div className="carrito-detalles">
              <h3>{prod.nombre}</h3>
              <div className="cantidad-controles">
                <button onClick={() => modificarCantidad(prod.id, -1)}>-</button>
                <span>{prod.cantidad}</span>
                <button onClick={() => modificarCantidad(prod.id, 1)}>+</button>
                <button
                  className="btn-eliminar"
                  onClick={() => removeFromCart(prod.id)}
                >
                  🗑
                </button>
              </div>
              <div className="carrito-precio">
                S/. {(prod.precioVenta * prod.cantidad).toFixed(2)}
              </div>
            </div>
          </div>
        ))}
      </div>

      {productos.length > 0 && (
        <div className="carrito-resumen">
          <h2>Resumen</h2>
          {productos.map((prod) => (
            <div key={prod.id} className="resumen-linea">
              <span>{prod.nombre}</span>
              <span>
                {prod.cantidad} x S/. {prod.precioVenta.toFixed(2)}
              </span>
            </div>
          ))}
          <hr />
          <div className="resumen-total">
            <p>Subtotal: S/. {subtotal.toFixed(2)}</p>
            <p>IGV (18%): S/. {igv.toFixed(2)}</p>
            <h3>Total: S/. {total.toFixed(2)}</h3>
          </div>

          <button
            className="btn-finalizar"
            onClick={() => navigate("/Checkout")}
            style={{
              marginTop: "20px",
              padding: "10px",
              backgroundColor: "#c94f7c",
              color: "white",
              border: "none",
              borderRadius: "6px",
              fontWeight: "bold",
              cursor: "pointer",
            }}
          >
            Finalizar Compra
          </button>
        </div>
      )}
    </div>
  );
};

export default Carrito;
