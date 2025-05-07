import React from "react";

import logo from "../assets/logo.png";
import slide from "../assets/slide.jpg";
import bannerTortas from "../assets/banner_tortas.webp";
import bannerPostres from "../assets/banner_postres.jpg";
import bannerEventos from "../assets/banner_eventos.webp";

const Home = () => {
  return (
    <div className="container">

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
