import React, { memo, useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import "./styles/RelatedProducts.css";

const RelatedProducts = memo(({ productos, loading = false }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isHovered, setIsHovered] = useState(false);
  const intervalRef = useRef(null);

  // Configuración del carrusel
  const VISIBLE_COUNT = 2; // Mostrar solo 2 productos
  const AUTO_SCROLL_INTERVAL = 2500; // 2.5 segundos

  // Auto scroll del carrusel
  useEffect(() => {
    if (productos && productos.length > VISIBLE_COUNT && !isHovered) {
      intervalRef.current = setInterval(() => {
        setCurrentIndex((prevIndex) => {
          return (prevIndex + 1) % (productos.length - VISIBLE_COUNT + 1);
        });
      }, AUTO_SCROLL_INTERVAL);
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [productos, isHovered]);

  // Pausar/reanudar en hover - mejorado
  const handleMouseEnter = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
  };

  // Si está cargando, mostrar spinner
  if (loading) {
    return (
      <div className="related-products-section">
        <div className="related-products-title">🍰 Productos Relacionados</div>
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

  // Obtener productos visibles para mostrar
  const getVisibleProducts = () => {
    if (productos.length <= VISIBLE_COUNT) {
      return productos;
    }
    return productos.slice(currentIndex, currentIndex + VISIBLE_COUNT);
  };

  const visibleProducts = getVisibleProducts();
  const showCarouselControls = productos.length > VISIBLE_COUNT;

  return (
    <section
      className="related-products-section"
      role="complementary"
      aria-labelledby="related-products-heading"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}>
      <h2 id="related-products-heading" className="related-products-title">
        🍰 Productos Relacionados
      </h2>

      {showCarouselControls && (
        <div className="carousel-info">
          <span className="carousel-counter">
            {VISIBLE_COUNT} de {productos.length} productos
          </span>
          <div className="carousel-indicators">
            {Array.from({ length: productos.length - VISIBLE_COUNT + 1 }).map(
              (_, index) => (
                <div
                  key={index}
                  className={`indicator ${
                    index === currentIndex ? "active" : ""
                  }`}
                  onClick={() => setCurrentIndex(index)}
                />
              )
            )}
          </div>
        </div>
      )}

      <div className="related-products-carousel">
        <div className="related-products-list">
          {visibleProducts.map((producto) => (
            <RelatedProductCard key={producto.id} producto={producto} />
          ))}
        </div>
      </div>
    </section>
  );
});

// Componente separado para cada tarjeta de producto relacionado
const RelatedProductCard = memo(({ producto }) => {
  const handleImageError = (e) => {
    e.target.src = "/placeholder-product.svg"; // Imagen de respaldo
  };

  // Determinar la imagen a mostrar, evitando strings vacíos que causan warnings
  const getImageSrc = () => {
    if (!producto.imagenUrl || producto.imagenUrl.trim() === "") {
      return "/placeholder-product.svg";
    }
    return producto.imagenUrl;
  };

  return (
    <Link
      to={`/productos/${producto.id}`}
      className="related-product-card"
      aria-label={`Ver detalles de ${producto.nombre}`}
      title={producto.nombre}>
      <img
        className="related-product-img"
        src={getImageSrc()}
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
