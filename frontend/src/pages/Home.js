import React from "react";
import { Link, useNavigate } from "react-router-dom";
import slide from "../assets/slide.jpg";
import TortaRosa from "../assets/imgReales/TortaRosa.png";
import TortaSherk from "../assets/imgReales/TortaSherk.png";
import TestimonialCarousel from "../components/TestimonialCarousel";
import { useAuth } from "../context/AuthContext";
import "../styles/Home.css";

const Home = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const handleRecomendacionClick = () => {
    navigate("/recomendacion");
  };

  return (
    <div className="container">
      {/* Hero Section */}
      <div className="content">
        <img src={slide} alt="Pastelería Como en Casa" className="hero-image" />
        <h1 className="text-center slogan">
          Tortas y pasteles que recuerden al hogar
        </h1>

        {/* Botón Compra Aquí debajo del slogan */}
        <div className="text-center mt-4 mb-5">
          <Link
            to="/productos"
            className="btn btn-primary btn-lg compra-productos-btn">
            🛒 Compra Aquí
          </Link>
        </div>
      </div>

      {/* Blog Section */}
      <section className="container blogs mb-5">
        <div className="row g-4">
          <div className="col-md-6">
            <div className="card-blog">
              <div className="blog-image-container">
                <img
                  src={TortaRosa}
                  alt="Torta Rosa"
                  className="blog-image-rosa"
                />
              </div>
              <div className="blog-content">
                <h3>Torta Especiales</h3>
                <p>
                  Torta de vainilla con relleno de crema de fresa y decorada con
                  rosas. Ideal para cualquier celebración.
                </p>
              </div>
            </div>
          </div>
          <div className="col-md-6">
            <div className="card-blog">
              <div className="blog-image-container">
                <img
                  src={TortaSherk}
                  alt="Torta Sherk"
                  className="blog-image-sherk"
                />
              </div>
              <div className="blog-content">
                <h3>Tortas Tematicas</h3>
                <p>
                  Torta de chocolate con relleno de crema de avellanas y
                  decorada con el personaje Sherk. Perfecta para los más
                  pequeños.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <TestimonialCarousel />

      {/* Botón de Recomendación para usuarios logueados */}
      {user && (
        <section className="container recommendation-section mb-5">
          <div className="row justify-content-center">
            <div className="col-md-8">
              <div className="recommendation-card">
                <div className="recommendation-content">
                  <h3 className="recommendation-title">
                    💝 ¿Qué opinas de nuestros productos?
                  </h3>
                  <p className="recommendation-subtitle">
                    Tu experiencia nos importa. Comparte tu opinión y ayúdanos a
                    seguir mejorando.
                  </p>
                  <button
                    className="btn-recommendation"
                    onClick={handleRecomendacionClick}>
                    ✍️ Comparte tu opinión
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>
      )}
    </div>
  );
};

export default Home;
