import React from "react";
import "../styles/Footer.css";

export const FooterComponent = () => {
  return (
    <footer className="footer-pastel">
      <div className="footer-grid">
        <div className="footer-brand">
          <h3 className="footer-title">Como En Casa</h3>
          <p className="footer-slogan">Endulzando tus momentos especiales</p>
          <div className="social-icons">
            {["📱", "📷", "💌"].map((icon, index) => (
              <span key={index} className="social-icon">
                {icon}
              </span>
            ))}
          </div>
        </div>

        <div className="footer-section">
          <h4 className="footer-heading">Horario</h4>
          <ul className="footer-links">
            <li>Lunes-Viernes: 9am - 8pm</li>
            <li>Sábados: 10am - 9pm</li>
            <li>Domingos: 11am - 6pm</li>
          </ul>
        </div>

        <div className="footer-section">
          <h4 className="footer-heading">Contacto</h4>
          <ul className="footer-links">
            <li>📞 123456789</li>
            <li>✉️ comoencasa@gmail.com</li>
            <li>📍 Street</li>
          </ul>
        </div>
      </div>

      <div className="footer-bottom">
        <p>
          © {new Date().getFullYear()} ComoEnCasa. Todos los derechos
          reservados.
        </p>
      </div>
    </footer>
  );
};
