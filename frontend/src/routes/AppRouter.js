import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Layout from "../components/Layout";
import AdminLayout from "../components/AdminLayout";
import Home from "../pages/Home";
import Pasteles from "../pages/Pasteles";
import Postres from "../pages/Postres";
import Eventos from "../pages/Eventos";
import Nosotros from "../pages/Nosotros";
import CrearCuenta from "../pages/CrearCuenta";
import Login from "../pages/Login";
import Productos from "../pages/Productos";
import ProductDetail from "../pages/ProductDetail";
import Carrito from "../pages/Carrito";
import Checkout from "../pages/Checkout";
import RecuperarCuenta from "../pages/RecuperarCuenta";
import VerificarCuenta from "../pages/VerificarCuenta"; // ✅ Ruta corregida

import Perfil from "../pages/Perfil";
import Recomendacion from "../pages/Recomendacion";

import { CartProvider } from "../context/CartContext";
import { AuthProvider } from "../context/AuthContext";
import { ProductProvider } from "../context/ProductContext";
import Pedidos from "../pages/Pedidos";
import AdminProducts from "../pages/AdminProducts";
import AdminOrders from "../pages/AdminOrders";
import AdminComprobantes from "../pages/AdminComprobantes";
import AdminCategories from "../pages/AdminCategories";
import PagoExitoso from "../pages/PagoExitoso";
import AdminReportes from "../pages/AdminReportes";
import AdminFacturas from "../pages/AdminFacturas";


export const AppRouter = () => {
  return (
    <AuthProvider>
      <CartProvider>
        <ProductProvider>
          <Router>
            <Routes>
              {/* ✅ Alternativa temporal (si Layout está causando el error) */}
              {/* <Route path="/verificar" element={<VerificarCuenta />} /> */}

              <Route path="/" element={<Layout />}>
                <Route index element={<Home />} />
                <Route path="login" element={<Login />} />
                <Route path="crear-cuenta" element={<CrearCuenta />} />
                <Route path="pasteles" element={<Pasteles />} />
                <Route path="postres" element={<Postres />} />
                <Route path="eventos" element={<Eventos />} />
                <Route path="nosotros" element={<Nosotros />} />
                <Route path="productos" element={<Productos />} />
                <Route path="productos/:id" element={<ProductDetail />} />
                <Route path="checkout" element={<Checkout />} />
                <Route path="carrito" element={<Carrito />} />

                <Route path="recuperar" element={<RecuperarCuenta />} />
                <Route path="verificar" element={<VerificarCuenta />} />

                <Route path="perfil" element={<Perfil />} />
                <Route path="recomendacion" element={<Recomendacion />} />
                <Route path="pedidos" element={<Pedidos />} />
                <Route path="pago-exitoso" element={<PagoExitoso />} />
              </Route>
              <Route path="admin" element={<AdminLayout />}>
                <Route path="productos" element={<AdminProducts />} />
                <Route path="categorias" element={<AdminCategories />} />
                <Route path="pedidos" element={<AdminOrders />} />
                <Route path="comprobantes" element={<AdminComprobantes />} />
                <Route path="facturas" element={<AdminFacturas />} />
                <Route path="reportes" element={<AdminReportes />} />
              </Route>
            </Routes>
            <ToastContainer
              position="top-right"
              autoClose={3000}
              hideProgressBar={false}
              newestOnTop={true}
              closeOnClick={true}
              rtl={false}
              pauseOnFocusLoss={false}
              draggable={true}
              pauseOnHover={false}
              theme="light"
              limit={3}
            />
          </Router>
        </ProductProvider>
      </CartProvider>
    </AuthProvider>
  );
};

export default AppRouter;
