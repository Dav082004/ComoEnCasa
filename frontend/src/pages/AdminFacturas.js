import React, { useState, useEffect } from "react";
import "../styles/Admin.css";
import {
  listarComprobantes,
  exportarPdf,
  exportarReporteVentas,
} from "../services/comprobanteService";


const AdminFacturas = () => {
  const [facturas, setFacturas] = useState([]);
  const [filteredFacturas, setFilteredFacturas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedFactura, setSelectedFactura] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [showFilters, setShowFilters] = useState(false);
  const [filtros, setFiltros] = useState({
    clienteDocumento: "",
    pedidoId: "",
    desde: "",
    hasta: "",
  });

  useEffect(() => {
    fetchFacturas();
  }, []);

const fetchFacturas = async () => {
  try {
    setLoading(true);
    const data = await listarComprobantes();
    const soloFacturas = data.filter((f) => f.tipo === "Factura");
    setFacturas(soloFacturas);
    setFilteredFacturas(soloFacturas);
  } catch (error) {
    console.error("Error al cargar las Facturas:", error);
  } finally {
    setLoading(false);
  }
};


  const handleSearch = () => {
    const filtered = facturas.filter(
      (f) =>
        f.id?.toString().includes(searchTerm) ||
        f.clienteNombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        f.clienteDocumento?.includes(searchTerm) ||
        f.numeroComprobante?.includes(searchTerm)
    );
    setFilteredFacturas(filtered);
  };

 const handleFilterSubmit = async (e) => {
  e.preventDefault();
  try {
    setLoading(true);
    const filtrosLimpios = {};
    Object.entries(filtros).forEach(([key, value]) => {
      if (value && value.trim() !== "") {
        filtrosLimpios[key] = value.trim();
      }
    });

    const data = await listarComprobantes(filtrosLimpios);
    const soloFacturas = data.filter((f) => f.tipo === "Factura");
    setFacturas(soloFacturas);
    setFilteredFacturas(soloFacturas);
  } catch (error) {
    console.error("Error al filtrar Facturas:", error);
  } finally {
    setLoading(false);
  }
};


  const handleExportPdf = async (id) => {
    try {
      const blob = await exportarPdf(id);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `Factura_${id}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Error al exportar PDF:", error);
      alert("Error al exportar el PDF");
    }
  };
const handleExportExcel = async () => {
  try {
    const blob = await exportarReporteVentas(); // Aquí la función nueva
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `reporte_ventas.xlsx`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Error al exportar Excel:", error);
    alert("Error al exportar el reporte de ventas");
  }
};



  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString("es-ES", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const formatCurrency = (amount) => {
    if (!amount) return "S/ 0.00";
    return `S/ ${Number(amount).toFixed(2)}`;
  };

  return (
    <div className="page-container">
      <h1 className="theme-header">Gestión de Facturas</h1>

      <div className="admin-header">
        <input
          type="text"
          placeholder="Buscar por ID, cliente, documento..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="theme-button" onClick={handleSearch}>
          Buscar
        </button>
        <button
          className="theme-button filter-toggle-btn"
          onClick={() => setShowFilters(!showFilters)}
        >
          Filtros {showFilters ? "▲" : "▼"}
        </button>
      </div>

      {showFilters && (
        <div className="filter-panel">
          <form onSubmit={handleFilterSubmit} className="filter-form">
            <input
              type="text"
              placeholder="Documento del cliente"
              value={filtros.clienteDocumento}
              onChange={(e) =>
                setFiltros({ ...filtros, clienteDocumento: e.target.value })
              }
            />
            <input
              type="number"
              placeholder="ID del pedido"
              value={filtros.pedidoId}
              onChange={(e) =>
                setFiltros({ ...filtros, pedidoId: e.target.value })
              }
            />
            <input
              type="date"
              placeholder="Desde"
              value={filtros.desde}
              onChange={(e) =>
                setFiltros({ ...filtros, desde: e.target.value })
              }
            />
            <input
              type="date"
              placeholder="Hasta"
              value={filtros.hasta}
              onChange={(e) =>
                setFiltros({ ...filtros, hasta: e.target.value })
              }
            />
            <button type="submit" className="theme-button">
              Aplicar Filtros
            </button>
            <button
              type="button"
              className="theme-button"
              onClick={() => {
                setFiltros({
                  clienteDocumento: "",
                  pedidoId: "",
                  desde: "",
                  hasta: "",
                });
                fetchFacturas();
              }}
            >
              Limpiar
            </button>
          </form>
        </div>
      )}

      {loading ? (
        <div className="loading-container" style={{ padding: "2rem", textAlign: "center" }}>
          <div className="spinner-border text-pink" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p>Cargando Facturas...</p>
        </div>
      ) : (
        <>
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Pedido ID</th>
                <th>Tipo</th>
                <th>Cliente</th>
                <th>Documento</th>
                <th>Email</th>
                <th>Fecha Emisión</th>
                <th>Serie</th>
                <th>Número</th>
                <th>Subtotal</th>
                <th>Total</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredFacturas.map((f) => (
                <tr
                  key={f.id}
                  onClick={() => setSelectedFactura(f)}
                  className={selectedFactura?.id === f.id ? "selected-row" : ""}
                >
                  <td>{f.id}</td>
                  <td>{f.pedidoId}</td>
                  <span className={`status-badge status-${f.tipo?.toLowerCase()}`}>
                      {f.tipo}
                    </span>
                  <td>{f.clienteNombre}</td>
                  <td>{f.clienteDocumento || "N/A"}</td>
                  <td>{f.clienteEmail}</td>
                  <td>{formatDate(f.fechaEmision)}</td>
                  <td>{f.numeroSerie}</td>
                  <td>{f.numeroComprobante}</td>
                  <td>{formatCurrency(f.subtotal)}</td>
                  <td>{formatCurrency(f.total)}</td>
                  <td>
                    <button
                      className="theme-button"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleExportPdf(f.id);
                      }}
                    >
                      PDF
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

<div className="table-footer">
  <span>Total de Facturas: {filteredFacturas.length}</span>
  {selectedFactura && (
    <div className="selected-info">
      <span>
        Seleccionado: Factura #{selectedFactura.id} - {selectedFactura.clienteNombre}
      </span>
    </div>
  )}

  <div style={{ width: "100%", textAlign: "right", marginTop: "1rem" }}>
    <button className="theme-button" onClick={handleExportExcel}>
      📥 Reporte Ventas
    </button>
  </div>
</div>

        </>
      )}
    </div>
  );
};

export default AdminFacturas;
