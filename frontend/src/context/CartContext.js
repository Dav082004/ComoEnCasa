// src/context/CartContext.js
import React, { createContext, useContext, useState } from 'react';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({});

  // cantidad puede ser positiva o negativa
  const addToCart = (producto, cantidad = 1) => {
    setCart((prevCart) => {
      const nuevoCarrito = { ...prevCart };
      if (nuevoCarrito[producto.id]) {
        nuevoCarrito[producto.id].cantidad += cantidad;
        if (nuevoCarrito[producto.id].cantidad <= 0) {
          delete nuevoCarrito[producto.id];
        }
      } else if (cantidad > 0) {
        nuevoCarrito[producto.id] = { ...producto, cantidad };
      }
      return nuevoCarrito;
    });
  };

  const removeFromCart = (id) => {
    setCart((prevCart) => {
      const nuevoCarrito = { ...prevCart };
      delete nuevoCarrito[id];
      return nuevoCarrito;
    });
  };

  return (
    <CartContext.Provider value={{ cart, setCart, addToCart, removeFromCart }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
