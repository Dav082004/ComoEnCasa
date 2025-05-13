import React from "react";
import { Link } from "react-router-dom";
import slide from "../assets/slide.jpg";
import PastelDeChocolate from "../assets/tortas/torta_1.webp";
import PiedeMazana from "../assets/postres/postre_1.webp";
import TestimonialCarousel from "../components/TestimonialCarousel";
import "../styles/home.css";

const Home = () => {
  // Eliminamos el estado del índice ya que ahora está manejado por TestimonialCarousel
  return (
    <div className="container">
      {/* Hero Section */}
      <div className="content">
        <img
          src={slide}
          alt="Pastelería Como en Casa"
          className="w-100"
          style={{ maxHeight: "70vh", objectFit: "cover" }}
        />
        <h1 className="text-center slogan">
          Tortas y pasteles que recuerden al hogar
        </h1>
        <br />
      </div>

      {/* Categories Section */}
      <section className="container top-categories mb-5">
        <div className="row g-4">
          <div className="col-md-4">
            <Link to="/pasteles" className="card-category category-Pasteles">
              <p>Pasteles</p>
            </Link>
          </div>
          <div className="col-md-4">
            <Link to="/postres" className="card-category category-Postres">
              <p>Postres</p>
            </Link>
          </div>
          <div className="col-md-4">
            <Link to="/eventos" className="card-category category-Eventos">
              <p>Eventos</p>
            </Link>
          </div>
        </div>
      </section>

      {/* Blog Section */}
      <section className="container blogs mb-5">
        <div className="row g-4">
          <div className="col-md-6">
            <div className="card-blog h-100">
              <div className="container-img">
                <img
                  src={PastelDeChocolate}
                  alt="Pastel de Chocolate"
                  className="img-fluid"
                />
              </div>
              <div className="content-blog">
                <h3>Pastel de Chocolate</h3>
                <span>02 Mayo 2025</span>
                <p>
                  Un postre delicioso y esponjoso, hecho con capas de bizcocho
                  de cacao y cubierto con una rica crema o ganache de chocolate.
                </p>
              </div>
            </div>
          </div>
          <div className="col-md-6">
            <div className="card-blog h-100">
              <div className="container-img">
                <img
                  src={PiedeMazana}
                  alt="Pie de Manzana"
                  className="img-fluid"
                />
              </div>
              <div className="content-blog">
                <h3>Pie de Manzana</h3>
                <span>25 Abril 2025</span>
                <p>
                  Clásico postre con relleno de manzanas caramelizadas y
                  especias, envuelto en una crujiente masa dorada.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <TestimonialCarousel />
    </div>
  );
};

export default Home;
