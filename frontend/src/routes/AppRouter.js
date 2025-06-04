import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Layout from "../components/Layout";
import Home from "../pages/Home";
import Pasteles from "../pages/Pasteles";
import Postres from "../pages/Postres";
import Eventos from "../pages/Eventos";
import Nosotros from "../pages/Nosotros";
import CrearCuenta from "../pages/CrearCuenta";
import Login from "../pages/Login";
import Tienda from "../pages/Tienda";
import Productos from "../pages/Productos";
import Carrito from "../pages/Carrito";
import Checkout from "../pages/Checkout";
import RecuperarCuenta from "../pages/RecuperarCuenta";
import { CartProvider } from "../context/CartContext";
import { AuthProvider } from "../context/AuthContext";

export const AppRouter = () => {
  return (
    <AuthProvider>
      <CartProvider>
        <Router>
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<Home />} />
              <Route path="login" element={<Login />} />
              <Route path="crear-cuenta" element={<CrearCuenta />} />
              <Route path="pasteles" element={<Pasteles />} />
              <Route path="postres" element={<Postres />} />
              <Route path="eventos" element={<Eventos />} />
              <Route path="nosotros" element={<Nosotros />} />
              <Route path="productos" element={<Productos />} />
              <Route path="checkout" element={<Checkout />} />
              <Route path="carrito" element={<Carrito />} />
              <Route path="*" element={<Tienda />} />
            <Route path="/recuperar" element={<RecuperarCuenta />} />

            </Route>
          </Routes>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
};

export default AppRouter;
