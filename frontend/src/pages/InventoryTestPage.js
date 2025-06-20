import React, { useState, useEffect } from "react";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import { getProductos } from "../services/productoService";
import checkoutService from "../services/checkoutService";
import StockWarning from "../components/StockWarning";
import { toast } from "react-toastify";
import "../styles/InventoryTest.css";

const InventoryTestPage = () => {
  const { addToCart, clearCart, validateCartStock, syncCartWithStock } =
    useCart();
  const { user } = useAuth();

  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [testResults, setTestResults] = useState([]);
  const [concurrentTests, setConcurrentTests] = useState(false);
  const [stockWarnings, setStockWarnings] = useState([]);

  useEffect(() => {
    loadProductos();
  }, []);

  const loadProductos = async () => {
    try {
      setLoading(true);
      const data = await getProductos();
      setProductos(data.filter((p) => p.disponible)); // Solo productos disponibles
    } catch (error) {
      console.error("Error cargando productos:", error);
      toast.error("Error cargando productos");
    } finally {
      setLoading(false);
    }
  };

  const testStockReduction = async (producto, cantidad) => {
    const testId = Date.now();
    const testName = `Test Reducción Stock - ${producto.nombre}`;

    addTestResult(testId, testName, "running", "Iniciando test...");

    try {
      // 1. Agregar al carrito
      await addToCart(producto, cantidad);
      addTestResult(
        testId,
        testName,
        "running",
        `Agregado ${cantidad} unidades al carrito`
      );

      // 2. Validar stock
      const stockErrors = await validateCartStock();
      if (stockErrors.length > 0) {
        addTestResult(
          testId,
          testName,
          "warning",
          `Advertencias: ${stockErrors.join(", ")}`
        );
      }

      // 3. Simular checkout
      const checkoutData = createMockCheckoutData([
        {
          productoId: producto.id,
          nombre: producto.nombre,
          cantidad: cantidad,
          precioUnitario: producto.precioVenta,
        },
      ]);

      const response = await checkoutService.procesarCheckout(checkoutData);

      if (response.exitoso) {
        addTestResult(
          testId,
          testName,
          "success",
          `✅ Checkout exitoso. Stock reducido correctamente`
        );
        await loadProductos(); // Recargar productos para ver cambios
      } else {
        addTestResult(
          testId,
          testName,
          "error",
          `❌ Error en checkout: ${response.mensaje}`
        );
      }
    } catch (error) {
      addTestResult(
        testId,
        testName,
        "error",
        `❌ Error: ${error.mensaje || error.message}`
      );
    }
  };

  const testStockExhaustion = async (producto) => {
    const testId = Date.now();
    const testName = `Test Agotamiento Stock - ${producto.nombre}`;

    addTestResult(testId, testName, "running", "Intentando agotar stock...");

    try {
      // Intentar comprar todo el stock disponible + 1
      const cantidadExcesiva = producto.cantidad + 1;

      const checkoutData = createMockCheckoutData([
        {
          productoId: producto.id,
          nombre: producto.nombre,
          cantidad: cantidadExcesiva,
          precioUnitario: producto.precioVenta,
        },
      ]);

      const response = await checkoutService.procesarCheckout(checkoutData);

      if (
        !response.exitoso &&
        response.mensaje.toLowerCase().includes("stock insuficiente")
      ) {
        addTestResult(
          testId,
          testName,
          "success",
          `✅ Validación correcta: ${response.mensaje}`
        );
      } else {
        addTestResult(
          testId,
          testName,
          "error",
          `❌ Validación falló: Se permitió compra excesiva`
        );
      }
    } catch (error) {
      if (
        error.mensaje &&
        error.mensaje.toLowerCase().includes("stock insuficiente")
      ) {
        addTestResult(
          testId,
          testName,
          "success",
          `✅ Excepción correcta: ${error.mensaje}`
        );
      } else {
        addTestResult(
          testId,
          testName,
          "error",
          `❌ Error inesperado: ${error.mensaje || error.message}`
        );
      }
    }
  };

  const testConcurrentPurchases = async (producto, userCount = 3) => {
    const testId = Date.now();
    const testName = `Test Concurrencia - ${producto.nombre}`;

    addTestResult(
      testId,
      testName,
      "running",
      `Simulando ${userCount} usuarios comprando simultáneamente...`
    );

    setConcurrentTests(true);

    try {
      const stockInicial = producto.cantidad;
      const cantidadPorUsuario = Math.floor(stockInicial / userCount) + 1; // Cantidad que causará conflicto

      const promises = Array.from({ length: userCount }, (_, index) => {
        const checkoutData = createMockCheckoutData(
          [
            {
              productoId: producto.id,
              nombre: producto.nombre,
              cantidad: cantidadPorUsuario,
              precioUnitario: producto.precioVenta,
            },
          ],
          `user${index + 1}`
        );

        return checkoutService.procesarCheckout(checkoutData);
      });

      const results = await Promise.allSettled(promises);

      let successCount = 0;
      let failCount = 0;

      results.forEach((result, index) => {
        if (result.status === "fulfilled" && result.value.exitoso) {
          successCount++;
        } else {
          failCount++;
        }
      });

      addTestResult(
        testId,
        testName,
        "info",
        `Resultados: ${successCount} compras exitosas, ${failCount} rechazadas por stock insuficiente`
      );

      if (failCount > 0) {
        addTestResult(
          testId,
          testName,
          "success",
          `✅ Control de concurrencia funcionando correctamente`
        );
      } else {
        addTestResult(
          testId,
          testName,
          "warning",
          `⚠️ Posible problema: Todas las compras fueron exitosas`
        );
      }

      await loadProductos(); // Recargar para ver el estado final
    } catch (error) {
      addTestResult(
        testId,
        testName,
        "error",
        `❌ Error en test de concurrencia: ${error.message}`
      );
    } finally {
      setConcurrentTests(false);
    }
  };

  const createMockCheckoutData = (items, userId = "testUser") => {
    const subtotal = items.reduce(
      (sum, item) => sum + item.precioUnitario * item.cantidad,
      0
    );
    const costoEnvio = 10;
    const igv = (subtotal + costoEnvio) * 0.18;
    const total = subtotal + costoEnvio + igv;

    return {
      usuarioId: user?.id || 1,
      direccionEntrega: "Dirección de prueba 123",
      distrito: "Lima",
      referencia: "Prueba de inventario",
      notas: `Test de inventario - Usuario: ${userId}`,
      necesitaFactura: false,
      subtotal,
      costoEnvio,
      igv,
      total,
      fechaEntrega: new Date(
        Date.now() + 3 * 24 * 60 * 60 * 1000
      ).toISOString(),
      metodoPago: "tarjeta",
      montoPago: total,
      tipoComprobante: "boleta",
      documento: "12345678",
      items,
    };
  };

  const addTestResult = (id, name, status, message) => {
    setTestResults((prev) => {
      const existing = prev.find((r) => r.id === id);
      if (existing) {
        return prev.map((r) =>
          r.id === id
            ? {
                ...r,
                status,
                messages: [...r.messages, message],
                timestamp: new Date(),
              }
            : r
        );
      } else {
        return [
          ...prev,
          {
            id,
            name,
            status,
            messages: [message],
            timestamp: new Date(),
          },
        ];
      }
    });
  };

  const addStockWarning = (type, title, message, suggestions = []) => {
    const warning = {
      id: Date.now(),
      type,
      title,
      message,
      suggestions,
    };
    setStockWarnings((prev) => [...prev, warning]);
  };

  const removeStockWarning = (id) => {
    setStockWarnings((prev) => prev.filter((w) => w.id !== id));
  };

  const clearTestResults = () => {
    setTestResults([]);
    setStockWarnings([]);
  };

  if (loading) {
    return (
      <div className="inventory-test-page">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Cargando productos para pruebas...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="inventory-test-page">
      <header className="test-header">
        <h1>🧪 Pruebas de Control de Inventario</h1>
        <p>
          Esta página permite probar el sistema de control de inventario con
          diferentes escenarios.
        </p>
      </header>

      {/* Avisos de Stock */}
      {stockWarnings.map((warning) => (
        <StockWarning
          key={warning.id}
          type={warning.type}
          title={warning.title}
          message={warning.message}
          suggestions={warning.suggestions}
          onClose={() => removeStockWarning(warning.id)}
        />
      ))}

      <div className="test-content">
        {/* Panel de Productos */}
        <section className="products-panel">
          <h2>Productos Disponibles</h2>
          <div className="products-grid">
            {productos.slice(0, 6).map((producto) => (
              <div key={producto.id} className="test-product-card">
                <h3>{producto.nombre}</h3>
                <p>
                  Stock:{" "}
                  <span
                    className={`stock-count ${
                      producto.cantidad <= 5 ? "low" : ""
                    }`}>
                    {producto.cantidad} unidades
                  </span>
                </p>
                <p>Precio: S/. {producto.precioVenta}</p>

                <div className="test-actions">
                  <button
                    onClick={() => testStockReduction(producto, 2)}
                    className="test-btn test-btn-primary"
                    disabled={concurrentTests}>
                    Test Reducción (2 unidades)
                  </button>

                  <button
                    onClick={() => testStockExhaustion(producto)}
                    className="test-btn test-btn-warning"
                    disabled={concurrentTests}>
                    Test Agotamiento
                  </button>

                  <button
                    onClick={() => testConcurrentPurchases(producto)}
                    className="test-btn test-btn-danger"
                    disabled={concurrentTests || producto.cantidad < 3}>
                    Test Concurrencia
                  </button>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Panel de Resultados */}
        <section className="results-panel">
          <div className="results-header">
            <h2>Resultados de Pruebas</h2>
            <div className="results-actions">
              <button onClick={clearTestResults} className="clear-btn">
                Limpiar Resultados
              </button>
              <button onClick={loadProductos} className="refresh-btn">
                🔄 Actualizar Stock
              </button>
            </div>
          </div>

          <div className="test-results">
            {testResults.length === 0 ? (
              <p className="no-results">
                No hay resultados de pruebas aún. Ejecuta algún test.
              </p>
            ) : (
              testResults.map((result) => (
                <div
                  key={result.id}
                  className={`test-result test-result-${result.status}`}>
                  <div className="result-header">
                    <span className="result-name">{result.name}</span>
                    <span className="result-status">{result.status}</span>
                    <span className="result-time">
                      {result.timestamp.toLocaleTimeString()}
                    </span>
                  </div>
                  <div className="result-messages">
                    {result.messages.map((message, index) => (
                      <div key={index} className="result-message">
                        {message}
                      </div>
                    ))}
                  </div>
                </div>
              ))
            )}
          </div>
        </section>
      </div>

      {/* Panel de Control */}
      <section className="control-panel">
        <h3>🎛️ Panel de Control</h3>
        <div className="control-actions">
          <button onClick={clearCart} className="control-btn">
            🗑️ Limpiar Carrito
          </button>

          <button
            onClick={async () => {
              const errors = await validateCartStock();
              if (errors.length > 0) {
                addStockWarning(
                  "warning",
                  "Problemas de Stock en Carrito",
                  "Se detectaron problemas con los productos en tu carrito",
                  errors
                );
              } else {
                toast.success(
                  "✅ Todos los productos en el carrito tienen stock suficiente"
                );
              }
            }}
            className="control-btn">
            🔍 Validar Stock del Carrito
          </button>

          <button onClick={syncCartWithStock} className="control-btn">
            🔄 Sincronizar Carrito
          </button>
        </div>
      </section>
    </div>
  );
};

export default InventoryTestPage;
