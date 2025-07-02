import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getProductosByCategoria } from "../services/productoService";

const Postres = () => {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const cargarProductos = async () => {
      try {
        setLoading(true);
        const productosPostres = await getProductosByCategoria(3); // Categoría Postres
        setProductos(productosPostres);
      } catch (error) {
        console.error("Error cargando productos de postres:", error);
      } finally {
        setLoading(false);
      }
    };

    cargarProductos();
  }, []);

  const handleProductClick = (producto) => {
    navigate(`/productos/${producto.id}`, { state: { producto } });
  };

  if (loading) {
    return (
      <div className="container py-5 text-center">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="container py-5 pastel-bg">
      <div className="text-center mb-5">
        <h1 className="pastel-title">Nuestros Postres</h1>
        <p className="pastel-subtitle">Delicias dulces para endulzar tu día</p>
      </div>

      <div className="row g-4">
        {productos.map((producto) => (
          <div className="col-lg-3 col-md-4 col-sm-6" key={producto.id}>
            <div
              className="card h-100 pastel-card"
              onClick={() => handleProductClick(producto)}
              style={{ cursor: "pointer" }}>
              <div className="pastel-img-container">
                <img
                  src={producto.imagenUrl || "/placeholder-product.svg"}
                  alt={producto.nombre}
                  className="pastel-img"
                  onError={(e) => {
                    e.target.src = "/placeholder-product.svg";
                  }}
                />
              </div>
              <div className="card-body">
                <h5 className="pastel-product-title">{producto.nombre}</h5>
                <p className="pastel-price">
                  S/.{" "}
                  {producto.precioVenta
                    ? producto.precioVenta.toFixed(2)
                    : producto.precio.toFixed(2)}
                </p>
                <p className="product-description">
                  {producto.descripcion
                    ? producto.descripcion.length > 80
                      ? `${producto.descripcion.substring(0, 80)}...`
                      : producto.descripcion
                    : "Delicioso postre para endulzar tu día"}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>

      {productos.length === 0 && (
        <div className="text-center py-5">
          <h3 className="text-muted">
            No hay productos disponibles en esta categoría
          </h3>
        </div>
      )}
    </div>
  );
};

export default Postres;
