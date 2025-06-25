import React, { useState, useEffect, useCallback } from "react";
import {
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import "../styles/Admin.css";
import "../styles/Reportes.css";
import { getAllPedidos } from "../services/pedidoService";
import { getProductos } from "../services/productoService";
import { useCategorias } from "../context/CategoriaContext";

const AdminReportes = () => {
  const [pedidos, setPedidos] = useState([]);
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [dateRange, setDateRange] = useState({
    mesDesde: "",
    mesHasta: "",
  });
  const [filteredData, setFilteredData] = useState({
    ventasPorCategoria: [],
    ventasPorProducto: [],
    gananciasPorProducto: [],
  });
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [productosDeCategoria, setProductosDeCategoria] = useState([]);

  const { categorias, categoriaMap } = useCategorias();

  // Función para manejar cambio de categoría
  const handleCategoryChange = (categoryName) => {
    setSelectedCategory(categoryName);
    
    if (categoryName === "all") {
      setProductosDeCategoria(filteredData.gananciasPorProducto.slice(0, 10));
    } else {
      // Filtrar productos de la categoría específica
      const productosDeEstaCategoria = filteredData.gananciasPorProducto.filter(producto => {
        // Buscar el producto original para obtener su categoría
        const productoOriginal = productos.find(p => p.nombre === producto.nombre);
        if (productoOriginal && productoOriginal.categoriaId) {
          const nombreCategoria = categoriaMap[productoOriginal.categoriaId];
          return nombreCategoria === categoryName;
        }
        return false;
      });
      setProductosDeCategoria(productosDeEstaCategoria);
    }
  };

  // Colores para los gráficos
  const COLORS = [
    "#FF6BA6", // Rosa principal
    "#FF85C0", // Rosa claro
    "#4ECDC4", // Turquesa
    "#45B7D1", // Azul
    "#96CEB4", // Verde suave
    "#FFEAA7", // Amarillo suave
    "#DDA0DD", // Violeta suave
    "#FFB6C1", // Rosa pálido
    "#87CEEB", // Azul cielo
    "#98FB98", // Verde menta
  ];

  const fetchData = async () => {
    try {
      setLoading(true);
      const [pedidosData, productosData] = await Promise.all([
        getAllPedidos(),
        getProductos(),
      ]);
      setPedidos(pedidosData);
      setProductos(productosData);
    } catch (error) {
      console.error("Error al cargar datos:", error);
    } finally {
      setLoading(false);
    }
  };

  const processData = useCallback(() => {
    console.log("🔍 Procesando datos...");
    console.log("📦 Pedidos totales:", pedidos.length);
    console.log("🛍️ Productos disponibles:", productos.length);
    
    // Mostrar TODOS los pedidos primero
    console.log("📋 Todos los pedidos:", pedidos.map(p => ({
      id: p.id,
      estado: p.estado,
      detalles: p.detalles?.length || 0,
      fechaCreacion: p.fechaCreacion
    })));
    
    // Filtrar pedidos por estado "Entregado" y fecha
    let pedidosFiltrados = pedidos.filter(
      (pedido) => pedido.estado === "Entregado"
    );
    
    console.log("✅ Pedidos entregados:", pedidosFiltrados.length);

    if (dateRange.mesDesde) {
      const [year, month] = dateRange.mesDesde.split('-');
      const fechaInicio = new Date(year, month - 1, 1); // Primer día del mes
      
      pedidosFiltrados = pedidosFiltrados.filter(
        (pedido) => new Date(pedido.fechaCreacion) >= fechaInicio
      );
      console.log("📅 Después de filtro 'mes desde':", pedidosFiltrados.length);
    }

    if (dateRange.mesHasta) {
      const [year, month] = dateRange.mesHasta.split('-');
      const fechaFin = new Date(year, month, 0); // Último día del mes
      fechaFin.setHours(23, 59, 59, 999); // Final del día
      
      pedidosFiltrados = pedidosFiltrados.filter(
        (pedido) => new Date(pedido.fechaCreacion) <= fechaFin
      );
      console.log("📅 Después de filtro 'mes hasta':", pedidosFiltrados.length);
    }

    // Procesar ventas por categoría y ganancias por producto
    const ventasPorCategoria = {};
    const ventasPorProducto = {};
    const gananciasPorProducto = {};

    pedidosFiltrados.forEach((pedido) => {
      console.log("🔄 Procesando pedido:", pedido.id, "con", pedido.detalles?.length || 0, "detalles");
      
      if (pedido.detalles && pedido.detalles.length > 0) {
        pedido.detalles.forEach((detalle) => {
          console.log("🛒 Procesando detalle:", detalle);
          
          // Usar directamente la información del detalle
          const nombreProducto = detalle.nombreProducto;
          const cantidadVendida = detalle.cantidad || 1;
          const montoVenta = detalle.subtotal || (detalle.precioUnitario * cantidadVendida);
          const costoUnitario = detalle.costoUnitario || 0;
          const precioUnitario = detalle.precioUnitario || 0;
          
          // Calcular ganancia: (precio_venta - costo_produccion) * cantidad
          const gananciaPorUnidad = precioUnitario - costoUnitario;
          const gananciaTotal = gananciaPorUnidad * cantidadVendida;

          // Buscar el producto para obtener la categoría
          const producto = productos.find((p) => p.id === detalle.productoId);
          let nombreCategoria = "Sin categoría";
          
          if (producto && producto.categoriaId) {
            // El categoriaMap ya contiene nombres directamente según el log
            nombreCategoria = categoriaMap[producto.categoriaId] || "Sin categoría";
          }

          console.log("📊 Producto:", nombreProducto, "| ProductoID:", detalle.productoId, "| Producto encontrado:", !!producto, "| CategoriaID:", producto?.categoriaId, "| Categoría:", nombreCategoria, "| Cantidad:", cantidadVendida, "| Monto:", montoVenta, "| Ganancia:", gananciaTotal);

          // Ventas por categoría
          if (!ventasPorCategoria[nombreCategoria]) {
            ventasPorCategoria[nombreCategoria] = {
              nombre: nombreCategoria,
              cantidad: 0,
              monto: 0,
            };
          }
          ventasPorCategoria[nombreCategoria].cantidad += cantidadVendida;
          ventasPorCategoria[nombreCategoria].monto += montoVenta;

          // Ventas por producto
          if (!ventasPorProducto[nombreProducto]) {
            ventasPorProducto[nombreProducto] = {
              nombre: nombreProducto,
              cantidad: 0,
              monto: 0,
            };
          }
          ventasPorProducto[nombreProducto].cantidad += cantidadVendida;
          ventasPorProducto[nombreProducto].monto += montoVenta;

          // Ganancias por producto
          if (!gananciasPorProducto[nombreProducto]) {
            gananciasPorProducto[nombreProducto] = {
              nombre: nombreProducto,
              cantidad: 0,
              ganancia: 0,
              costo: 0,
              venta: 0,
              margen: 0,
            };
          }
          gananciasPorProducto[nombreProducto].cantidad += cantidadVendida;
          gananciasPorProducto[nombreProducto].ganancia += gananciaTotal;
          gananciasPorProducto[nombreProducto].costo += (costoUnitario * cantidadVendida);
          gananciasPorProducto[nombreProducto].venta += montoVenta;
          gananciasPorProducto[nombreProducto].margen = gananciasPorProducto[nombreProducto].venta > 0 
            ? ((gananciasPorProducto[nombreProducto].ganancia / gananciasPorProducto[nombreProducto].venta) * 100) 
            : 0;
        });
      }
    });

    console.log("📈 Ventas por categoría:", ventasPorCategoria);
    console.log("🏆 Ventas por producto:", ventasPorProducto);
    console.log("💰 Ganancias por producto:", gananciasPorProducto);

    // Convertir a arrays y ordenar
    const ventasPorCategoriaArray = Object.values(ventasPorCategoria).sort(
      (a, b) => b.monto - a.monto
    );

    const ventasPorProductoArray = Object.values(ventasPorProducto)
      .sort((a, b) => b.cantidad - a.cantidad)
      .slice(0, 10); // Top 10 productos

    const gananciasPorProductoArray = Object.values(gananciasPorProducto)
      .sort((a, b) => b.ganancia - a.ganancia)
      .slice(0, 10); // Top 10 productos más rentables

    console.log("📊 Resultado final - Categorías:", ventasPorCategoriaArray);
    console.log("🏆 Resultado final - Productos:", ventasPorProductoArray);
    console.log("💰 Resultado final - Ganancias:", gananciasPorProductoArray);

    setFilteredData({
      ventasPorCategoria: ventasPorCategoriaArray,
      ventasPorProducto: ventasPorProductoArray,
      gananciasPorProducto: gananciasPorProductoArray,
    });
  }, [pedidos, productos, dateRange, categoriaMap]);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    console.log("=== ADMIN REPORTES DEBUG ===");
    console.log("🔄 Datos actualizados:", {
      pedidos: pedidos.length,
      productos: productos.length,
      categorias: Object.keys(categoriaMap).length
    });
    
    if (productos.length > 0) {
      console.log("🛍️ Lista de productos disponibles:", productos);
      
      // Verificar mapeo de categorías para debugging
      const categoriasEnProductos = [...new Set(productos.map(p => p.categoriaId))];
      console.log("🏷️ Categorías encontradas en productos:", categoriasEnProductos);
      
      categoriasEnProductos.forEach(catId => {
        const nombreCategoria = categoriaMap[catId];
        const productosEnCategoria = productos.filter(p => p.categoriaId === catId);
        console.log(`📂 Categoría ID ${catId}: ${nombreCategoria || 'NOMBRE NO ENCONTRADO'} - ${productosEnCategoria.length} productos`);
        if (productosEnCategoria.length > 0) {
          console.log(`   Productos en categoría ${catId}:`, productosEnCategoria.map(p => p.nombre));
        }
      });
    }
    
    if (Object.keys(categoriaMap).length > 0) {
      console.log("🏷️ Mapa de categorías completo:", categoriaMap);
    }
    
    if (pedidos.length > 0 && productos.length > 0) {
      processData();
    }
  }, [pedidos, productos, dateRange, categorias, categoriaMap, processData]);

  // Actualizar productos de categoría cuando cambian los datos filtrados
  useEffect(() => {
    if (filteredData.gananciasPorProducto.length > 0) {
      handleCategoryChange(selectedCategory);
    }
  }, [filteredData.gananciasPorProducto, selectedCategory, productos, categoriaMap]);

  const formatCurrency = (value) => {
    return `S/. ${value.toFixed(2)}`;
  };

  const formatMonth = (monthString) => {
    if (!monthString) return "";
    const [year, month] = monthString.split('-');
    const monthNames = [
      "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
      "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    ];
    return `${monthNames[parseInt(month) - 1]} ${year}`;
  };

  const formatTooltip = (value, name, props) => {
    if (name === "monto") {
      const categoria = props?.payload?.nombre || "Categoría";
      return [`${formatCurrency(value)}`, `${categoria} - Monto`];
    }
    return [value, name === "cantidad" ? "Cantidad" : name];
  };

  const formatPieTooltip = (value, name, props) => {
    const categoria = props?.payload?.nombre || "Categoría";
    return [`${formatCurrency(value)}`, `${categoria}`];
  };

  const handleDateChange = (field, value) => {
    setDateRange((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const clearFilters = () => {
    setDateRange({
      mesDesde: "",
      mesHasta: "",
    });
  };

  const setQuickFilter = (type) => {
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1;
    
    switch (type) {
      case 'currentMonth':
        const current = `${currentYear}-${currentMonth.toString().padStart(2, '0')}`;
        setDateRange({ mesDesde: current, mesHasta: current });
        break;
      case 'lastMonth':
        const lastMonth = currentMonth === 1 ? 12 : currentMonth - 1;
        const lastYear = currentMonth === 1 ? currentYear - 1 : currentYear;
        const last = `${lastYear}-${lastMonth.toString().padStart(2, '0')}`;
        setDateRange({ mesDesde: last, mesHasta: last });
        break;
      case 'last3Months':
        const threeMonthsAgo = new Date(currentYear, currentMonth - 4, 1);
        const from3 = `${threeMonthsAgo.getFullYear()}-${(threeMonthsAgo.getMonth() + 1).toString().padStart(2, '0')}`;
        const to3 = `${currentYear}-${currentMonth.toString().padStart(2, '0')}`;
        setDateRange({ mesDesde: from3, mesHasta: to3 });
        break;
      case 'currentYear':
        setDateRange({ mesDesde: `${currentYear}-01`, mesHasta: `${currentYear}-12` });
        break;
      default:
        console.warn('Tipo de filtro rápido no reconocido:', type);
        break;
    }
  };

  const getTotalVentas = () => {
    return filteredData.ventasPorCategoria.reduce(
      (total, categoria) => total + categoria.monto,
      0
    );
  };

  const getTotalProductosVendidos = () => {
    return filteredData.ventasPorProducto.reduce(
      (total, producto) => total + producto.cantidad,
      0
    );
  };

  const getTotalGanancias = () => {
    return filteredData.gananciasPorProducto.reduce(
      (total, producto) => total + producto.ganancia,
      0
    );
  };

  const getTotalCostos = () => {
    return filteredData.gananciasPorProducto.reduce(
      (total, producto) => total + producto.costo,
      0
    );
  };

  const getMargenPromedioGeneral = () => {
    const totalVentas = getTotalVentas();
    const totalGanancias = getTotalGanancias();
    return totalVentas > 0 ? ((totalGanancias / totalVentas) * 100) : 0;
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-container">
          <div className="spinner-border text-pink" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p>Cargando datos de reportes...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h1 className="theme-header">📊 Reportes de Ventas</h1>

      {/* Filtros de fecha */}
      <div className="filter-panel">
        <h3>� Filtros por Mes</h3>
        <div className="filter-form">
          <div className="form-group">
            <label htmlFor="mesDesde">Desde el mes:</label>
            <input
              type="month"
              id="mesDesde"
              value={dateRange.mesDesde}
              onChange={(e) => handleDateChange("mesDesde", e.target.value)}
              className="form-input"
              max={new Date().toISOString().slice(0, 7)} // No permitir meses futuros
            />
          </div>
          <div className="form-group">
            <label htmlFor="mesHasta">Hasta el mes:</label>
            <input
              type="month"
              id="mesHasta"
              value={dateRange.mesHasta}
              onChange={(e) => handleDateChange("mesHasta", e.target.value)}
              className="form-input"
              max={new Date().toISOString().slice(0, 7)} // No permitir meses futuros
            />
          </div>
          <button
            type="button"
            className="theme-button"
            onClick={clearFilters}
          >
            🗑️ Limpiar Filtros
          </button>
        </div>
        
        {/* Filtros rápidos */}
        <div className="quick-filters">
          <p><strong>⚡ Filtros rápidos:</strong></p>
          <div className="quick-filter-buttons">
            <button 
              className="quick-filter-btn" 
              onClick={() => setQuickFilter('currentMonth')}
            >
              📅 Este mes
            </button>
            <button 
              className="quick-filter-btn" 
              onClick={() => setQuickFilter('lastMonth')}
            >
              ⏮️ Mes anterior
            </button>
            <button 
              className="quick-filter-btn" 
              onClick={() => setQuickFilter('last3Months')}
            >
              📈 Últimos 3 meses
            </button>
            <button 
              className="quick-filter-btn" 
              onClick={() => setQuickFilter('currentYear')}
            >
              🗓️ Todo el año
            </button>
          </div>
        </div>
        
        {/* Información del filtro activo */}
        {(dateRange.mesDesde || dateRange.mesHasta) && (
          <div className="filter-info">
            <p className="filter-active">
              📊 Filtro activo: 
              {dateRange.mesDesde && ` Desde ${formatMonth(dateRange.mesDesde)}`}
              {dateRange.mesHasta && ` Hasta ${formatMonth(dateRange.mesHasta)}`}
            </p>
          </div>
        )}
      </div>

      {/* Estadísticas resumen */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">💰</div>
          <div className="stat-content">
            <h3>{formatCurrency(getTotalVentas())}</h3>
            <p>Total de Ventas</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">�</div>
          <div className="stat-content">
            <h3>{formatCurrency(getTotalGanancias())}</h3>
            <p>Ganancias Netas</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">📊</div>
          <div className="stat-content">
            <h3>{getMargenPromedioGeneral().toFixed(1)}%</h3>
            <p>Margen Promedio</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">📦</div>
          <div className="stat-content">
            <h3>{getTotalProductosVendidos()}</h3>
            <p>Productos Vendidos</p>
          </div>
        </div>
      </div>

      {/* Gráficos */}
      <div className="charts-grid">
        {/* Gráfico de pie - Ventas por categoría */}
        <div className="chart-container">
          <h2 className="chart-title">🥧 Ventas por Categoría</h2>
          {filteredData.ventasPorCategoria.length > 0 ? (
            <ResponsiveContainer width="100%" height={400}>
              <PieChart>
                <Pie
                  data={filteredData.ventasPorCategoria}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ nombre, percent, monto }) =>
                    `${nombre}: ${(percent * 100).toFixed(1)}%`
                  }
                  outerRadius={120}
                  fill="#8884d8"
                  dataKey="monto"
                  nameKey="nombre"
                >
                  {filteredData.ventasPorCategoria.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={COLORS[index % COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip 
                  formatter={formatPieTooltip}
                  labelFormatter={(label) => `Categoría: ${label}`}
                />
                <Legend 
                  formatter={(value, entry) => `${entry.payload.nombre}`}
                />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="no-data">
              <p>No hay datos de ventas para mostrar</p>
            </div>
          )}
        </div>

        {/* Gráfico de barras - Top productos */}
        <div className="chart-container">
          <h2 className="chart-title">📊 Top 10 Productos Más Vendidos</h2>
          {filteredData.ventasPorProducto.length > 0 ? (
            <ResponsiveContainer width="100%" height={400}>
              <BarChart
                data={filteredData.ventasPorProducto}
                margin={{
                  top: 5,
                  right: 30,
                  left: 20,
                  bottom: 80,
                }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis
                  dataKey="nombre"
                  angle={-45}
                  textAnchor="end"
                  height={100}
                  interval={0}
                  fontSize={12}
                />
                <YAxis />
                <Tooltip formatter={formatTooltip} />
                <Legend />
                <Bar
                  dataKey="cantidad"
                  fill="#FF6BA6"
                  name="Cantidad Vendida"
                  radius={[4, 4, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div className="no-data">
              <p>No hay datos de productos para mostrar</p>
            </div>
          )}
        </div>
      </div>

      {/* Gráfico de ganancias por producto */}
      <div className="chart-container full-width">
        <h2 className="chart-title">💰 Ganancia por Producto</h2>
        
        {/* Toggle de categorías */}
        <div className="category-toggle">
          <label htmlFor="categorySelect" className="toggle-label">
            🏷️ Filtrar por categoría:
          </label>
          <select 
            id="categorySelect"
            value={selectedCategory} 
            onChange={(e) => handleCategoryChange(e.target.value)}
            className="category-select"
          >
            <option value="all">📋 Todas las categorías</option>
            {filteredData.ventasPorCategoria.map((categoria, index) => (
              <option key={index} value={categoria.nombre}>
                {categoria.nombre}
              </option>
            ))}
          </select>
        </div>

        {productosDeCategoria.length > 0 ? (
          <ResponsiveContainer width="100%" height={400}>
            <BarChart
              data={productosDeCategoria}
              margin={{
                top: 5,
                right: 30,
                left: 20,
                bottom: 80,
              }}
            >
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis
                dataKey="nombre"
                angle={-45}
                textAnchor="end"
                height={100}
                interval={0}
                fontSize={12}
              />
              <YAxis tickFormatter={(value) => `S/. ${value.toFixed(0)}`} />
              <Tooltip 
                formatter={(value, name) => {
                  if (name === "ganancia") return [`S/. ${value.toFixed(2)}`, "Ganancia"];
                  if (name === "venta") return [`S/. ${value.toFixed(2)}`, "Ventas"];
                  if (name === "costo") return [`S/. ${value.toFixed(2)}`, "Costos"];
                  return [value, name];
                }}
                labelFormatter={(label) => `Producto: ${label}`}
              />
              <Legend />
              <Bar
                dataKey="venta"
                fill="#4ECDC4"
                name="Ventas"
                radius={[4, 4, 0, 0]}
              />
              <Bar
                dataKey="costo"
                fill="#FFB6C1"
                name="Costos"
                radius={[4, 4, 0, 0]}
              />
              <Bar
                dataKey="ganancia"
                fill="#96CEB4"
                name="Ganancia"
                radius={[4, 4, 0, 0]}
              />
            </BarChart>
          </ResponsiveContainer>
        ) : (
          <div className="no-data">
            <p>
              {selectedCategory === "all" 
                ? "No hay datos de ganancias para mostrar" 
                : `No hay productos vendidos en la categoría "${selectedCategory}"`
              }
            </p>
          </div>
        )}
      </div>

      {/* Productos de la categoría seleccionada */}
      {selectedCategory !== "all" && productosDeCategoria.length > 0 && (
        <div className="chart-container">
          <h3 className="chart-title">
            🏷️ Productos de la categoría: <span style={{color: '#ff6ba6'}}>{selectedCategory}</span>
          </h3>
          <div className="category-products-grid">
            {productosDeCategoria.map((producto, index) => (
              <div key={index} className="product-card">
                <div className="product-header">
                  <h4 className="product-name">🍰 {producto.nombre}</h4>
                </div>
                <div className="product-stats">
                  <div className="stat-item">
                    <span className="stat-label">📦 Cantidad vendida:</span>
                    <span className="stat-value">{producto.cantidad}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">💰 Ventas totales:</span>
                    <span className="stat-value gain">{formatCurrency(producto.venta)}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">💸 Costos totales:</span>
                    <span className="stat-value cost">{formatCurrency(producto.costo)}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">✨ Ganancia neta:</span>
                    <span className="stat-value profit">{formatCurrency(producto.ganancia)}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">📈 Margen:</span>
                    <span className="stat-value margin">{producto.margen.toFixed(1)}%</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tabla de detalles por categoría */}
      {filteredData.ventasPorCategoria.length > 0 && (
        <div className="chart-container">
          <h2 className="chart-title">📋 Detalle de Ventas por Categoría</h2>
          <table className="admin-table">
            <thead>
              <tr>
                <th>🏷️ Categoría</th>
                <th>📦 Cantidad Vendida</th>
                <th>💰 Monto Total</th>
                <th>📊 Porcentaje</th>
              </tr>
            </thead>
            <tbody>
              {filteredData.ventasPorCategoria.map((categoria, index) => {
                // Íconos por categoría
                const getCategoryIcon = (categoryName) => {
                  const name = categoryName.toLowerCase();
                  if (name.includes('torta')) return '🎂';
                  if (name.includes('postre')) return '🍰';
                  if (name.includes('evento')) return '🎉';
                  if (name.includes('cupcake') || name.includes('mini')) return '🧁';
                  if (name.includes('fiesta')) return '🎈';
                  return '🍰'; // Default
                };

                return (
                  <tr key={index}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <span
                          className="category-indicator"
                          style={{
                            backgroundColor: COLORS[index % COLORS.length],
                            width: '12px',
                            height: '12px',
                            borderRadius: '50%',
                            display: 'inline-block'
                          }}
                        ></span>
                        <span style={{ fontSize: '1.2em' }}>
                          {getCategoryIcon(categoria.nombre)}
                        </span>
                        <strong>{categoria.nombre}</strong>
                      </div>
                    </td>
                    <td style={{ textAlign: 'center', fontWeight: 'bold' }}>
                      {categoria.cantidad}
                    </td>
                    <td style={{ textAlign: 'right', fontWeight: 'bold', color: '#ff6ba6' }}>
                      {formatCurrency(categoria.monto)}
                    </td>
                    <td style={{ textAlign: 'center' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <div 
                          style={{
                            width: `${(categoria.monto / getTotalVentas()) * 100}%`,
                            maxWidth: '60px',
                            height: '8px',
                            backgroundColor: COLORS[index % COLORS.length],
                            borderRadius: '4px',
                            minWidth: '4px'
                          }}
                        ></div>
                        <span style={{ fontWeight: 'bold' }}>
                          {((categoria.monto / getTotalVentas()) * 100).toFixed(1)}%
                        </span>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
            <tfoot>
              <tr style={{ backgroundColor: '#f8f9fa', fontWeight: 'bold' }}>
                <td>📊 TOTAL GENERAL</td>
                <td style={{ textAlign: 'center' }}>
                  {filteredData.ventasPorCategoria.reduce((total, cat) => total + cat.cantidad, 0)}
                </td>
                <td style={{ textAlign: 'right', color: '#ff6ba6' }}>
                  {formatCurrency(getTotalVentas())}
                </td>
                <td style={{ textAlign: 'center' }}>100.0%</td>
              </tr>
            </tfoot>
          </table>
        </div>
      )}
    </div>
  );
};

export default AdminReportes;
