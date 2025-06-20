// src/context/CartContext.js
import React, { createContext, useContext, useState } from "react";
import { toast } from "react-toastify";
import ProductToast from "../components/products/ProductToast";
import { useAuth } from "./AuthContext";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({});
  const [isAdding, setIsAdding] = useState(false);
  const { user } = useAuth();

  const addToCart = async (producto, cantidad = 1) => {
    // Evitar múltiples clics rápidos
    if (isAdding) return false;

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

      return true; // Indicar éxito
    } catch (error) {
      console.error("Error al agregar producto al carrito:", error);
      toast.error("Error al agregar producto al carrito");
      return false; // Indicar fallo
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

  // VALIDACIÓN DE STOCK ANTES DEL CHECKOUT
  const validateCartStock = async () => {
    const errors = [];
    const cartItems = Object.values(cart);

    for (const item of cartItems) {
      try {
        // Aquí deberías importar y usar el servicio de productos
        // para verificar el stock actual del producto
        const response = await fetch(
          `http://localhost:8081/api/productos/${item.id}`
        );
        if (response.ok) {
          const producto = await response.json();

          if (!producto.disponible) {
            errors.push(`${item.nombre} ya no está disponible`);
          } else if (producto.cantidad < item.quantity) {
            errors.push(
              `${item.nombre}: Solo ${producto.cantidad} unidades disponibles (tienes ${item.quantity} en el carrito)`
            );
          }
        }
      } catch (error) {
        console.error(`Error validando stock de ${item.nombre}:`, error);
        errors.push(`No se pudo verificar el stock de ${item.nombre}`);
      }
    }

    return errors;
  };

  // FUNCIÓN PARA ACTUALIZAR CARRITO CON STOCK DISPONIBLE
  const syncCartWithStock = async () => {
    const cartItems = Object.values(cart);
    let hasChanges = false;
    const updatedCart = { ...cart };

    for (const item of cartItems) {
      try {
        const response = await fetch(
          `http://localhost:8081/api/productos/${item.id}`
        );
        if (response.ok) {
          const producto = await response.json();

          if (!producto.disponible) {
            // Eliminar producto no disponible
            delete updatedCart[`${item.id}`];
            hasChanges = true;
            toast.warning(
              `${item.nombre} ha sido removido del carrito (no disponible)`
            );
          } else if (producto.cantidad < item.quantity) {
            // Ajustar cantidad al stock disponible
            if (producto.cantidad > 0) {
              updatedCart[`${item.id}`].quantity = producto.cantidad;
              hasChanges = true;
              toast.warning(
                `Cantidad de ${item.nombre} ajustada a ${producto.cantidad} unidades (stock disponible)`
              );
            } else {
              delete updatedCart[`${item.id}`];
              hasChanges = true;
              toast.warning(
                `${item.nombre} ha sido removido del carrito (sin stock)`
              );
            }
          }
        }
      } catch (error) {
        console.error(`Error sincronizando ${item.nombre}:`, error);
      }
    }

    if (hasChanges) {
      setCart(updatedCart);
    }

    return hasChanges;
  };

  // VALIDACIÓN DE AUTENTICACIÓN ANTES DEL CHECKOUT
  const validateAuthForCheckout = () => {
    if (!user) {
      return {
        isValid: false,
        message: "Debes iniciar sesión para proceder con la compra",
        action: "login",
      };
    }

    // Verificar si la cuenta está activada (si implementas activación)
    if (user.activado === false) {
      return {
        isValid: false,
        message: "Tu cuenta debe estar activada para realizar compras",
        action: "activate",
      };
    }

    return { isValid: true };
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
        validateCartStock,
        syncCartWithStock,
        validateAuthForCheckout,
      }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
