import React from "react";
import "../styles/Layout.css";
import Cumpleaños from "../assets/eventos/eventos_1.webp";
import Graduacion from "../assets/eventos/eventos_3.webp";
import SanValentin from "../assets/eventos/eventos_4.webp";

const Pasteles = () => {
  const pastelesEspeciales = [
    {
      img: Cumpleaños,
      nombre: "Torta de Cumpleaños",
      precio: "S/. 60.00",
      precioAnterior: "S/. 82.00",
      descuento: "-15%",
    },
    {
      img: Graduacion,
      nombre: "Torta de Graduación",
      precio: "S/. 45.00",
      precioAnterior: "S/. 60.00",
      descuento: "-10%",
    },
    {
      img: SanValentin,
      nombre: "Torta de San Valentín",
      precio: "S/. 60.00",
    },
  ];

  return (
    <div className="container py-5 pastel-bg">
      <div className="text-center mb-5">
        <h1 className="pastel-title">Pasteles Especiales</h1>
        <p className="pastel-subtitle">
          Tortas para cada ocasión: celebra con dulzura
        </p>
      </div>

      <div className="row g-4">
        {pastelesEspeciales.map((pastel, index) => (
          <div className="col-md-4 col-sm-6" key={index}>
            <div className="card h-100 pastel-card">
              <div className="position-relative">
                <img
                  src={pastel.img}
                  alt={pastel.nombre}
                  className="card-img-top pastel-img"
                />
                {pastel.descuento && (
                  <span className="badge bg-danger position-absolute top-0 end-0 m-2">
                    {pastel.descuento}
                  </span>
                )}
              </div>
              <div className="card-body text-center">
                <h5 className="card-title pastel-product-title">
                  {pastel.nombre}
                </h5>
                <p className="card-text pastel-price">
                  {pastel.precio}{" "}
                  {pastel.precioAnterior && (
                    <span className="text-muted text-decoration-line-through ms-2">
                      {pastel.precioAnterior}
                    </span>
                  )}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Pasteles;
