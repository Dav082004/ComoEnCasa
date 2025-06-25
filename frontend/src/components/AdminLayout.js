import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import { FaTags, FaShoppingCart, FaList, FaChartPie } from "react-icons/fa";
import { FaFileInvoice } from "react-icons/fa6";
import { FaRegFile } from "react-icons/fa6";
import "../styles/AdminLayout.css";
import { HeaderComponent } from "../components/HeaderComponent";

export default function AdminLayout() {
  return (
    <div className="admin-container">
      <aside className="admin-sidebar">
        <div className="admin-logo">
          <span>Secciones</span>
        </div>
        <nav className="admin-nav">
          <NavLink
            to="/admin/productos"
            className="nav-item"
            activeclassname="active">
            <FaTags className="nav-icon" />
            <span>Productos</span>
          </NavLink>
          <NavLink
            to="/admin/categorias"
            className="nav-item"
            activeclassname="active">
            <FaList className="nav-icon" />
            <span>Categorías</span>
          </NavLink>
          <NavLink
            to="/admin/pedidos"
            className="nav-item"
            activeclassname="active">
            <FaShoppingCart className="nav-icon" />
            <span>Pedidos</span>
          </NavLink>
          <NavLink
            to="/admin/comprobantes"
            className="nav-item"
            activeclassname="active">
            <FaFileInvoice className="nav-icon" />
            <span>Boletas</span>
          </NavLink>
          <NavLink
            to="/admin/facturas"  
            className="nav-item"
            activeclassname="active">
            <FaRegFile className="nav-icon" />
            <span>Facturas</span>
          </NavLink>
          <NavLink
            to="/admin/reportes"  
            className="nav-item"
            activeclassname="active">
            <FaChartPie className="nav-icon" />
            <span>Reportes de Ventas</span>
          </NavLink>
        </nav>
      </aside>

      <main className="admin-main">
        <HeaderComponent />
        <Outlet />
      </main>
    </div>
  );
}
