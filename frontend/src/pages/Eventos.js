import React from "react";

import evento1 from "../assets/eventos/eventos_1.webp";
import evento2 from "../assets/eventos/eventos_2.webp";
import evento3 from "../assets/eventos/eventos_3.webp";
import evento4 from "../assets/eventos/eventos_4.webp";

const Eventos = () => {
  return (
    <div className="container py-5 pastel-bg-events">
      <div className="text-center mb-5">
        <h1 className="pastel-title-events">Pasteles para Eventos</h1>
        <p className="pastel-subtitle">
          Celebra tus momentos especiales con nosotros
        </p>
      </div>

      <div className="row g-4">
        {[
          { img: evento1, nombre: "Torta Cumpleañera", precio: "S/. 45.00" },
          { img: evento2, nombre: "Torta Rosa Vainilla", precio: "S/. 45.00" },
          { img: evento3, nombre: "Torta Graduación", precio: "S/. 45.00" },
          { img: evento4, nombre: "Torta San Valentín", precio: "S/. 45.00" },
        ].map((evento, index) => (
          <div className="col-md-3 col-sm-6" key={index}>
            <div className="card h-100 pastel-card">
              <img
                src={evento.img}
                alt={evento.nombre}
                className="card-img-top pastel-img"
              />
              <div className="card-body text-center">
                <h5 className="card-title pastel-product-title">
                  {evento.nombre}
                </h5>
                <p className="card-text pastel-price">{evento.precio}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Eventos;
