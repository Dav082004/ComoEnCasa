import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import { FaTags, FaShoppingCart } from "react-icons/fa";
import "../styles/AdminLayout.css";
import { FaFileInvoice } from "react-icons/fa6"; // Importar el icono de factura

export default function AdminLayout() {
  return (
    <div className="admin-container">
      <aside className="admin-sidebar">
        <div className="admin-logo">
          <span>Admin</span>
        </div>
        <nav className="admin-nav">
          <NavLink
            to="/admin/productos"
            className="nav-item"
            activeclassname="active"
          >
            <FaTags className="nav-icon" />
            <span>Productos</span>
          </NavLink>
          <NavLink
            to="/admin/pedidos"
            className="nav-item"
            activeclassname="active"
          >
            <FaShoppingCart className="nav-icon" />
            <span>Pedidos</span>
          </NavLink>
          <NavLink
            to="/admin/comprobantes"
            className="nav-item"
            activeclassname="active"
          >
            <FaFileInvoice className="nav-icon" />
            <span>Comprobantes</span>
          </NavLink>
        </nav>
      </aside>

      <main className="admin-main">
        <Outlet />
      </main>
    </div>
  );
}
