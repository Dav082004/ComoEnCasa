import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { FaFilter } from 'react-icons/fa';
import {
  listarComprobantes,
  exportarExcel,
  exportarPdf
} from '../services/comprobanteService';
import '../styles/Admin.css';

export default function AdminComprobantes() {
  const [comprobantes, setComprobantes] = useState([]);
  const [showFilters, setShowFilters] = useState(false);
  const [filtros, setFiltros] = useState({
    clienteDocumento: '',
    pedidoId: '',
    desde: '',
    hasta: ''
  });

  useEffect(() => {
    cargar();
  }, []);

  const cargar = async () => {
    try {
      // eliminamos filtros vacíos
      const params = {};
      Object.entries(filtros).forEach(([k, v]) => { if (v) params[k] = v; });
      const data = await listarComprobantes(params);
      setComprobantes(data);
    } catch (e) {
      toast.error('Error al cargar comprobantes');
    }
  };

  const handleCambio = e => {
    setFiltros({ ...filtros, [e.target.name]: e.target.value });
  };

  const aplicar = e => {
    e.preventDefault();
    cargar();
  };

  const descargar = async (id, tipo) => {
    try {
      const blob = tipo === 'xlsx'
        ? await exportarExcel(id)
        : await exportarPdf(id);
      const url = URL.createObjectURL(new Blob([blob]));
      const a = document.createElement('a');
      a.href = url;
      a.download = `comprobante_${id}.${tipo === 'xlsx' ? 'xlsx' : 'pdf'}`;
      document.body.appendChild(a);
      a.click();
      a.remove();
    } catch {
      toast.error('Error al descargar');
    }
  };

  return (
    <div className="page-container">
      <div className="admin-header">
        <h2 className="theme-header">Comprobantes de Pago</h2>
        <button
          className="theme-button filter-toggle-btn"
          onClick={() => setShowFilters(prev => !prev)}
        >
          <FaFilter style={{ marginRight: '0.5rem' }} />
          {showFilters ? 'Ocultar filtros' : 'Mostrar filtros'}
        </button>
      </div>

      {showFilters && (
        <div className="filter-panel">
          <form onSubmit={aplicar} className="filter-form">
            <input
              name="clienteDocumento"
              placeholder="DNI / RUC / CE"
              value={filtros.clienteDocumento}
              onChange={handleCambio}
            />
            <input
              name="pedidoId"
              placeholder="ID Pedido"
              value={filtros.pedidoId}
              onChange={handleCambio}
            />
            <input
              name="desde"
              type="date"
              value={filtros.desde}
              onChange={handleCambio}
            />
            <input
              name="hasta"
              type="date"
              value={filtros.hasta}
              onChange={handleCambio}
            />
            <button type="submit" className="theme-button">
              Filtrar
            </button>
          </form>
        </div>
      )}

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>Pedido</th><th>Cliente</th><th>Documento</th><th>Correo</th>
            <th>Fecha Emisión</th><th>Tipo</th><th>Serie</th><th>Número</th>
            <th>Subtotal</th><th>Total</th><th>Exportar</th>
          </tr>
        </thead>
        <tbody>
          {comprobantes.map(c => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.pedidoId}</td>
              <td>{c.clienteNombre}</td>
              <td>{c.clienteDocumento}</td>
              <td>{c.clienteEmail}</td>
              <td>{new Date(c.fechaEmision).toLocaleString()}</td>
              <td>{c.tipo}</td>
              <td>{c.numeroSerie}</td>
              <td>{c.numeroComprobante}</td>
              <td>{c.subtotal}</td>
              <td>{c.total}</td>
              <td>
                <button
                  className="theme-button"
                  onClick={() => descargar(c.id, 'pdf')}
                >
                  PDF
                </button>
                <button
                  className="theme-button"
                  onClick={() => descargar(c.id, 'xlsx')}
                >
                  Excel
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}