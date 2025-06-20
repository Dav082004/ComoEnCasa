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
            <div className="card-blog">
              <div className="blog-image-container">
                <img src={TortaRosa} alt="Torta Rosa" className="blog-image" />
              </div>
              <div className="blog-content">
                <h3>Torta Rosa</h3>
                <span className="blog-date">02 Mayo 2025</span>
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
                  className="blog-image"
                />
              </div>
              <div className="blog-content">
                <h3>Torta Sherk</h3>
                <span className="blog-date">25 Abril 2025</span>
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
