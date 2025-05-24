import React, { useEffect, useState } from "react";
import { obtenerProductos } from "../services/productoService";
import "bootstrap/dist/css/bootstrap.min.css";
import "../styles/Producto.css";

const Productos = () => {
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const cargarProductos = async () => {
      try {
        setCargando(true);
        const datosProductos = await obtenerProductos();
        setProductos(datosProductos);
        setError(null);
      } catch (err) {
        console.error("Error al cargar productos:", err);
        setError(
          "No se pudieron cargar los productos. Intenta recargar la página."
        );
      } finally {
        setCargando(false);
      }
    };
    cargarProductos();
  }, []);

  if (cargando)
    return (
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ height: "50vh" }}>
        <div className="spinner-border text-warning" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
      </div>
    );

  if (error)
    return <div className="alert alert-danger text-center mt-5">{error}</div>;

  return (
    <div className="productos-container">
      <h1 className="productos-title">Nuestros Productos</h1>

      <div className="productos-grid">
        {productos.map((producto) => (
          <div key={producto.id} className="producto-card">
            <img
              src={producto.imagenUrl}
              alt={producto.nombre}
              className="producto-img"
            />
            <div>
              <h3 className="producto-nombre">{producto.nombre}</h3>
              <p className="producto-precio">S/. {producto.precioVenta}</p>
              {producto.descripcion.includes("Incluye") && (
                <p className="producto-incluye">
                  {producto.descripcion.match(/Incluye.*/)[0]}
                </p>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Productos;
