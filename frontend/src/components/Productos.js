import React, { useState } from "react";
import "../styles/Productos.css";

export default function Productos({ productos, onAgregarAlCarrito }) {
  const [activo, setActivo] = useState(null);
  const [cantidad, setCantidad] = useState(1);

  const abrir = (prod) => {
    setActivo(prod);
    setCantidad(1);
  };
  const cerrar = () => setActivo(null);

  const inc = () => setCantidad((c) => c + 1);
  const dec = () => setCantidad((c) => Math.max(1, c - 1));

  const agregar = () => {
    onAgregarAlCarrito(activo, cantidad);
    cerrar();
  };

  return (
    <>
      <div className="productos-grid">
        {productos.map((p) => (
          <div key={p.id} className="producto-card">
            <img src={p.img} alt={p.nombre} />
            <h3 className="producto-nombre" onClick={() => abrir(p)}>
              {p.nombre}
            </h3>
            <p className="producto-precio">S/. {p.precio.toFixed(2)}</p>
          </div>
        ))}
      </div>

      {activo && (
        <div className="modal-overlay" onClick={cerrar}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <img
              src={activo.imgGrande}
              alt={activo.nombre}
              className="modal-img"
            />
            <h2>{activo.nombre}</h2>
            <p>{activo.descripcion}</p>
            <div className="cantidad">
              <button onClick={dec}>–</button>
              <span>{cantidad}</span>
              <button onClick={inc}>+</button>
            </div>
            <button onClick={agregar} className="modal-agregar">
              Agregar al carrito
            </button>
          </div>
        </div>
      )}
    </>
  );
}
