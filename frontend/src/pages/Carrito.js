// src/pages/Carrito.js
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import "../styles/Carrito.css";

const Carrito = () => {
  const { cart, setCart } = useCart();
  const navigate = useNavigate();

  const modificarCantidad = (id, delta) => {
    setCart((prevCart) => {
      const nuevoCarrito = { ...prevCart };
      if (!nuevoCarrito[id]) return prevCart;

      nuevoCarrito[id].cantidad += delta;
      if (nuevoCarrito[id].cantidad <= 0) {
        delete nuevoCarrito[id];
      }
      return nuevoCarrito;
    });
  };

  const eliminarProducto = (id) => {
    setCart((prevCart) => {
      const nuevoCarrito = { ...prevCart };
      delete nuevoCarrito[id];
      return nuevoCarrito;
    });
  };

  const productos = Object.values(cart);
  const subtotal = productos.reduce((acc, p) => acc + p.precio * p.cantidad, 0);
  const igv = subtotal * 0.18;
  const total = subtotal + igv;

  return (
    <div className="carrito-container">
      <div className="carrito-lista">
        {productos.map((prod) => (
          <div className="carrito-item" key={prod.id}>
            <img src={prod.img} alt={prod.nombre} className="carrito-img" />
            <div className="carrito-detalles">
              <h3>{prod.nombre}</h3>
              <div className="cantidad-controles">
                <button onClick={() => modificarCantidad(prod.id, -1)}>
                  -
                </button>
                <span>{prod.cantidad}</span>
                <button onClick={() => modificarCantidad(prod.id, 1)}>+</button>
                <button
                  className="btn-eliminar"
                  onClick={() => eliminarProducto(prod.id)}>
                  🗑
                </button>
              </div>
              <div className="carrito-precio">
                S/. {(prod.precio * prod.cantidad).toFixed(2)}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="carrito-resumen">
        <h2>Resumen</h2>
        {productos.map((prod) => (
          <div key={prod.id} className="resumen-linea">
            <span>{prod.nombre}</span>
            <span>
              {prod.cantidad} x S/. {prod.precio.toFixed(2)}
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
          }}>
          Finalizar Compra
        </button>
      </div>
    </div>
  );
};

export default Carrito;
