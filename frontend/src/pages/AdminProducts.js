import React, { useState, useEffect } from 'react';
import '../styles/Admin.css';
import {
  getProductos,
  createProducto,
  updateProducto,
  deleteProducto
} from '../services/productoService';

export default function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState(''); // 'create' | 'edit' | 'delete'
  const [currentProduct, setCurrentProduct] = useState(null);
  const [form, setForm] = useState({
    categoriaId: '',
    nombre: '',
    descripcion: '',
    precioVenta: '',
    costoProduccion: '',
    imagenUrl: '',
    cantidad: ''
  });

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const data = await getProductos();
      setProducts(data);
    } catch (err) {
      console.error('Error cargando productos:', err);
    }
  };

  const openModal = (type, product = null) => {
    setModalType(type);
    setCurrentProduct(product);

    if (type === 'edit' && product) {
      setForm({
        categoriaId: product.categoriaId,
        nombre: product.nombre,
        descripcion: product.descripcion || '',
        precioVenta: product.precioVenta,
        costoProduccion: product.costoProduccion,
        imagenUrl: product.imagenUrl || '',
        cantidad: product.cantidad
      });
    } else {
      setForm({
        categoriaId: '',
        nombre: '',
        descripcion: '',
        precioVenta: '',
        costoProduccion: '',
        imagenUrl: '',
        cantidad: ''
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
      cantidad: Number(form.cantidad)
    };

    try {
      if (modalType === 'create') {
        await createProducto(payload);
      } else if (modalType === 'edit' && currentProduct) {
        await updateProducto(currentProduct.id, payload);
      } else if (modalType === 'delete' && currentProduct) {
        await deleteProducto(currentProduct.id);
      }
      closeModal();
      loadProducts();
    } catch (err) {
      console.error('Error en acción CRUD:', err);
    }
  };

  return (
    <div className="page-container">
      <h2 className="theme-header">Gestión de Productos</h2>
      <button className="theme-button" onClick={() => openModal('create')}>
        Nuevo Producto
      </button>

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Categoría ID</th>
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
              <td>{p.id}</td>
              <td>{p.categoriaId}</td>
              <td>{p.nombre}</td>
              <td>{p.descripcion}</td>
              <td>{p.precioVenta}</td>
              <td>{p.costoProduccion}</td>
              <td>{p.cantidad}</td>
              <td>{p.disponible ? 'Sí' : 'No'}</td>
              <td>
                {p.imagenUrl && (
                  <img src={p.imagenUrl} alt={p.nombre} className="product-image" />
                )}
              </td>
              <td>
                <button className="theme-button" onClick={() => openModal('edit', p)}>
                  Editar
                </button>
                <button className="theme-button" onClick={() => openModal('delete', p)}>
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
            <h3>
              {modalType === 'create'
                ? 'Crear Producto'
                : modalType === 'edit'
                ? 'Editar Producto'
                : 'Eliminar Producto'}
            </h3>

            {(modalType === 'create' || modalType === 'edit') && (
              <form onSubmit={handleSubmit} className="product-form">
                <input
                  name="categoriaId"
                  placeholder="Categoría ID"
                  value={form.categoriaId}
                  onChange={handleChange}
                  required
                />
                <input
                  name="nombre"
                  placeholder="Nombre"
                  value={form.nombre}
                  onChange={handleChange}
                  required
                />
                <textarea
                  name="descripcion"
                  placeholder="Descripción"
                  value={form.descripcion}
                  onChange={handleChange}
                />
                <input
                  name="precioVenta"
                  placeholder="Precio Venta"
                  value={form.precioVenta}
                  onChange={handleChange}
                  required
                />
                <input
                  name="costoProduccion"
                  placeholder="Costo Producción"
                  value={form.costoProduccion}
                  onChange={handleChange}
                  required
                />
                <input
                  name="imagenUrl"
                  placeholder="URL Imagen"
                  value={form.imagenUrl}
                  onChange={handleChange}
                />
                <input
                  name="cantidad"
                  placeholder="Cantidad"
                  value={form.cantidad}
                  onChange={handleChange}
                  required
                />
                <div className="modal-actions">
                  <button type="submit" className="theme-button">
                    {modalType === 'create' ? 'Crear' : 'Actualizar'}
                  </button>
                  <button type="button" className="theme-button" onClick={closeModal}>
                    Cancelar
                  </button>
                </div>
              </form>
            )}

            {modalType === 'delete' && (
              <div className="delete-confirm">
                <p>
                  ¿Eliminar el producto <strong>{currentProduct.nombre}</strong>?
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