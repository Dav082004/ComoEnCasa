// src/pages/ProductosPage.js
import React from "react";
import { useCart } from "../context/CartContext";
import Productos from "../components/Productos";
import { productosData } from "../data/productosData";

export default function ProductosPage() {
  const { addToCart } = useCart();

  return (
    <div className="productos-page">
      <h1 className="productos-title">Nuestros Productos</h1>
      <Productos
        productos={productosData}
        onAgregarAlCarrito={addToCart}
      />
    </div>
  );
}
