import React from "react";

import postre1 from "../assets/postres/postre_1.webp";
import postre2 from "../assets/postres/postre_2.webp";
import postre3 from "../assets/postres/postre_3.webp";
import postre4 from "../assets/postres/postre_4.webp";

const Postres = () => {
  return (
    <div className="container">

      <div className="content">

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
