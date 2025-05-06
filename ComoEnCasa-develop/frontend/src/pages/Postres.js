import React from "react";
import "./estilos.css";

import logo from "./img/logo.png";
import bannerPostres from "./img/banner_postres.jpg";

import postre1 from "./img/postres/postre_1.webp";
import postre2 from "./img/postres/postre_2.webp";
import postre3 from "./img/postres/postre_3.webp";
import postre4 from "./img/postres/postre_4.webp";

const Postres = () => {
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
          <a href="Eventos.html">Eventos</a>
        </div>
      </nav>

      <div className="content">
        <div className="text-center">
          <img src={bannerPostres} alt="Banner Postres" />
        </div>
        <h1 className="title">POSTRES</h1>

        <div className="productos">
          {[
            { img: postre1, nombre: "Pye de Manzana", precio: "S/. 50.00" },
            { img: postre2, nombre: "Mousse de Lúcuma", precio: "S/. 43.00" },
            { img: postre3, nombre: "Mousse de Maracuyá", precio: "S/. 50.00" },
            { img: postre4, nombre: "Delirium", precio: "S/. 45.00" },
          ].map((postre, index) => (
            <div className="text-center" key={index}>
              <img src={postre.img} alt={postre.nombre} />
              <h2>{postre.nombre}</h2>
              <h3>{postre.precio}</h3>
            </div>
          ))}
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

export default Postres;
