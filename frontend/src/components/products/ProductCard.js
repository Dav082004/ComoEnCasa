import React from "react";
import { Link } from "react-router-dom";
import "./styles/ProductCard.css";

const ProductCard = ({ producto }) => {
  const includesText = producto.descripcion?.match(/Incluye.*/)?.[0];

  return (
    <Link to={`/productos/${producto.id}`} className="cardLink">
      <div className="card">
        <div className="image-container">
          <img
            src={producto.imagenUrl}
            alt={producto.nombre}
            className="image"
          />
        </div>
        <div className="info">
          <h3 className="name">{producto.nombre}</h3>
          <p className="price">S/. {producto.precioVenta}</p>
          {includesText && <p className="includes">{includesText}</p>}
        </div>
      </div>
    </Link>
  );
};

export default ProductCard;
