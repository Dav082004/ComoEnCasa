import React, { useState, useEffect } from "react";
import ProductCard from "../components/products/ProductCard";
import "../styles/Productos.css";

const Eventos = () => {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchEventos = async () => {
      try {
        setLoading(true);
        setError(null);

        // Primero obtenemos todas las categorías para encontrar el ID de "eventos"
        const categoriasResponse = await fetch(
          "http://localhost:8080/api/categorias"
        );
        if (!categoriasResponse.ok) {
          throw new Error("Error al cargar las categorías");
        }
        const categorias = await categoriasResponse.json();

        // Buscar la categoría "eventos" (case insensitive)
        const categoriaEventos = categorias.find(
          (categoria) =>
            categoria.nombre.toLowerCase().includes("evento") ||
            categoria.nombre.toLowerCase().includes("celebracion") ||
            categoria.nombre.toLowerCase().includes("especial")
        );

        if (!categoriaEventos) {
          // Si no hay categoría específica, obtener todos los productos y filtrar
          const response = await fetch("http://localhost:8080/api/productos");
          if (!response.ok) {
            throw new Error("Error al cargar los productos");
          }
          const todosLosProductos = await response.json();

          // Filtrar productos que sean para eventos o celebraciones
          const productosEventos = todosLosProductos.filter(
            (producto) =>
              producto.nombre.toLowerCase().includes("evento") ||
              producto.nombre.toLowerCase().includes("celebracion") ||
              producto.nombre.toLowerCase().includes("cumpleaños") ||
              producto.nombre.toLowerCase().includes("graduacion") ||
              producto.nombre.toLowerCase().includes("boda") ||
              producto.nombre.toLowerCase().includes("aniversario") ||
              producto.descripcion?.toLowerCase().includes("evento") ||
              producto.descripcion?.toLowerCase().includes("celebracion") ||
              producto.categoria?.nombre?.toLowerCase().includes("evento")
          );

          setProductos(productosEventos);
        } else {
          // Si existe la categoría, obtener productos por categoría
          const response = await fetch(
            `http://localhost:8080/api/productos/categoria/${categoriaEventos.id}`
          );
          if (!response.ok) {
            throw new Error("Error al cargar los productos de eventos");
          }
          const data = await response.json();
          setProductos(data);
        }
      } catch (error) {
        console.error("Error al cargar productos de eventos:", error);
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchEventos();
  }, []);

  if (loading) {
    return (
      <div
        className="loading-container d-flex flex-column align-items-center justify-content-center"
        style={{ minHeight: "60vh" }}>
        <div className="spinner-border text-primary mb-3" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
        <span className="loading-text">Cargando productos para eventos...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container py-5">
        <div className="alert alert-danger text-center">
          <h4>¡Oops! Algo salió mal</h4>
          <p>{error}</p>
          <button
            className="btn btn-primary"
            onClick={() => window.location.reload()}>
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="products-page">
      <div className="container py-4">
        {/* Header de la página */}
        <div className="products-header d-flex flex-column flex-md-row justify-content-between align-items-center mb-4">
          <div>
            <h1 className="products-title">Pasteles para Eventos</h1>
            <p className="products-subtitle text-muted">
              Celebra tus momentos especiales con nosotros
            </p>
          </div>
          <div className="products-count">
            <span className="badge bg-primary px-3 py-2">
              {productos.length} productos disponibles
            </span>
          </div>
        </div>

        {/* Grid de productos */}
        {productos.length > 0 ? (
          <div className="products-grid">
            {productos.map((producto) => (
              <ProductCard key={producto.id} producto={producto} />
            ))}
          </div>
        ) : (
          <div className="no-products text-center py-5">
            <div className="mb-4">
              <i className="fas fa-birthday-cake fa-4x text-muted mb-3"></i>
              <h3>No hay productos para eventos disponibles</h3>
              <p className="text-muted">
                Actualmente no tenemos productos especiales para eventos.
                ¡Vuelve pronto!
              </p>
            </div>
            <a href="/productos" className="btn btn-primary">
              Ver todos los productos
            </a>
          </div>
        )}
      </div>
    </div>
  );
};

export default Eventos;
