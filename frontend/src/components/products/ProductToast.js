import React from "react";
import "./styles/ProductToast.css";

const ProductToast = ({ producto, cantidad }) => (
  <div className="toast-product-info">
    <div className="toast-header">
      <div className="toast-icon">🧁</div>
      <div className="toast-title">¡Agregado al carrito!</div>
    </div>
    <div className="toast-product-details">
      <div className="toast-product-name">{producto.nombre}</div>
      <div className="toast-quantity-price">
        <span className="toast-quantity">
          <i className="fas fa-shopping-basket"></i> {cantidad}{" "}
          {cantidad === 1 ? "unidad" : "unidades"}
        </span>
        <span className="toast-price">
          ${(producto.precioVenta * cantidad).toLocaleString("es-CO")}
        </span>
      </div>
      {producto.comentarios && (
        <div className="toast-product-comments">
          <i className="fas fa-comment-dots"></i>
          <span>{producto.comentarios}</span>
        </div>
      )}
    </div>
  </div>
);

export default ProductToast;
