import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";
import { categoriaService } from "../services/categoriaService";

const CategoriaContext = createContext();

export const useCategorias = () => {
  const context = useContext(CategoriaContext);
  if (!context) {
    throw new Error("useCategorias debe usarse dentro de CategoriaProvider");
  }
  return context;
};

export const CategoriaProvider = ({ children }) => {
  const [categorias, setCategorias] = useState([]);
  const [categoriaMap, setCategoriaMap] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isReconnecting, setIsReconnecting] = useState(false);
  const [hasTriedReconnect, setHasTriedReconnect] = useState(false); // Función para cargar categorías (con conteo cuando sea posible)
  const loadCategorias = useCallback(
    async (isRetry = false) => {
      try {
        setLoading(true);
        setError(null);
        console.log(
          "Context: Cargando categorías...",
          isRetry ? "(reintento)" : ""
        );

        // Intentar primero cargar con conteo
        let categoriasData;
        try {
          categoriasData = await categoriaService.getCategoriasConConteo();
          console.log(
            "Context: Categorías con conteo cargadas:",
            categoriasData
          );
        } catch (conteoError) {
          console.warn(
            "Context: No se pudo cargar con conteo, intentando categorías básicas..."
          );
          try {
            categoriasData = await categoriaService.getAllCategorias();
            // Agregar cantidadProductos = 0 para compatibilidad
            categoriasData = categoriasData.map((cat) => ({
              ...cat,
              cantidadProductos: 0,
            }));
            console.log(
              "Context: Categorías básicas cargadas:",
              categoriasData
            );
          } catch (basicError) {
            // Si es el primer intento, esperar 2 segundos e intentar de nuevo
            if (!isRetry) {
              console.log(
                "Context: Backend puede estar iniciando, esperando 2 segundos..."
              );
              setTimeout(() => {
                loadCategorias(true);
              }, 2000);
              return;
            }
            throw basicError;
          }
        }
        setCategorias(categoriasData);

        // Crear mapa de categorías
        const map = {};
        categoriasData.forEach((categoria) => {
          map[categoria.id] = categoria.nombre;
        });
        setCategoriaMap(map);
        console.log("Context: Mapa de categorías creado:", map);

        // Resetear estado de reconexión al conectar exitosamente
        setHasTriedReconnect(false);
        setError(null);
      } catch (err) {
        console.error("Context: Error cargando categorías:", err);

        // Mensaje de error más específico
        let errorMessage = "Error al cargar categorías";
        if (
          err.code === "ERR_NETWORK" ||
          err.code === "ERR_CONNECTION_REFUSED"
        ) {
          errorMessage =
            "No se puede conectar al servidor. Verifica que el backend esté funcionando en el puerto 8081.";
        } else if (err.response) {
          errorMessage = `Error del servidor: ${err.response.status} - ${err.response.statusText}`;
        } else if (err.request) {
          errorMessage = "No se recibió respuesta del servidor";
        } else {
          errorMessage = err.message;
        }

        setError(errorMessage);

        // Fallback con categorías conocidas (basado en la BD real)
        const fallbackCategorias = [
          {
            id: 1,
            nombre: "Tortas",
            descripcion: "Deliciosas tortas personalizadas para toda ocasión",
            cantidadProductos: 0,
          },
          {
            id: 2,
            nombre: "Eventos",
            descripcion: "Productos especiales para eventos y celebraciones",
            cantidadProductos: 0,
          },
          {
            id: 3,
            nombre: "Postres",
            descripcion:
              "Variedad de postres y dulces para complementar tu comida",
            cantidadProductos: 0,
          },
          {
            id: 4,
            nombre: "Ola321",
            descripcion: "asda",
            cantidadProductos: 0,
          },
          {
            id: 5,
            nombre: "Fiestas",
            descripcion: "fiestadescrip",
            cantidadProductos: 0,
          },
        ];

        setCategorias(fallbackCategorias);

        const fallbackMap = {};
        fallbackCategorias.forEach((categoria) => {
          fallbackMap[categoria.id] = categoria.nombre;
        });
        setCategoriaMap(fallbackMap);

        console.log(
          "Context: Usando categorías de fallback debido al error:",
          errorMessage
        ); // Si el error es de conexión, intentar reconectar cada 5 segundos (solo una vez)
        if (
          (err.code === "ERR_NETWORK" ||
            err.code === "ERR_CONNECTION_REFUSED") &&
          !hasTriedReconnect
        ) {
          console.log(
            "Context: Programando reintento de conexión en 5 segundos..."
          );
          setIsReconnecting(true);
          setHasTriedReconnect(true);
          setTimeout(() => {
            console.log("Context: Reintentando conexión con el backend...");
            setIsReconnecting(false);
            loadCategorias();
          }, 5000);
        }
      } finally {
        setLoading(false);
      }
    },
    [hasTriedReconnect]
  ); // useCallback con dependencia para evitar múltiples reconexiones
  // Función para refrescar categorías
  const refreshCategorias = useCallback(async () => {
    console.log("Context: Refrescando categorías...");
    await loadCategorias();
  }, [loadCategorias]); // Depende de loadCategorias

  // Funciones CRUD que automáticamente refrescan la lista
  const createCategoria = useCallback(
    async (categoriaData) => {
      try {
        setLoading(true);
        const newCategoria = await categoriaService.createCategoria(
          categoriaData
        );
        console.log("Context: Categoría creada, refrescando lista...");
        await loadCategorias(); // Recargar directamente para garantizar actualización
        return newCategoria;
      } catch (error) {
        console.error("Context: Error creando categoría:", error);
        throw error;
      } finally {
        setLoading(false);
      }
    },
    [loadCategorias]
  ); // Depende de loadCategorias

  const updateCategoria = useCallback(
    async (id, categoriaData) => {
      try {
        setLoading(true);
        const updatedCategoria = await categoriaService.updateCategoria(
          id,
          categoriaData
        );
        console.log("Context: Categoría actualizada, refrescando lista...");
        await loadCategorias(); // Recargar directamente para garantizar actualización
        return updatedCategoria;
      } catch (error) {
        console.error("Context: Error actualizando categoría:", error);
        throw error;
      } finally {
        setLoading(false);
      }
    },
    [loadCategorias]
  ); // Depende de loadCategorias

  const deleteCategoria = useCallback(
    async (id) => {
      try {
        setLoading(true);
        await categoriaService.deleteCategoria(id);
        console.log("Context: Categoría eliminada, refrescando lista...");
        await loadCategorias(); // Recargar directamente para garantizar actualización
      } catch (error) {
        console.error("Context: Error eliminando categoría:", error);
        throw error;
      } finally {
        setLoading(false);
      }
    },
    [loadCategorias]
  ); // Depende de loadCategorias

  // Cargar categorías al inicializar
  useEffect(() => {
    loadCategorias();
  }, [loadCategorias]);
  const value = {
    categorias,
    categoriaMap,
    loading,
    error,
    isReconnecting,
    loadCategorias,
    refreshCategorias,
    createCategoria,
    updateCategoria,
    deleteCategoria,
  };

  return (
    <CategoriaContext.Provider value={value}>
      {children}
    </CategoriaContext.Provider>
  );
};
