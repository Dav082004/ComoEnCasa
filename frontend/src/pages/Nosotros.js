import React from "react";
const Nosotros = () => {
  return (
    <div className="container-fluid p-0">
      {/* Content Section */}
      <div className="container py-5" style={{ backgroundColor: "#fff9fa" }}>
        <div className="row justify-content-center">
          <div className="col-lg-8">
            <h2
              className="text-center mb-4 fw-bold"
              style={{
                color: "#d63384",
                fontFamily: "cursive",
                fontSize: "2.5rem",
              }}>
              Quiénes Somos
            </h2>
            <p className="lead text-center mb-5" style={{ color: "#5a1a4a" }}>
              En Como en Casa, nos dedicamos a crear experiencias dulces que
              deleitan los sentidos y elevan los momentos especiales.
            </p>

            <div>
              <p
                className="mb-4"
                style={{ color: "#5a1a4a", lineHeight: "1.8" }}>
                Con una pasión por la repostería que se remonta a generaciones,
                hemos perfeccionado el arte de combinar ingredientes frescos,
                técnicas tradicionales y un toque de creatividad para ofrecer
                productos excepcionales a nuestros clientes.
              </p>

              <h2
                className="mt-5 mb-4 fw-bold"
                style={{
                  color: "#d63384",
                  fontFamily: "cursive",
                  fontSize: "2.5rem",
                }}>
                Nuestra Historia
              </h2>
              <p
                className="mb-4"
                style={{ color: "#5a1a4a", lineHeight: "1.8" }}>
                La historia de Como en Casa es una historia de amor por los
                postres. Esta pastelería ha sido un lugar de referencia para
                aquellos que buscan lo mejor en repostería artesanal. Desde
                nuestros modestos comienzos en un pequeño local hasta
                convertirnos en una institución respetada en la comunidad, hemos
                mantenido nuestro compromiso con la calidad, la frescura y el
                servicio excepcional.
              </p>

              <div
                className="p-4 mt-5"
                style={{
                  backgroundColor: "#ffecf1",
                  borderLeft: "4px solid #e83e8c",
                  borderRadius: "0 8px 8px 0",
                }}>
                <p className="mb-0 text-center" style={{ color: "#5a1a4a" }}>
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
