import React from "react";
import "../styles/Nosotros.css";

const Nosotros = () => {
  return (
    <div className="container-fluid p-0">
      {/* Content Section */}
      <div className="container nosotros-content nosotros-container">
        <div className="row justify-content-center">
          <div className="col-lg-8">
            <h2 className="nosotros-title">Quiénes Somos</h2>
            <p className="lead nosotros-lead">
              En Como en Casa, nos dedicamos a crear experiencias dulces que
              deleitan los sentidos y elevan los momentos especiales.
            </p>

            <div>
              <p className="nosotros-text">
                Con una pasión por la repostería que se remonta a generaciones,
                hemos perfeccionado el arte de combinar ingredientes frescos,
                técnicas tradicionales y un toque de creatividad para ofrecer
                productos excepcionales a nuestros clientes.
              </p>

              <h2 className="nosotros-subtitle">Nuestra Historia</h2>
              <p className="nosotros-text">
                La historia de Como en Casa es una historia de amor por los
                postres. Esta pastelería ha sido un lugar de referencia para
                aquellos que buscan lo mejor en repostería artesanal. Desde
                nuestros modestos comienzos en un pequeño local hasta
                convertirnos en una institución respetada en la comunidad, hemos
                mantenido nuestro compromiso con la calidad, la frescura y el
                servicio excepcional.
              </p>

              <div className="nosotros-highlight">
                <p>
                  Bienvenido a Como en Casa, donde cada bocado es una
                  experiencia digna de recordar.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Nosotros;
