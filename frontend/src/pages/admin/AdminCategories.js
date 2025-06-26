import React, { useState } from "react";
import "./styles/Admin.css";
import { useCategorias } from "../../context/CategoriaContext";

export default function AdminCategories() {
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState("");
  const [currentCategory, setCurrentCategory] = useState(null);
  const [form, setForm] = useState({
    nombre: "",
    descripcion: "",
  }); // Usar el contexto de categorías
  const {
    categorias: categories,
    loading,
    error,
    isReconnecting,
    createCategoria,
    updateCategoria,
    deleteCategoria,
  } = useCategorias();
  // No necesitamos cargar categorías manualmente, el contexto lo hace

  const openModal = (type, category = null) => {
    setModalType(type);
    setCurrentCategory(category);

    if (type === "edit" && category) {
      setForm({
        nombre: category.nombre,
        descripcion: category.descripcion || "",
      });
    } else {
      setForm({
        nombre: "",
        descripcion: "",
      });
    }

    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setCurrentCategory(null);
    setForm({
      nombre: "",
      descripcion: "",
    });
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (modalType === "delete") {
      // No necesita validación del formulario para eliminar
    } else if (!validateForm()) {
      alert("El nombre debe tener al menos 2 caracteres.");
      return;
    }

    const payload = {
      nombre: form.nombre.trim(),
      descripcion: form.descripcion.trim(),
    };

    try {
      if (modalType === "create") {
        console.log("Creando nueva categoría:", payload);
        await createCategoria(payload);
      } else if (modalType === "edit" && currentCategory) {
        console.log("Actualizando categoría:", currentCategory.id, payload);
        await updateCategoria(currentCategory.id, payload);
      } else if (modalType === "delete" && currentCategory) {
        console.log("Eliminando categoría:", currentCategory.id);
        await deleteCategoria(currentCategory.id);
      }

      closeModal();
      // No necesitamos llamar loadCategories, el contexto se actualiza automáticamente
    } catch (err) {
      console.error("Error en acción CRUD:", err);
      alert("Error al realizar la operación. Por favor, inténtelo de nuevo.");
    }
  };

  const validateForm = () => {
    return form.nombre.trim().length >= 2;
  };
  return (
    <div className="page-container">
      <h2 className="theme-header">🏷️ Gestión de Categorías</h2>
      <div className="admin-actions">
        <button
          className="theme-button"
          onClick={() => openModal("create")}
          disabled={loading}>
          ➕ Nueva Categoría
        </button>
      </div>
      {/* Mostrar error de conexión si existe */}
      {error && (
        <div
          className="error-container"
          style={{
            padding: "15px",
            backgroundColor: "#fee",
            border: "1px solid #fcc",
            borderRadius: "5px",
            margin: "10px 0",
          }}>
          <p style={{ color: "#c33", margin: "0" }}>⚠️ {error}</p>
          {isReconnecting && (
            <p style={{ color: "#666", margin: "5px 0 0 0", fontSize: "14px" }}>
              🔄 Intentando reconectar...
            </p>
          )}
        </div>
      )}
      {loading && categories.length === 0 ? (
        <div className="loading-container">
          <div className="spinner-border text-pink" role="status">
            <span className="sr-only">Cargando...</span>
          </div>{" "}
          <p>Cargando categorías...</p>
        </div>
      ) : (
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Productos Asociados</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id}>
                <td>
                  <span className="category-id">{category.id}</span>
                </td>
                <td>
                  <strong className="category-name">{category.nombre}</strong>
                </td>
                <td>
                  <span className="category-description">
                    {category.descripcion || "Sin descripción"}
                  </span>
                </td>
                <td>
                  <span className="product-count">
                    {category.cantidadProductos || 0} productos
                  </span>
                </td>
                <td>
                  <button
                    className="theme-button"
                    onClick={() => openModal("edit", category)}
                    disabled={loading}>
                    ✏️ Editar
                  </button>
                  <button
                    className="theme-button"
                    onClick={() => openModal("delete", category)}
                    disabled={loading}>
                    🗑️ Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {categories.length === 0 && !loading && (
        <div className="empty-state">
          <p>No hay categorías registradas.</p>
          <button className="theme-button" onClick={() => openModal("create")}>
            Crear Primera Categoría
          </button>
        </div>
      )}
      {showModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h3 className="modal-title">
              {modalType === "create"
                ? "🏷️ Crear Nueva Categoría"
                : modalType === "edit"
                ? "✏️ Editar Categoría"
                : "🗑️ Eliminar Categoría"}
            </h3>

            {(modalType === "create" || modalType === "edit") && (
              <form onSubmit={handleSubmit} className="product-form">
                <div className="form-group">
                  <label htmlFor="nombre" className="form-label">
                    Nombre de la Categoría *
                  </label>
                  <input
                    id="nombre"
                    name="nombre"
                    type="text"
                    placeholder="Ej: Tortas, Postres, Eventos..."
                    value={form.nombre}
                    onChange={handleChange}
                    required
                    minLength="2"
                    maxLength="50"
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
                    placeholder="Describe el tipo de productos de esta categoría..."
                    value={form.descripcion}
                    onChange={handleChange}
                    maxLength="200"
                    className="form-textarea"
                    rows="3"
                  />
                </div>

                <div className="modal-actions">
                  <button
                    type="submit"
                    className="theme-button"
                    disabled={loading || !validateForm()}>
                    {loading
                      ? "Procesando..."
                      : modalType === "create"
                      ? "Crear"
                      : "Actualizar"}
                  </button>
                  <button
                    type="button"
                    className="theme-button"
                    onClick={closeModal}
                    disabled={loading}>
                    Cancelar
                  </button>
                </div>
              </form>
            )}

            {modalType === "delete" && (
              <div className="delete-confirm">
                <p>
                  ¿Estás seguro de que quieres eliminar la categoría{" "}
                  <strong>{currentCategory?.nombre}</strong>?
                </p>
                <p className="warning-text">
                  ⚠️ Esta acción no se puede deshacer. Los productos asociados
                  quedarán sin categoría.
                </p>
                <div className="modal-actions">
                  <button
                    onClick={handleSubmit}
                    className="theme-button"
                    disabled={loading}>
                    {loading ? "Eliminando..." : "Sí, eliminar"}
                  </button>
                  <button
                    onClick={closeModal}
                    className="theme-button"
                    disabled={loading}>
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
