import React, { useState, useEffect } from "react";
import "../styles/Admin.css";
import {
  listarComprobantes,
  exportarPdf,
  exportarExcel,
} from "../services/comprobanteService";

const AdminComprobantes = () => {
  const [comprobantes, setComprobantes] = useState([]);
  const [filteredComprobantes, setFilteredComprobantes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedComprobante, setSelectedComprobante] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [showFilters, setShowFilters] = useState(false);
  const [filtros, setFiltros] = useState({
    clienteDocumento: "",
    pedidoId: "",
    desde: "",
    hasta: "",
  });

  useEffect(() => {
    fetchComprobantes();
  }, []);

  const fetchComprobantes = async () => {
    try {
      setLoading(true);
      const data = await listarComprobantes();
      setComprobantes(data);
      setFilteredComprobantes(data);
    } catch (error) {
      console.error("Error al cargar los comprobantes:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    const filtered = comprobantes.filter(
      (comprobante) =>
        comprobante.id?.toString().includes(searchTerm) ||
        comprobante.clienteNombre
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ||
        comprobante.clienteDocumento?.includes(searchTerm) ||
        comprobante.tipo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        comprobante.numeroComprobante?.includes(searchTerm)
    );
    setFilteredComprobantes(filtered);
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
      setComprobantes(data);
      setFilteredComprobantes(data);
    } catch (error) {
      console.error("Error al filtrar comprobantes:", error);
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
      link.download = `comprobante_${id}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Error al exportar PDF:", error);
      alert("Error al exportar el PDF");
    }
  };

  const handleExportExcel = async (id) => {
    try {
      const blob = await exportarExcel(id);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = `comprobante_${id}.xlsx`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Error al exportar Excel:", error);
      alert("Error al exportar el Excel");
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
      <h1 className="theme-header">Gestión de Comprobantes</h1>

      <div className="admin-header">
        <input
          type="text"
          placeholder="Buscar por ID, cliente, documento, tipo..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="theme-button" onClick={handleSearch}>
          Buscar
        </button>
        <button
          className="theme-button filter-toggle-btn"
          onClick={() => setShowFilters(!showFilters)}>
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
                fetchComprobantes();
              }}>
              Limpiar
            </button>
          </form>
        </div>
      )}

      {loading ? (
        <div
          className="loading-container"
          style={{ padding: "2rem", textAlign: "center" }}>
          <div className="spinner-border text-pink" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p>Cargando comprobantes...</p>
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
              {filteredComprobantes.map((comprobante) => (
                <tr
                  key={comprobante.id}
                  onClick={() => setSelectedComprobante(comprobante)}
                  className={
                    selectedComprobante?.id === comprobante.id
                      ? "selected-row"
                      : ""
                  }>
                  <td>{comprobante.id}</td>
                  <td>{comprobante.pedidoId}</td>
                  <td>
                    <span
                      className={`status-badge status-${comprobante.tipo?.toLowerCase()}`}>
                      {comprobante.tipo}
                    </span>
                  </td>
                  <td>{comprobante.clienteNombre}</td>
                  <td>{comprobante.clienteDocumento || "N/A"}</td>
                  <td>{comprobante.clienteEmail}</td>
                  <td>{formatDate(comprobante.fechaEmision)}</td>
                  <td>{comprobante.numeroSerie}</td>
                  <td>{comprobante.numeroComprobante}</td>
                  <td>{formatCurrency(comprobante.subtotal)}</td>
                  <td>{formatCurrency(comprobante.total)}</td>
                  <td>
                    <button
                      className="theme-button"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleExportPdf(comprobante.id);
                      }}
                      title="Exportar a PDF">
                      PDF
                    </button>
                    <button
                      className="theme-button"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleExportExcel(comprobante.id);
                      }}
                      title="Exportar a Excel">
                      Excel
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="table-footer">
            <span>Total de comprobantes: {filteredComprobantes.length}</span>
            {selectedComprobante && (
              <div className="selected-info">
                <span>
                  Seleccionado: Comprobante #{selectedComprobante.id} -{" "}
                  {selectedComprobante.clienteNombre}
                </span>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default AdminComprobantes;
