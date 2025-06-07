// src/context/CartContext.js
import React, { createContext, useContext, useState } from "react";
import { toast } from "react-toastify";
import ProductToast from "../components/products/ProductToast";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({});
  const [isAdding, setIsAdding] = useState(false);

  const addToCart = async (producto, cantidad = 1) => {
    // Evitar múltiples clics rápidos
    if (isAdding) return;

    setIsAdding(true);

    try {
      setCart((prevCart) => {
        const nuevoCarrito = { ...prevCart };
        const productKey = `${producto.id}`;

        if (nuevoCarrito[productKey]) {
          // CORREGIDO: No sumar la cantidad, sino establecer la cantidad exacta seleccionada
          nuevoCarrito[productKey].quantity = cantidad;
          // Actualizar comentarios si se proporcionan
          if (producto.comentarios) {
            nuevoCarrito[productKey].comentarios = producto.comentarios;
          }
        } else {
          nuevoCarrito[productKey] = {
            ...producto,
            quantity: cantidad,
          };
        }

        return nuevoCarrito;
      });

      // Mostrar notificación con el componente ProductToast
      toast.success(<ProductToast producto={producto} cantidad={cantidad} />, {
        toastId: `product-${producto.id}-${Date.now()}`,
        autoClose: 3000, // 3 segundos para buena visibilidad
        closeOnClick: true,
        pauseOnHover: true, // Permitir pausar para leer
        hideProgressBar: false,
        draggable: true,
      });
    } catch (error) {
      console.error("Error al agregar producto al carrito:", error);
      toast.error("Error al agregar el producto al carrito");
    } finally {
      // Reactivar después de un breve delay
      setTimeout(() => {
        setIsAdding(false);
      }, 500);
    }
  };

  // NUEVAS FUNCIONES PARA MEJOR MANEJO DEL CARRITO
  const updateQuantity = (productId, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(productId);
      return;
    }

    setCart((prevCart) => {
      const nuevoCarrito = { ...prevCart };
      const productKey = `${productId}`;

      if (nuevoCarrito[productKey]) {
        nuevoCarrito[productKey].quantity = newQuantity;
      }

      return nuevoCarrito;
    });
  };

  const removeFromCart = (productId) => {
    setCart((prevCart) => {
      const nuevoCarrito = { ...prevCart };
      delete nuevoCarrito[`${productId}`];
      return nuevoCarrito;
    });
  };

  const clearCart = () => {
    setCart({});
  };

  const getTotalItems = () => {
    return Object.values(cart).reduce(
      (total, item) => total + item.quantity,
      0
    );
  };

  const getTotalPrice = () => {
    return Object.values(cart).reduce(
      (total, item) =>
        total + (item.precioVenta || item.precio || 0) * item.quantity,
      0
    );
  };

  return (
    <CartContext.Provider
      value={{
        cart,
        setCart,
        addToCart,
        updateQuantity,
        removeFromCart,
        clearCart,
        getTotalItems,
        getTotalPrice,
        isAdding,
      }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
