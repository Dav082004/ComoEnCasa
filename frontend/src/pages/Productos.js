import React, { useState, useEffect, useCallback } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { getProductos } from "../services/productoService";
import ProductCard from "../components/products/ProductCard";
import CategoryFilter from "../components/products/CategoryFilter";
import PriceSortFilter from "../components/products/PriceSortFilter";
import "../styles/Productos.css";
import "bootstrap/dist/css/bootstrap.min.css";

const Productos = () => {
  const [productos, setProductos] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    category: null,
    priceSort: null,
  });

  // Hooks de react-router
  const location = useLocation();
  const navigate = useNavigate();

  // Cargar productos iniciales
  useEffect(() => {
    const loadProducts = async () => {
      try {
        setLoading(true);
        const data = await getProductos();
        setProductos(data);
        setFilteredProducts(data);
      } catch (error) {
        console.error("Error loading products:", error);
      } finally {
        setLoading(false);
      }
    };
    loadProducts();
  }, []);

  // Actualizar URL con los filtros
  const updateURL = useCallback(() => {
    const params = new URLSearchParams();
    if (filters.category) params.set("category", filters.category);
    if (filters.priceSort) params.set("sort", filters.priceSort);
    navigate(`?${params.toString()}`, { replace: true });
  }, [filters.category, filters.priceSort, navigate]);

  // Aplicar filtros cuando cambian
  useEffect(() => {
    const applyFilters = () => {
      let result = [...productos];

      // Filtrar por categoría
      if (filters.category) {
        result = result.filter(
          (p) => p.categoriaId.toString() === filters.category
        );
      }

      // Ordenar por precio
      if (filters.priceSort === "asc") {
        result.sort((a, b) => a.precioVenta - b.precioVenta);
      } else if (filters.priceSort === "desc") {
        result.sort((a, b) => b.precioVenta - a.precioVenta);
      }

      setFilteredProducts(result);
      updateURL();
    };

    if (productos.length > 0) {
      applyFilters();
    }
  }, [filters, productos, updateURL]);

  // Cargar filtros iniciales desde URL
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    setFilters({
      category: params.get("category") || null,
      priceSort: params.get("sort") || null,
    });
  }, [location.search]);

  if (loading) {
    return (
      <div
        className="loading-container d-flex flex-column align-items-center justify-content-center"
        style={{ minHeight: "40vh" }}>
        <div
          className="spinner-border text-pink mb-3"
          role="status"
          style={{ width: "4rem", height: "4rem" }}>
          <span className="visually-hidden">Cargando...</span>
        </div>
        <span className="mt-2" style={{ color: "#d63384", fontWeight: "bold" }}>
          Cargando productos...
        </span>
      </div>
    );
  }

  return (
    <div className="products-page">
      <div className="container">
        <div className="row">
          {/* Sidebar de categorías */}
          <div className="col-md-3">
            <CategoryFilter
              selectedCategory={filters.category}
              onSelectCategory={(category) =>
                setFilters({ ...filters, category, priceSort: null })
              }
            />
          </div>

          {/* Área principal */}
          <div className="col-md-9">
            {/* Header con título y filtro de precio */}
            <div
              className="products-header d-flex flex-column flex-md-row align-items-md-center justify-content-between mb-3 p-3 rounded shadow-sm bg-white"
              style={{ gap: "1rem" }}>
              <div>
                <h1
                  className="mb-0"
                  style={{
                    color: "#d63384",
                    fontWeight: "bold",
                    fontSize: "2.2rem",
                  }}>
                  Nuestros Productos
                </h1>
                <span style={{ fontSize: "1rem", color: "#888" }}>
                  {filteredProducts.length} producto
                  {filteredProducts.length !== 1 && "s"} encontrado
                  {filteredProducts.length !== 1 && "s"}
                </span>
              </div>
              <PriceSortFilter
                priceSort={filters.priceSort}
                onSortChange={(priceSort) =>
                  setFilters({ ...filters, priceSort })
                }
              />
            </div>

            {/* Lista de productos */}
            <div className="product-list">
              {filteredProducts.length > 0 ? (
                filteredProducts.map((producto) => (
                  <div key={producto.id}>
                    <ProductCard producto={producto} />
                  </div>
                ))
              ) : (
                <div className="no-products text-center p-4 bg-light rounded shadow-sm">
                  <h4 className="mb-2" style={{ color: "#d63384" }}>
                    No se encontraron productos con estos filtros
                  </h4>
                  <p className="mb-0">
                    Prueba cambiando la categoría o el orden de precio.
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Productos;
