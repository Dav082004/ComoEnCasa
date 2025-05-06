import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { HeaderComponent } from "./components/HeaderComponent";
import { FooterComponent } from "./components/FooterComponent";
import Home from "./pages/Home";
import Pasteles from "./pages/Pasteles";
import Postres from "./pages/Postres";
import Eventos from "./pages/Eventos";
import Nosotros from "./pages/Nosotros";
import Contacto from "./pages/Contacto";
import CrearCuenta from "./pages/CrearCuenta";
import Login from "./pages/Login";
import "./styles/fonts.css";
import "./styles/layout.css";
import "./App.css";

function App() {
  return (
    <Router>
      <div className="app-container">
        <HeaderComponent />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/crear-cuenta" element={<CrearCuenta />} />
            <Route path="/pasteles" element={<Pasteles />} />
            <Route path="/postres" element={<Postres />} />
            <Route path="/eventos" element={<Eventos />} />
            <Route path="/nosotros" element={<Nosotros />} />
            <Route path="/contacto" element={<Contacto />} />
          </Routes>
        </main>
        <FooterComponent />
      </div>
    </Router>
  );
}

export default App;
