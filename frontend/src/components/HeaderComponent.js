import React, { useState } from "react";
import { Link, NavLink } from "react-router-dom";
import "../styles/Header.css";

export const HeaderComponent = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const handleLogin = () => {
    setIsLoggedIn(!isLoggedIn);
  };

  return (
    <header className="header-pastel">
      <div className="container">
        <nav className="nav-main">
          <Link to="/" className="brand">
            <span className="logo">🍰</span>
            <span className="brand-name">Como En Casa</span>
          </Link>

          <ul className="nav-links">
            <li>
              <NavLink to="/" end>
                Inicio
              </NavLink>
            </li>
            <li>
              <NavLink to="/pasteles">Pasteles</NavLink>
            </li>
            <li>
              <NavLink to="/postres">Postres</NavLink>
            </li>
            <li>
              <NavLink to="/eventos">Eventos</NavLink>
            </li>
            <li>
              <NavLink to="/nosotros">Nosotros</NavLink>
            </li>
          </ul>

          <div className="nav-actions">
            <button onClick={handleLogin} className="login-button">
              {isLoggedIn ? "👤" : "Iniciar Sesión"}
            </button>
            <Link to="/carrito" className="cart-icon">
              🛒
            </Link>
          </div>
        </nav>
      </div>
    </header>
  );
};
