import React, { useState, useEffect } from 'react';
import '../styles/Admin.css';
import { getAllOrders } from '../services/pedidoService';

export default function AdminOrders() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    getAllOrders().then(setOrders);
  }, []);

  return (
   <div className="page-container">
      <h2 className="theme-header">Gestión de Pedidos</h2>
      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>Usuario</th><th>Creación</th><th>Entrega</th><th>Estado</th><th>Subtotal</th><th>Total</th><th>Dirección</th><th>Notas</th><th>Factura</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(o => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.usuarioNombre}</td>
              <td>{new Date(o.fechaCreacion).toLocaleString()}</td>
              <td>{o.fechaEntrega ? new Date(o.fechaEntrega).toLocaleString() : '-'}</td>
              <td>{o.estado}</td>
              <td>{o.subtotal}</td>
              <td>{o.costoTotal}</td>
              <td>{o.direccionEntrega}</td>
              <td>{o.notas}</td>
              <td>{o.necesitaFactura ? 'Sí' : 'No'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}