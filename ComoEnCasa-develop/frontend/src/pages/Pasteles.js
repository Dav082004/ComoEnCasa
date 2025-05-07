import React from "react";



import torta1 from "../assets/tortas/torta_1.webp";
import torta2 from "../assets/tortas/torta_2.webp";
import torta3 from "../assets/tortas/torta_3.webp";
import torta4 from "../assets/tortas/torta_4.webp";

const Pasteles = () => {
  return (
    <div className="container">


      <div className="content">

        <h1 className="title">TORTAS</h1>

        <div className="productos">
          {[
            { img: torta1, nombre: "Torta de chocolate", precio: "S/. 45.00" },
            { img: torta2, nombre: "Torta de Vainilla", precio: "S/. 45.00" },
            { img: torta3, nombre: "Torta de Lúcuma", precio: "S/. 45.00" },
            { img: torta4, nombre: "Torta de Fresa", precio: "S/. 45.00" },
          ].map((torta, index) => (
            <div className="text-center" key={index}>
              <img src={torta.img} alt={torta.nombre} />
              <h2>{torta.nombre}</h2>
              <h3>{torta.precio}</h3>
            </div>
          ))}
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

export default Pasteles;
