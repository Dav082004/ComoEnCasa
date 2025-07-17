import React from "react";
import { Link } from "react-router-dom";
import "./styles/ProductCard.css";

const ProductCard = ({ producto }) => {
  const includesText = producto.descripcion?.match(/Incluye.*/)?.[0];

  // Imagen por defecto si no hay imagenUrl o está vacía
  const imagenSrc =
    producto.imagenUrl && producto.imagenUrl.trim() !== ""
      ? producto.imagenUrl
      : "/placeholder-product.svg"; // Imagen por defecto

  return (
    <div className="card">
      <Link to={`/productos/${producto.id}`} className="cardLink">
        <div className="image-container">
          <img
            src={imagenSrc}
            alt={producto.nombre}
            className="image"
            onError={(e) => {
              // Fallback si la imagen no se puede cargar
              if (e.target.src !== "/placeholder-product.svg") {
                e.target.src = "/placeholder-product.svg";
              }
            }}
          />
        </div>
        <div className="info">
          <h3 className="name">{producto.nombre}</h3>
          <p className="price">S/. {producto.precioVenta}</p>
          {includesText && <p className="includes">{includesText}</p>}
        </div>
      </Link>
    </div>
  );
};

export default ProductCard;
