// src/context/CartContext.js
import React, { createContext, useContext, useState } from 'react';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({});

  const addToCart = (producto) => {
    setCart((prevCart) => {
      const nuevoCarrito = { ...prevCart };
      if (nuevoCarrito[producto.id]) {
        nuevoCarrito[producto.id].cantidad += 1;
      } else {
        nuevoCarrito[producto.id] = { ...producto, cantidad: 1 };
      }
      return nuevoCarrito;
    });
  };

  return (
    <CartContext.Provider value={{ cart, setCart, addToCart }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);

