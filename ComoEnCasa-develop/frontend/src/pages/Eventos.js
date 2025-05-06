import React from "react";
import "./estilos.css";

import logo from "./img/logo.png";
import bannerEventos from "./img/banner_eventos.webp";
import evento1 from "./img/eventos/eventos_1.webp";
import evento2 from "./img/eventos/eventos_2.webp";
import evento3 from "./img/eventos/eventos_3.webp";
import evento4 from "./img/eventos/eventos_4.webp";

const Eventos = () => {
  return (
    <div className="container">
      <header className="text-center">
        <a href="index.html">
          <img src={logo} alt="Logo" className="logo" />
        </a>
      </header>

      <nav>
        <div id="navHamb">
          <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" fill="currentColor" className="bi bi-list" viewBox="0 0 16 16">
            <path fillRule="evenodd" d="M2.5 12a.5.5... (cortar para brevedad)" />
          </svg>
        </div>
        <div id="navMenu">
          <a href="index.html">Inicio</a>
          <a href="pasteleria.html">Pastelería</a>
          <a href="tortas.html">Tortas</a>
          <a href="postres.html">Postres</a>
          <a href="eventos.html">Eventos</a>
        </div>
      </nav>

      <div className="content">
        <div className="text-center">
          <img src={bannerEventos} alt="Banner Eventos" />
        </div>
        <h1 className="title">Eventos</h1>

        <div className="productos">
          <div className="text-center">
            <img src={evento1} alt="Torta Cumpleañera" />
            <h2>Torta Cumpleañera</h2>
            <h3>S/. 45.00</h3>
          </div>
          <div className="text-center">
            <img src={evento2} alt="Torta Rosa Vainilla" />
            <h2>Torta Rosa Vainilla</h2>
            <h3>S/. 45.00</h3>
          </div>
          <div className="text-center">
            <img src={evento3} alt="Torta Graduación" />
            <h2>Torta Graduación</h2>
            <h3>S/. 45.00</h3>
          </div>
          <div className="text-center">
            <img src={evento4} alt="Torta San Valentín" />
            <h2>Torta San Valentín</h2>
            <h3>S/. 45.00</h3>
          </div>
        </div>
      </div>

      <footer className="text-center">
        <p>Derechos reservados Como en Casa Pastelería | 2025 | Perú</p>
      </footer>

      <a
        className="whatsapp"
        href="https://wa.me/123456789?text=Hola,%20¿cómo%20estás?"
        target="_blank"
        rel="noopener noreferrer"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" fill="currentColor" className="bi bi-whatsapp" viewBox="0 0 16 16">
          <path d="M13.601 2.326A7.85... (cortar para brevedad)" />
        </svg>
      </a>
    </div>
  );
};

export default Eventos;
