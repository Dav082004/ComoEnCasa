import React from "react";
import "../styles/layout.css";
import TortadeChocolate from "../assets/tortas/torta_1.webp";
import TortadeVainilla from "../assets/tortas/torta_2.webp";
import TortadeLucuma from "../assets/tortas/torta_3.webp";
import TortadeFresa from "../assets/tortas/torta_4.webp";
import Cumpleaños from "../assets/eventos/eventos_1.webp";
import Graduacion from "../assets/eventos/eventos_3.webp";
import SanValentin from "../assets/eventos/eventos_4.webp";

const Pasteles = () => {
  return (
    <main className="main-content">
      <section className="container container-features">
        <div className="card-feature">
          <i className="fa-solid fa-plane-up"></i>
          <div className="feature-content">
            <span>Envío gratuito</span>
            <p>En pedido superior a S/.100</p>
          </div>
        </div>
        <div className="card-feature">
          <i className="fa-solid fa-wallet"></i>
          <div className="feature-content">
            <span>Contraentrega</span>
            <p>Pago realizado al momento de recibir el producto</p>
          </div>
        </div>
        <div className="card-feature">
          <i className="fa-solid fa-gift"></i>
          <div className="feature-content">
            <span>Tarjeta regalo especial</span>
            <p>Ofrece bonos especiales con regalo</p>
          </div>
        </div>
        <div className="card-feature">
          <i className="fa-solid fa-headset"></i>
          <div className="feature-content">
            <span>Servicio al cliente</span>
            <p>LLámenos al 123-456-789</p>
          </div>
        </div>
      </section>

      <section className="container top-products">
        <div className="container-options">
          <span>Destacados</span>
          <span>Más recientes</span>
          <span>Mejores Vendidos</span>
        </div>

        <div className="container-products">
          {[
            {
              img: TortadeChocolate,
              name: "Torta de Chocolate",
              price: "S/.58.00",
              oldPrice: "S/.70.00",
              discount: "-13%",
              stars: 4,
            },
            {
              img: TortadeVainilla,
              name: "Torta de Vainilla",
              price: "S/.45.00",
              oldPrice: "S/.60.00",
              discount: "-22%",
              stars: 3,
            },
            {
              img: TortadeLucuma,
              name: "Torta de Lucuma",
              price: "S/.60.00",
              stars: 5,
            },
            {
              img: TortadeFresa,
              name: "Torta de Fresa",
              price: "S/.42.00",
              stars: 4,
            },
          ].map((product, index) => (
            <div className="card-product" key={index}>
              <div className="container-img">
                <img src={product.img} alt={product.name} />
                {product.discount && (
                  <span className="discount">{product.discount}</span>
                )}
                <div className="button-group">
                  <span>
                    <i className="fa-regular fa-eye"></i>
                  </span>
                  <span>
                    <i className="fa-regular fa-heart"></i>
                  </span>
                  <span>
                    <i className="fa-solid fa-code-compare"></i>
                  </span>
                </div>
              </div>
              <div className="content-card-product">
                <div className="stars">
                  {[...Array(5)].map((_, i) => (
                    <i
                      key={i}
                      className={
                        i < product.stars
                          ? "fa-solid fa-star"
                          : "fa-regular fa-star"
                      }></i>
                  ))}
                </div>
                <h3>{product.name}</h3>
                <span className="add-cart">
                  <i className="fa-solid fa-basket-shopping"></i>
                </span>
                <p className="price">
                  {product.price}{" "}
                  {product.oldPrice && <span>{product.oldPrice}</span>}
                </p>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="container specials">
        <h1 className="heading-1">Especiales</h1>

        <div className="container-products">
          {[
            {
              img: Cumpleaños,
              name: "Torta de Cumpleaños",
              price: "S/.60.00",
              oldPrice: "S/.82.00",
              discount: "-15%",
              stars: 4,
            },
            {
              img: Graduacion,
              name: "Torta de Graduación",
              price: "S/.45.00",
              oldPrice: "S/.60.00",
              discount: "-10%",
              stars: 3,
            },
            {
              img: SanValentin,
              name: "Torta de San Valentin",
              price: "S/.60.00",
              stars: 5,
            },
          ].map((product, index) => (
            <div className="card-product" key={index}>
              <div className="container-img">
                <img src={product.img} alt={product.name} />
                {product.discount && (
                  <span className="discount">{product.discount}</span>
                )}
                <div className="button-group">
                  <span>
                    <i className="fa-regular fa-eye"></i>
                  </span>
                  <span>
                    <i className="fa-regular fa-heart"></i>
                  </span>
                  <span>
                    <i className="fa-solid fa-code-compare"></i>
                  </span>
                </div>
              </div>
              <div className="content-card-product">
                <div className="stars">
                  {[...Array(5)].map((_, i) => (
                    <i
                      key={i}
                      className={
                        i < product.stars
                          ? "fa-solid fa-star"
                          : "fa-regular fa-star"
                      }></i>
                  ))}
                </div>
                <h3>{product.name}</h3>
                <span className="add-cart">
                  <i className="fa-solid fa-basket-shopping"></i>
                </span>
                <p className="price">
                  {product.price}{" "}
                  {product.oldPrice && <span>{product.oldPrice}</span>}
                </p>
              </div>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
};

export default Pasteles;
