import React, { memo } from "react";
import { Link } from "react-router-dom";
import "./styles/RelatedProducts.css";

const RelatedProducts = memo(({ productos, loading = false }) => {
  // Si está cargando, mostrar spinner
  if (loading) {
    return (
      <div className="related-products-section">
        <div className="related-products-title">Productos Relacionados</div>
        <div className="related-products-loading">
          <div className="loading-spinner"></div>
        </div>
      </div>
    );
  }

  // Si no hay productos, no mostrar nada
  if (!productos || productos.length === 0) {
    return null;
  }

  return (
    <section
      className="related-products-section"
      role="complementary"
      aria-labelledby="related-products-heading">
      <h2 id="related-products-heading" className="related-products-title">
        Productos Relacionados
      </h2>
      <div className="related-products-list">
        {productos.map((producto) => (
          <RelatedProductCard key={producto.id} producto={producto} />
        ))}
      </div>
    </section>
  );
});

// Componente separado para cada tarjeta de producto relacionado
const RelatedProductCard = memo(({ producto }) => {
  const handleImageError = (e) => {
    e.target.src = "/placeholder-image.jpg"; // Imagen de respaldo
  };

  return (
    <Link
      to={`/productos/${producto.id}`}
      className="related-product-card"
      aria-label={`Ver detalles de ${producto.nombre}`}
      title={producto.nombre}>
      <img
        className="related-product-img"
        src={producto.imagenUrl}
        alt={`Imagen de ${producto.nombre}`}
        onError={handleImageError}
        loading="lazy"
      />
      <span className="related-product-name">{producto.nombre}</span>
    </Link>
  );
});

RelatedProducts.displayName = "RelatedProducts";
RelatedProductCard.displayName = "RelatedProductCard";

export default RelatedProducts;
