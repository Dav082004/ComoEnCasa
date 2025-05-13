// src/pages/Tienda.js

import React from "react";
import { useCart } from "../context/CartContext";       // Contexto único
import Productos from "../components/Productos";        // Tu componente de lista
import { productosData } from "../data/productosData";  // Tu array de 24 productos

const Tienda = () => {
  const { addToCart } = useCart(); // Desestructuramos solo lo que usamos

  // Envío directo a la función del contexto
  const handleAgregarAlCarrito = (producto, cantidad) => {
    addToCart(producto, cantidad);
  };

  return (
    <div className="tienda-container">
      <h2>Tienda</h2>
      <Productos
        productos={productosData}
        onAgregarAlCarrito={handleAgregarAlCarrito}
      />
    </div>
  );
};

export default Tienda;



