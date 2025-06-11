import React, { useState } from 'react';
import '../styles/Admin.css'; 

const AdminComprobantes = () => {
  const [comprobantes, setComprobantes] = useState([]);
  const [nuevoComprobante, setNuevoComprobante] = useState(null);
  const [mostrarModal, setMostrarModal] = useState(false);

  const abrirModalAgregar = () => {
    setNuevoComprobante({
      tipo: 'Boleta',
      cliente: '',
      dni: '',
      ruc: '',
      direccion: '',
      fecha_emision: '',
      productos: [],
      subtotal: 0,
      igv: 0,
      total: 0,
    });
    setMostrarModal(true);
  };

  const cerrarModal = () => {
    setMostrarModal(false);
  };

  const guardarComprobante = () => {
    if (nuevoComprobante) {
      const nuevo = { ...nuevoComprobante, id: Date.now() };
      setComprobantes([...comprobantes, nuevo]);
      setMostrarModal(false);
    }
  };

  return (
    <div className="page-container">
      <h1 className="theme-header">Gestión de Comprobantes</h1>
      <div className="admin-header">
        <button className="theme-button" onClick={abrirModalAgregar}>Agregar</button>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Tipo</th>
            <th>Cliente</th>
            <th>DNI / RUC</th>
            <th>Fecha Emisión</th>
            <th>Subtotal</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {comprobantes.map((c) => (
            <tr key={c.id}>
              <td>{c.tipo}</td>
              <td>{c.cliente}</td>
              <td>{c.tipo === 'Factura' ? c.ruc : c.dni}</td>
              <td>{c.fecha_emision}</td>
              <td>S/ {c.subtotal.toFixed(2)}</td>
              <td>S/ {c.total.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal para nuevo comprobante */}
      {mostrarModal && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <h2>Nueva {nuevoComprobante.tipo}</h2>
            <form className="product-form">
              <select
                value={nuevoComprobante.tipo}
                onChange={(e) =>
                  setNuevoComprobante({ ...nuevoComprobante, tipo: e.target.value })
                }
              >
                <option value="Boleta">Boleta</option>
                <option value="Factura">Factura</option>
              </select>
              <input
                type="text"
                placeholder="Nombre"
                value={nuevoComprobante.cliente}
                onChange={(e) =>
                  setNuevoComprobante({ ...nuevoComprobante, cliente: e.target.value })
                }
              />
              {nuevoComprobante.tipo === 'Factura' ? (
                <input
                  type="text"
                  placeholder="RUC"
                  value={nuevoComprobante.ruc}
                  onChange={(e) =>
                    setNuevoComprobante({ ...nuevoComprobante, ruc: e.target.value })
                  }
                />
              ) : (
                <input
                  type="text"
                  placeholder="DNI"
                  value={nuevoComprobante.dni}
                  onChange={(e) =>
                    setNuevoComprobante({ ...nuevoComprobante, dni: e.target.value })
                  }
                />
              )}
              <input
                type="text"
                placeholder="Dirección"
                value={nuevoComprobante.direccion}
                onChange={(e) =>
                  setNuevoComprobante({ ...nuevoComprobante, direccion: e.target.value })
                }
              />
              <input
                type="date"
                value={nuevoComprobante.fecha_emision}
                onChange={(e) =>
                  setNuevoComprobante({ ...nuevoComprobante, fecha_emision: e.target.value })
                }
              />
              {/* Aquí podrías agregar productos si deseas */}
              <input
                type="number"
                placeholder="Subtotal"
                value={nuevoComprobante.subtotal}
                onChange={(e) =>
                  setNuevoComprobante({
                    ...nuevoComprobante,
                    subtotal: parseFloat(e.target.value),
                    igv: parseFloat(e.target.value) * 0.18,
                    total: parseFloat(e.target.value) * 1.18,
                  })
                }
              />
              <input type="text" value={`IGV: S/ ${nuevoComprobante.igv.toFixed(2)}`} disabled />
              <input type="text" value={`Total: S/ ${nuevoComprobante.total.toFixed(2)}`} disabled />
            </form>
            <div className="modal-actions">
              <button className="theme-button" onClick={guardarComprobante}>Aceptar</button>
              <button className="theme-button" onClick={cerrarModal}>Cancelar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminComprobantes;
