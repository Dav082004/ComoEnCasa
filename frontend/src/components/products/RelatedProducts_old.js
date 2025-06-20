import React, { memo, useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import "./styles/RelatedProducts.css";

const RelatedProducts = memo(({ productos, loading = false }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [visibleProducts, setVisibleProducts] = useState([]);
  const intervalRef = useRef(null);

  // Configuración del carrusel
  const VISIBLE_COUNT = 2; // Mostrar solo 2 productos
  const AUTO_SCROLL_INTERVAL = 4000; // 4 segundos para mejor visualización

  // Procesar productos para el carrusel
  useEffect(() => {
    if (productos && productos.length > 0) {
      // Si hay menos de 2 productos, mostrar todos
      if (productos.length <= VISIBLE_COUNT) {
        setVisibleProducts(productos);
        return;
      }

      // Si hay más de 2, crear un array circular duplicando productos para efecto infinito
      const extendedProducts = [...productos, ...productos.slice(0, VISIBLE_COUNT)];
      setVisibleProducts(extendedProducts);
    }
  }, [productos]);

  // Auto scroll del carrusel
  useEffect(() => {
    if (visibleProducts.length > VISIBLE_COUNT) {
      intervalRef.current = setInterval(() => {
        setCurrentIndex((prevIndex) => {
          const nextIndex = prevIndex + 1;
          // Reset suave cuando llegamos al final
          if (nextIndex >= productos.length) {
            return 0;
          }
          return nextIndex;
        });
      }, AUTO_SCROLL_INTERVAL);
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [visibleProducts.length, productos?.length]);

  // Pausar/reanudar en hover
  const handleMouseEnter = () => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
    }
  };

  const handleMouseLeave = () => {
    if (visibleProducts.length > VISIBLE_COUNT) {
      intervalRef.current = setInterval(() => {
        setCurrentIndex((prevIndex) => {
          const nextIndex = prevIndex + 1;
          if (nextIndex >= productos.length) {
            return 0;
          }
          return nextIndex;
        });
      }, AUTO_SCROLL_INTERVAL);
    }
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

    const result = [];
    for (let i = 0; i < VISIBLE_COUNT; i++) {
      const index = (currentIndex + i) % productos.length;
      result.push(productos[index]);
    }
    return result;
  };

  const currentVisibleProducts = getVisibleProducts();

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
      
      {productos.length > VISIBLE_COUNT && (
        <div className="carousel-info">
          <span className="carousel-counter">
            {Math.min(productos.length, VISIBLE_COUNT)} de {productos.length} productos
          </span>
          <div className="carousel-indicators">
            {productos.map((_, index) => (
              <div
                key={index}
                className={`indicator ${index === currentIndex ? 'active' : ''}`}
                onClick={() => setCurrentIndex(index)}
              />
            ))}
          </div>
        </div>
      )}

      <div className="related-products-carousel">
        <div 
          className="related-products-list carousel-container"
          style={{
            transform: productos.length <= VISIBLE_COUNT ? 'none' : 
              `translateX(-${(currentIndex * (100 / VISIBLE_COUNT))}%)`,
          }}>
          {currentVisibleProducts.map((producto, index) => (
            <RelatedProductCard 
              key={`${producto.id}-${currentIndex}-${index}`} 
              producto={producto} 
            />
          ))}
        </div>
      </div>

      {productos.length > VISIBLE_COUNT && (
        <div className="carousel-controls">
          <button 
            className="carousel-btn prev" 
            onClick={() => setCurrentIndex(prev => prev === 0 ? productos.length - 1 : prev - 1)}
            aria-label="Producto anterior">
            ←
          </button>
          <button 
            className="carousel-btn next" 
            onClick={() => setCurrentIndex(prev => (prev + 1) % productos.length)}
            aria-label="Siguiente producto">
            →
          </button>
        </div>
      )}

      <div className="carousel-status">
        <span className="auto-scroll-indicator">
          {productos.length > VISIBLE_COUNT ? "🔄 Rotación automática cada 4 segundos" : `📦 ${productos.length} productos disponibles`}
        </span>
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
