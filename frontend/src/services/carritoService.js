// src/services/carritoService.js
const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8081";

class CarritoService {
  /**
   * Agregar producto al carrito en el backend
   */
  async agregarProducto(productoId, cantidad, comentarios = "") {
    try {
      const response = await fetch(`${API_URL}/api/carrito/agregar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Para enviar cookies de sesión
        body: JSON.stringify({
          productoId,
          cantidad,
          comentarios,
        }),
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error al agregar producto al carrito:", error);
      throw error;
    }
  }

  /**
   * Actualizar cantidad de un producto
   */
  async actualizarCantidad(productoId, cantidad) {
    try {
      const response = await fetch(
        `${API_URL}/api/carrito/actualizar/${productoId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({ cantidad }),
        }
      );

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error al actualizar cantidad:", error);
      throw error;
    }
  }

  /**
   * Eliminar producto del carrito
   */
  async eliminarProducto(productoId) {
    try {
      const response = await fetch(
        `${API_URL}/api/carrito/eliminar/${productoId}`,
        {
          method: "DELETE",
          credentials: "include",
        }
      );

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error al eliminar producto:", error);
      throw error;
    }
  }

  /**
   * Obtener carrito actual
   */
  async obtenerCarrito() {
    try {
      const response = await fetch(`${API_URL}/api/carrito`, {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error al obtener carrito:", error);
      throw error;
    }
  }

  /**
   * Limpiar carrito
   */
  async limpiarCarrito() {
    try {
      const response = await fetch(`${API_URL}/api/carrito/limpiar`, {
        method: "DELETE",
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error al limpiar carrito:", error);
      throw error;
    }
  }

  /**
   * Obtener total de items
   */
  async obtenerTotalItems() {
    try {
      const response = await fetch(`${API_URL}/api/carrito/total-items`, {
        method: "GET",
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`Error HTTP: ${response.status}`);
      }

      const data = await response.json();
      return data.totalItems;
    } catch (error) {
      console.error("Error al obtener total de items:", error);
      throw error;
    }
  }
}

export default new CarritoService();
