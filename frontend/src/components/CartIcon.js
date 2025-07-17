import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import '../styles/Admin.css'; // si necesitas estilos

export default function AdminLayout() {
  return (
    <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#fff0f6' }}>
      <nav style={{
        width: '200px',
        padding: '1rem',
        backgroundColor: '#ffe4f0'
      }}>
        <h3 style={{ color: '#ff6ba6', fontFamily: 'Pacifico, cursive' }}>Admin</h3>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          <li><Link to="/admin/productos">Productos</Link></li>
          <li><Link to="/admin/pedidos">Pedidos</Link></li>
        </ul>
      </nav>
      <main style={{ flex: 1, padding: '2rem' }}>
        <Outlet />
      </main>
    </div>
  );
}