// src/pages/carrito.js
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { getProductoById } from "../services/productoService";
import "../styles/Carrito.css";

const Carrito = () => {
  const { cart, updateQuantity, removeFromCart, getTotalPrice } = useCart();
  const navigate = useNavigate();
  const [productosConStock, setProductosConStock] = useState({});
  const [loading, setLoading] = useState(true);

  // Cargar información de stock actualizada para cada producto
  useEffect(() => {
    const cargarStockProductos = async () => {
      const productos = Object.values(cart);
      const stockData = {};

      for (const producto of productos) {
        try {
          const productoActualizado = await getProductoById(producto.id);
          if (productoActualizado) {
            stockData[producto.id] = {
              stock: productoActualizado.cantidad || 0,
              disponible: productoActualizado.disponible,
              ...productoActualizado,
            };
          }
        } catch (error) {
          console.error(
            `Error al obtener stock de producto ${producto.id}:`,
            error
          );
          stockData[producto.id] = {
            stock: 0,
            disponible: false,
          };
        }
      }

      setProductosConStock(stockData);
      setLoading(false);
    };

    if (Object.keys(cart).length > 0) {
      cargarStockProductos();
    } else {
      setLoading(false);
    }
  }, [cart]);

  const modificarCantidad = (id, delta) => {
    const producto = Object.values(cart).find((p) => p.id === parseInt(id));
    const stockInfo = productosConStock[id];

    if (producto && stockInfo) {
      const nuevaCantidad = producto.quantity + delta;
      const stockDisponible = stockInfo.stock || 0;

      // Validar que no exceda el stock disponible
      if (nuevaCantidad > stockDisponible) {
        alert(
          `Solo hay ${stockDisponible} unidades disponibles de ${producto.nombre}`
        );
        return;
      }

      // Validar que no sea menor a 1
      if (nuevaCantidad < 1) {
        return;
      }

      updateQuantity(id, nuevaCantidad);
    }
  };

  const eliminarProducto = (id) => {
    removeFromCart(id);
  };

  const productos = Object.values(cart);
  const total = getTotalPrice();
  const subtotal = total / 1.18; // Calcular subtotal sin IGV
  const igv = total - subtotal;

  if (loading) {
    return (
      <div className="carrito-container">
        <div className="loading">Cargando información del carrito...</div>
      </div>
    );
  }

  if (productos.length === 0) {
    return (
      <div className="carrito-container">
        <div className="carrito-vacio">
          <h2>Tu carrito está vacío</h2>
          <p>¡Descubre nuestros deliciosos productos!</p>
          <button
            className="btn-finalizar"
            onClick={() => navigate("/productos")}
            style={{ background: "var(--primary-pink)" }}>
            Ver Productos
          </button>
        </div>
      </div>
    );
  }
  return (
    <div className="carrito-container">
      <div className="carrito-seccion-izquierda">
        <h1 className="carrito-titulo">Mi Carrito</h1>
        <div className="carrito-lista">
          {productos.map((prod) => {
            const stockInfo = productosConStock[prod.id] || {};
            const stockDisponible = stockInfo.stock || 0;
            const esDisponible = stockInfo.disponible !== false;

            return (
              <div className="carrito-item" key={prod.id}>
                <img
                  src={prod.imagenUrl || prod.img}
                  alt={prod.nombre}
                  className="carrito-img"
                />
                <div className="carrito-detalles">
                  <h3>{prod.nombre}</h3>
                  {/* Información de stock */}
                  <div className="stock-info-carrito">
                    {esDisponible ? (
                      <span className="stock-disponible">
                        Stock disponible: {stockDisponible}
                      </span>
                    ) : (
                      <span className="stock-agotado">
                        Producto no disponible
                      </span>
                    )}
                  </div>
                  {/* Notas/Comentarios del producto */}
                  {prod.comentarios && (
                    <div className="comentarios-carrito">
                      <strong>Nota:</strong> {prod.comentarios}
                    </div>
                  )}{" "}
                  <div className="cantidad-controles">
                    <button
                      onClick={() => modificarCantidad(prod.id, -1)}
                      disabled={prod.quantity <= 1}
                      title="Reducir cantidad">
                      −
                    </button>
                    <span>{prod.quantity}</span>
                    <button
                      onClick={() => modificarCantidad(prod.id, 1)}
                      disabled={
                        !esDisponible || prod.quantity >= stockDisponible
                      }
                      title={
                        !esDisponible
                          ? "Producto no disponible"
                          : prod.quantity >= stockDisponible
                          ? "Stock máximo alcanzado"
                          : "Agregar uno más"
                      }>
                      +
                    </button>
                    <button
                      className="btn-eliminar"
                      onClick={() => eliminarProducto(prod.id)}
                      title="Eliminar producto">
                      ×
                    </button>
                  </div>
                </div>
                <div className="carrito-precio">
                  S/.{" "}
                  {((prod.precioVenta || prod.precio) * prod.quantity).toFixed(
                    2
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <div className="carrito-resumen">
        <h2>Resumen del Pedido</h2>
        {productos.map((prod) => (
          <div key={prod.id} className="resumen-linea">
            <span>{prod.nombre}</span>
            <span>
              {prod.quantity} x S/.{" "}
              {(prod.precioVenta || prod.precio).toFixed(2)}
            </span>
          </div>
        ))}
        <div className="resumen-total">
          <p>Subtotal: S/. {subtotal.toFixed(2)}</p>
          <p>IGV (18%): S/. {igv.toFixed(2)}</p>
          <h3>Total: S/. {total.toFixed(2)}</h3>
        </div>

        <button className="btn-finalizar" onClick={() => navigate("/Checkout")}>
          Finalizar Compra
        </button>
      </div>
    </div>
  );
};

export default Carrito;
