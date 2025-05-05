import React from "react";
import "./estilos.css";

import logo from "./img/logo.png";
import slide from "./img/slide.jpg";
import bannerTortas from "./img/banner_tortas.webp";
import bannerPostres from "./img/banner_postres.jpg";
import bannerEventos from "./img/banner_eventos.webp";

const PasteleriaPage = () => {
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
            <path fillRule="evenodd" d="M2.5 12a.5.5 0 0 1 .5-.5h10a.5.5 
              0 0 1 0 1H3a.5.5 0 0 1-.5-.5m0-4a.5.5 
              0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 
              0 0 1-.5-.5m0-4a.5.5 0 0 1 .5-.5h10a.5.5 
              0 0 1 0 1H3a.5.5 0 0 1-.5-.5" />
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
        <img src={slide} alt="Slide" />
        <h1 className="text-center slogan">Tortas y pasteles que recuerden al hogar</h1>

        <div className="gallery">
          <div>
            <img src={bannerTortas} alt="Tortas" />
            <a className="button" href="tortas.html">Tortas</a>
          </div>
          <div>
            <img src={bannerPostres} alt="Postres" />
            <a className="button" href="postres.html">Postres</a>
          </div>
          <div>
            <img src={bannerEventos} alt="Eventos" />
            <a className="button" href="eventos.html">Eventos</a>
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
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="36"
          height="36"
          fill="currentColor"
          className="bi bi-whatsapp"
          viewBox="0 0 16 16"
        >
          <path d="M13.601 2.326A7.85 7.85..." />
        </svg>
      </a>
    </div>
  );
};

export default Home;
