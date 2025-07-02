import React, { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Cart, Truck, Shield, Clock } from "react-bootstrap-icons";
import {
  getProductoById,
  getProductosByCategoria,
} from "../../services/productoService";
import { useCart } from "../../context/CartContext";
import "./styles/ProductDetail.css";
import RelatedProducts from "./RelatedProducts";

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const { addToCart, isAdding } = useCart();

  // Estados
  const [producto, setProducto] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [comentarios, setComentarios] = useState("");
  const [relacionados, setRelacionados] = useState([]);
  const [loading, setLoading] = useState(true);
  const [relatedLoading, setRelatedLoading] = useState(false);
  const [error, setError] = useState(null);

  // Cargar producto principal
  const loadProduct = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await getProductoById(id);
      if (!data) {
        setError("Producto no encontrado");
        return;
      }

      setProducto(data);

      // Resetear cantidad si es mayor al stock disponible
      if (data.cantidad && quantity > data.cantidad) {
        setQuantity(Math.min(1, data.cantidad));
      } else if (!data.cantidad || data.cantidad === 0) {
        setQuantity(1); // Resetear aunque no haya stock
      }

      // Cargar productos relacionados
      if (data.categoriaId) {
        setRelatedLoading(true);
        try {
          const rel = await getProductosByCategoria(data.categoriaId);
          const filteredRelated = rel
            .filter((p) => p.id !== data.id)
            .slice(0, 4);
          setRelacionados(filteredRelated);
        } catch (relatedError) {
          console.error("Error cargando productos relacionados:", relatedError);
        } finally {
          setRelatedLoading(false);
        }
      }
    } catch (err) {
      console.error("Error cargando producto:", err);
      setError("Error al cargar el producto");
    } finally {
      setLoading(false);
    }
  }, [id, quantity]);

  useEffect(() => {
    loadProduct();
  }, [loadProduct]);
  // Manejar adición al carrito
  const handleAddToCart = useCallback(() => {
    const productToAdd = {
      ...producto,
      comentarios,
    };

    addToCart(productToAdd, quantity);
  }, [producto, quantity, comentarios, addToCart]);

  // Manejar error de imagen
  const handleImageError = useCallback((e) => {
    e.target.src = "/placeholder-image.jpg";
  }, []);

  // Estados de carga y error
  if (loading) {
    return (
      <div className="container-fluid py-5">
        <div className="row justify-content-center">
          <div className="col-12 text-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Cargando...</span>
            </div>
            <p className="mt-3">Cargando producto...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container-fluid py-5">
        <div className="row justify-content-center">
          <div className="col-md-6 text-center">
            <div className="alert alert-danger" role="alert">
              <h4 className="alert-heading">¡Oops!</h4>
              <p>{error}</p>
              <button
                className="btn btn-primary"
                onClick={() => navigate("/productos")}>
                Volver a Productos
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!producto) return null;
  return (
    <main className="container-fluid py-4">
      {/* Botón de volver a productos */}
      <div className="back-button-container">
        <button
          className="back-button"
          onClick={() => navigate("/productos")}
          aria-label="Volver a la página de productos">
          <span className="back-arrow">←</span>
          <span className="back-text">Volver a Productos</span>
        </button>
      </div>

      {/* Contenido principal del producto */}
      <div className="product-detail-container">
        <div className="row g-4">
          {/* Imagen del producto */}
          <div className="col-lg-6">
            <div className="product-image-container">
              <img
                className="product-detail-image img-fluid"
                src={producto.imagenUrl}
                alt={`Imagen de ${producto.nombre}`}
                onError={handleImageError}
                loading="eager"
              />
            </div>
          </div>

          {/* Información del producto */}
          <div className="col-lg-6">
            <div className="product-detail-info">
              {/* Header del producto */}
              <header className="product-detail-header mb-3">
                <h1 className="product-detail-title">{producto.nombre}</h1>
                <div className="product-detail-price">
                  S/. {producto.precioVenta.toFixed(2)}
                </div>
              </header>
              {/* Descripción */}
              <div className="product-detail-desc mb-4">
                <p>{producto.descripcion}</p>
              </div>
              {/* Stock disponible */}
              <div className="stock-info mb-3">
                <span className="stock-label">Stock disponible: </span>
                <span
                  className={`stock-quantity ${
                    producto.cantidad <= 5 ? "stock-low" : "stock-available"
                  }`}>
                  {producto.cantidad || 0} unidades
                </span>
                {producto.cantidad <= 5 && producto.cantidad > 0 && (
                  <small className="text-warning d-block">
                    ¡Pocas unidades disponibles!
                  </small>
                )}
                {producto.cantidad === 0 && (
                  <small className="text-danger d-block">
                    Producto agotado
                  </small>
                )}
              </div>
              <hr className="my-4" /> {/* Formulario de opciones */}
              <form
                className="product-detail-form"
                onSubmit={(e) => e.preventDefault()}>
                <div className="row g-3">
                  {/* Selector de cantidad */}
                  <div className="col-md-6">
                    <label
                      htmlFor="quantity-select"
                      className="form-label product-detail-label">
                      Cantidad
                    </label>
                    <select
                      id="quantity-select"
                      className="form-select product-detail-select"
                      value={quantity}
                      onChange={(e) => setQuantity(Number(e.target.value))}
                      disabled={!producto.cantidad || producto.cantidad === 0}>
                      {Array.from(
                        { length: Math.min(producto.cantidad || 0, 10) },
                        (_, i) => i + 1
                      ).map((n) => (
                        <option key={n} value={n}>
                          {n}
                        </option>
                      ))}
                    </select>
                    {(!producto.cantidad || producto.cantidad === 0) && (
                      <small className="text-danger">
                        Producto no disponible
                      </small>
                    )}
                  </div>

                  {/* Campo de comentarios */}
                  <div className="col-12">
                    <label
                      htmlFor="comments-input"
                      className="form-label product-detail-label">
                      Comentarios para el pedido
                    </label>
                    <textarea
                      id="comments-input"
                      className="form-control product-detail-textarea"
                      rows="3"
                      placeholder="Escribe aquí cualquier comentario especial para tu pedido..."
                      value={comentarios}
                      onChange={(e) => setComentarios(e.target.value)}
                      maxLength="500"
                    />
                    <small className="text-muted">
                      {comentarios.length}/500 caracteres
                    </small>
                  </div>
                </div>

                {/* Botón de agregar al carrito */}
                <div className="d-grid gap-2 mt-4">
                  <button
                    type="button"
                    className="btn product-detail-btn btn-lg"
                    onClick={handleAddToCart}
                    disabled={
                      isAdding || !producto.cantidad || producto.cantidad === 0
                    }>
                    <Cart className="me-2" size={18} />
                    {isAdding
                      ? "Agregando..."
                      : !producto.cantidad || producto.cantidad === 0
                      ? "Sin Stock"
                      : "Agregar al Carrito"}
                  </button>
                </div>
              </form>
              {/* Información adicional del producto */}{" "}
              <div className="product-additional-info mt-4">
                <div className="row text-center">
                  <div className="col-4">
                    <Truck
                      className="text-primary mb-2 d-block mx-auto"
                      size={24}
                    />
                    <small>Entrega gratis</small>
                  </div>
                  <div className="col-4">
                    <Shield
                      className="text-success mb-2 d-block mx-auto"
                      size={24}
                    />
                    <small>Producto fresco</small>
                  </div>
                  <div className="col-4">
                    <Clock
                      className="text-warning mb-2 d-block mx-auto"
                      size={24}
                    />
                    <small>Preparado al día</small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Productos relacionados */}
      <RelatedProducts productos={relacionados} loading={relatedLoading} />
    </main>
  );
};

export default ProductDetail;
