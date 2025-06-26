import React, { useState, useEffect } from "react";
import "./styles/Admin.css";
import {
  getProductos,
  createProducto,
  updateProducto,
  deleteProducto,
} from "../../services/productoService";
import { useCategorias } from "../../context/CategoriaContext";

export default function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState("");
  const [currentProduct, setCurrentProduct] = useState(null);
  const [form, setForm] = useState({
    categoriaId: "",
    nombre: "",
    descripcion: "",
    precioVenta: "",
    costoProduccion: "",
    imagenUrl: "",
    cantidad: "",
  });

  // Usar el contexto de categorías
  const { categorias, categoriaMap } = useCategorias();

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const data = await getProductos();
      setProducts(data);
    } catch (err) {
      console.error("Error cargando productos:", err);
    }
  };

  const openModal = (type, product = null) => {
    setModalType(type);
    setCurrentProduct(product);

    if (type === "edit" && product) {
      setForm({
        categoriaId: product.categoriaId,
        nombre: product.nombre,
        descripcion: product.descripcion || "",
        precioVenta: product.precioVenta,
        costoProduccion: product.costoProduccion,
        imagenUrl: product.imagenUrl || "",
        cantidad: product.cantidad,
      });
    } else {
      setForm({
        categoriaId: "",
        nombre: "",
        descripcion: "",
        precioVenta: "",
        costoProduccion: "",
        imagenUrl: "",
        cantidad: "",
      });
    }

    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setCurrentProduct(null);
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      categoriaId: Number(form.categoriaId),
      nombre: form.nombre,
      descripcion: form.descripcion,
      precioVenta: Number(form.precioVenta),
      costoProduccion: Number(form.costoProduccion),
      imagenUrl: form.imagenUrl,
      cantidad: Number(form.cantidad),
    };

    try {
      if (modalType === "create") {
        await createProducto(payload);
      } else if (modalType === "edit" && currentProduct) {
        await updateProducto(currentProduct.id, payload);
      } else if (modalType === "delete" && currentProduct) {
        await deleteProducto(currentProduct.id);
      }
      closeModal();
      // Solo recargar productos, las categorías se manejan automáticamente con el contexto
      await loadProducts();
    } catch (err) {
      console.error("Error en acción CRUD:", err);
    }
  };

  return (
    <div className="page-container">
      <h2 className="theme-header">Gestión de Productos</h2>
      <div className="admin-actions">
        <button className="theme-button" onClick={() => openModal("create")}>
          Nuevo Producto
        </button>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Categoría</th>
            <th>Nombre</th>
            <th>Descripción</th>
            <th>Precio</th>
            <th>Costo Producción</th>
            <th>Stock</th>
            <th>Disponible</th>
            <th>Imagen</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>
                {(() => {
                  const nombreCategoria = categoriaMap[p.categoriaId];
                  console.log(
                    `🔍 Producto ${p.nombre}: categoriaId=${
                      p.categoriaId
                    }, nombre encontrado=${nombreCategoria || "NO ENCONTRADO"}`
                  );
                  console.log(`🗺️ Mapa actual:`, categoriaMap);
                  return nombreCategoria || `Categoría ${p.categoriaId}`;
                })()}
              </td>
              <td>{p.nombre}</td>
              <td>{p.descripcion}</td>
              <td>S/ {p.precioVenta}</td>
              <td>S/ {p.costoProduccion}</td>
              <td>{p.cantidad}</td>
              <td>{p.disponible ? "Sí" : "No"}</td>
              <td>
                {p.imagenUrl && (
                  <img
                    src={p.imagenUrl}
                    alt={p.nombre}
                    className="product-image"
                  />
                )}
              </td>
              <td>
                <button
                  className="theme-button"
                  onClick={() => openModal("edit", p)}>
                  Editar
                </button>
                <button
                  className="theme-button"
                  onClick={() => openModal("delete", p)}>
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h3 className="modal-title">
              {modalType === "create"
                ? "🍰 Crear Nuevo Producto"
                : modalType === "edit"
                ? "✏️ Editar Producto"
                : "🗑️ Eliminar Producto"}
            </h3>

            {(modalType === "create" || modalType === "edit") && (
              <form onSubmit={handleSubmit} className="product-form">
                <div className="form-group">
                  <label htmlFor="categoriaId" className="form-label">
                    Categoría *
                  </label>
                  <select
                    id="categoriaId"
                    name="categoriaId"
                    value={form.categoriaId}
                    onChange={handleChange}
                    required
                    className="form-select">
                    <option value="">Seleccionar Categoría</option>
                    {categorias.length > 0 ? (
                      categorias.map((categoria) => (
                        <option key={categoria.id} value={categoria.id}>
                          {categoria.nombre}
                        </option>
                      ))
                    ) : (
                      <option disabled>Cargando categorías...</option>
                    )}
                  </select>
                  {/* Debug: Mostrar información de categorías - Comentado para producción */}
                  {/* 
                  <small style={{ color: "#666", fontSize: "12px" }}>
                    Debug: {categorias.length} categorías cargadas
                    {categorias.length > 0 && (
                      <span> - {categorias.map((c) => c.nombre).join(", ")}</span>
                    )}
                  </small>
                  */}
                </div>

                <div className="form-group">
                  <label htmlFor="nombre" className="form-label">
                    Nombre del Producto *
                  </label>
                  <input
                    id="nombre"
                    name="nombre"
                    type="text"
                    placeholder="Ej: Torta de Chocolate"
                    value={form.nombre}
                    onChange={handleChange}
                    required
                    className="form-input"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="descripcion" className="form-label">
                    Descripción
                  </label>
                  <textarea
                    id="descripcion"
                    name="descripcion"
                    placeholder="Describe las características del producto..."
                    value={form.descripcion}
                    onChange={handleChange}
                    className="form-textarea"
                    rows="3"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="precioVenta" className="form-label">
                    Precio de Venta (S/) *
                  </label>
                  <input
                    id="precioVenta"
                    name="precioVenta"
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="120.00"
                    value={form.precioVenta}
                    onChange={handleChange}
                    required
                    className="form-input"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="costoProduccion" className="form-label">
                    Costo de Producción (S/) *
                  </label>
                  <input
                    id="costoProduccion"
                    name="costoProduccion"
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="80.00"
                    value={form.costoProduccion}
                    onChange={handleChange}
                    required
                    className="form-input"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="imagenUrl" className="form-label">
                    URL de la Imagen
                  </label>
                  <input
                    id="imagenUrl"
                    name="imagenUrl"
                    type="url"
                    placeholder="https://ejemplo.com/imagen.jpg"
                    value={form.imagenUrl}
                    onChange={handleChange}
                    className="form-input"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="cantidad" className="form-label">
                    Cantidad en Stock *
                  </label>
                  <input
                    id="cantidad"
                    name="cantidad"
                    type="number"
                    min="0"
                    placeholder="10"
                    value={form.cantidad}
                    onChange={handleChange}
                    required
                    className="form-input"
                  />
                </div>

                <div className="modal-actions">
                  <button type="submit" className="theme-button">
                    {modalType === "create" ? "Crear" : "Actualizar"}
                  </button>
                  <button
                    type="button"
                    className="theme-button"
                    onClick={closeModal}>
                    Cancelar
                  </button>
                </div>
              </form>
            )}

            {modalType === "delete" && (
              <div className="delete-confirm">
                <p>
                  ¿Eliminar el producto <strong>{currentProduct.nombre}</strong>
                  ?
                </p>
                <div className="modal-actions">
                  <button onClick={handleSubmit} className="theme-button">
                    Sí, eliminar
                  </button>
                  <button onClick={closeModal} className="theme-button">
                    Cancelar
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
