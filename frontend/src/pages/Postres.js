import React from "react";
import postre1 from "../assets/postres/postre_1.webp";
import postre2 from "../assets/postres/postre_2.webp";
import postre3 from "../assets/postres/postre_3.webp";
import postre4 from "../assets/postres/postre_4.webp";

const Postres = () => {
  return (
    <div className="container py-5 pastel-bg">
      <div className="text-center mb-5">
        <h1 className="pastel-title">Nuestros Postres</h1>
        <p className="pastel-subtitle">Delicias dulces para endulzar tu día</p>
      </div>

      <div className="row g-4">
        {[
          { img: postre1, nombre: "Pye de Manzana", precio: "S/. 50.00" },
          { img: postre2, nombre: "Mousse de Lúcuma", precio: "S/. 43.00" },
          { img: postre3, nombre: "Mousse de Maracuyá", precio: "S/. 50.00" },
          { img: postre4, nombre: "Delirium", precio: "S/. 45.00" },
        ].map((postre, index) => (
          <div className="col-md-3 col-sm-6" key={index}>
            <div className="card h-100 pastel-card">
              <img
                src={postre.img}
                alt={postre.nombre}
                className="card-img-top pastel-img"
              />
              <div className="card-body text-center">
                <h5 className="card-title pastel-product-title">
                  {postre.nombre}
                </h5>
                <p className="card-text pastel-price">{postre.precio}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Postres;
