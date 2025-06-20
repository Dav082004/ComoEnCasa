// src/services/categoriaService.js
import axios from "axios";

const API_URL = "http://localhost:8081/api/categorias";

/**
 * Servicio para gestión de categorías de productos
 * Siguiendo patrones DAO y buenas prácticas de arquitectura
 */
export const categoriaService = {
  /**
   * Obtiene todas las categorías disponibles
   * @returns {Promise<Array>} Lista de categorías
   */
  async getAllCategorias() {
    try {
      const { data } = await axios.get(API_URL);
      return data;
    } catch (error) {
      console.error("Error al obtener categorías:", error);
      throw new Error("No se pudieron cargar las categorías");
    }
  },

  /**
   * Obtiene todas las categorías con conteo de productos asociados
   * @returns {Promise<Array>} Lista de categorías con cantidadProductos
   */
  async getCategoriasConConteo() {
    try {
      const { data } = await axios.get(`${API_URL}/con-conteo`);
      return data;
    } catch (error) {
      console.error("Error al obtener categorías con conteo:", error);
      throw new Error("No se pudieron cargar las categorías con conteo");
    }
  },

  /**
   * Obtiene una categoría por su ID
   * @param {number} id - ID de la categoría
   * @returns {Promise<Object>} Datos de la categoría
   */
  async getCategoriaById(id) {
    try {
      const { data } = await axios.get(`${API_URL}/${id}`);
      return data;
    } catch (error) {
      console.error(`Error al obtener categoría ${id}:`, error);
      throw new Error(`No se pudo cargar la categoría con ID ${id}`);
    }
  },
  /**
   * Crea un mapa de ID -> Nombre para uso eficiente en el frontend
   * @returns {Promise<Object>} Mapa de categorías {id: nombre}
   */
  async getCategoriaMap() {
    try {
      const categorias = await this.getAllCategorias();
      const categoriaMap = {};

      categorias.forEach((categoria) => {
        categoriaMap[categoria.id] = categoria.nombre;
      });

      return categoriaMap;
    } catch (error) {
      console.error("Error al crear mapa de categorías:", error);
      // Retornar mapa de fallback basado en la BD
      return {
        1: "Tortas",
        2: "Eventos",
        3: "Postres",
      };
    }
  },

  /**
   * Crea una nueva categoría
   * @param {Object} categoriaData - Datos de la nueva categoría
   * @param {string} categoriaData.nombre - Nombre de la categoría
   * @param {string} [categoriaData.descripcion] - Descripción opcional
   * @returns {Promise<Object>} Categoría creada
   */
  async createCategoria(categoriaData) {
    try {
      // Validación básica
      if (!categoriaData.nombre || categoriaData.nombre.trim().length < 2) {
        throw new Error(
          "El nombre de la categoría es requerido y debe tener al menos 2 caracteres"
        );
      }

      const payload = {
        nombre: categoriaData.nombre.trim(),
        descripcion: categoriaData.descripcion
          ? categoriaData.descripcion.trim()
          : "",
      };

      const { data } = await axios.post(API_URL, payload);
      return data;
    } catch (error) {
      console.error("Error al crear categoría:", error);
      if (error.response?.status === 409) {
        throw new Error("Ya existe una categoría con ese nombre");
      }
      throw new Error(error.message || "No se pudo crear la categoría");
    }
  },

  /**
   * Actualiza una categoría existente
   * @param {number} id - ID de la categoría a actualizar
   * @param {Object} categoriaData - Nuevos datos de la categoría
   * @returns {Promise<Object>} Categoría actualizada
   */
  async updateCategoria(id, categoriaData) {
    try {
      // Validación básica
      if (!categoriaData.nombre || categoriaData.nombre.trim().length < 2) {
        throw new Error(
          "El nombre de la categoría es requerido y debe tener al menos 2 caracteres"
        );
      }

      const payload = {
        nombre: categoriaData.nombre.trim(),
        descripcion: categoriaData.descripcion
          ? categoriaData.descripcion.trim()
          : "",
      };

      const { data } = await axios.put(`${API_URL}/${id}`, payload);
      return data;
    } catch (error) {
      console.error(`Error al actualizar categoría ${id}:`, error);
      if (error.response?.status === 404) {
        throw new Error("Categoría no encontrada");
      }
      if (error.response?.status === 409) {
        throw new Error("Ya existe una categoría con ese nombre");
      }
      throw new Error(error.message || "No se pudo actualizar la categoría");
    }
  },

  /**
   * Elimina una categoría
   * @param {number} id - ID de la categoría a eliminar
   * @returns {Promise<void>}
   */
  async deleteCategoria(id) {
    try {
      await axios.delete(`${API_URL}/${id}`);
    } catch (error) {
      console.error(`Error al eliminar categoría ${id}:`, error);
      if (error.response?.status === 404) {
        throw new Error("Categoría no encontrada");
      }
      if (error.response?.status === 409) {
        throw new Error(
          "No se puede eliminar la categoría porque tiene productos asociados"
        );
      }
      throw new Error(error.message || "No se pudo eliminar la categoría");
    }
  },

  /**
   * Verifica si una categoría existe por nombre
   * @param {string} nombre - Nombre de la categoría
   * @returns {Promise<boolean>} True si existe, false si no
   */
  async existeCategoria(nombre) {
    try {
      const { data } = await axios.get(`${API_URL}/existe`, {
        params: { nombre: nombre.trim() },
      });
      return data;
    } catch (error) {
      console.error("Error al verificar existencia de categoría:", error);
      return false;
    }
  },
};

// Exports individuales para compatibilidad
export const getAllCategorias = categoriaService.getAllCategorias;
export const getCategoriaById = categoriaService.getCategoriaById;
export const getCategoriaMap = categoriaService.getCategoriaMap;
export const createCategoria = categoriaService.createCategoria;
export const updateCategoria = categoriaService.updateCategoria;
export const deleteCategoria = categoriaService.deleteCategoria;
export const existeCategoria = categoriaService.existeCategoria;

export default categoriaService;
