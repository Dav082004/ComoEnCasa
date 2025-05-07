import React from "react";
import "./estilos.css";
import "./Header.css";
import "./layout.css";
import "./Footer.css";

import logo from './img/logo.png';
import pasteleriaImg from './img/pasteleria.jpg';

const Nosotros = () => {
  return (
    <div className="container">
      <header className="text-center">
        <a href="index.html">
          <img src={logo} alt="Logo" className="logo" />
        </a>
      </header>

      <div className="content">
        <img src={pasteleriaImg} alt="Pastelería" />
        <h1 className="title">COMO EN CASA</h1>
        <h2>Quienes Somos</h2>
        <p>
          En Como en Casa, nos dedicamos a crear experiencias dulces que deleitan los sentidos y elevan los momentos especiales. Con una pasión por la repostería que se remonta a generaciones, hemos perfeccionado el arte de combinar ingredientes frescos, técnicas tradicionales y un toque de creatividad para ofrecer productos excepcionales a nuestros clientes.
        </p>
        <h2>Nuestra Historia</h2>
        <p>
          La historia de Como en Casa es una historia de amor por los postres. Esta pastelería ha sido un lugar de referencia para aquellos que buscan lo mejor en repostería artesanal. Desde nuestros modestos comienzos en un pequeño local hasta convertirnos en una institución respetada en la comunidad, hemos mantenido nuestro compromiso con la calidad, la frescura y el servicio excepcional.
        </p>
        <p>Bienvenido a Como en Casa, donde cada bocado es una experiencia digna de recordar.</p>
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

export default Nosotros;
