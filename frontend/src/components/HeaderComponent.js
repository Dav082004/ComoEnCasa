import { useState } from "react";
import { Link, NavLink } from "react-router-dom";
import { useCart } from "../context/CartContext";
import "../styles/Header.css";

export const HeaderComponent = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const { cart } = useCart();
  const totalItems = Object.values(cart).reduce(
    (sum, item) => sum + item.cantidad,
    0
  );

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
              <NavLink to="/productos">Productos</NavLink>
            </li>
            <li>
              <NavLink to="/nosotros">Nosotros</NavLink>
            </li>
          </ul>

          <div className="nav-actions">
            <Link to="/login" className="login-button" onClick={handleLogin}>
              {isLoggedIn ? "Cerrar Sesión" : "Iniciar Sesión"}
            </Link>
            <Link to="/carrito" className="cart-icon">
              🛒
              {totalItems > 0 && (
                <span className="cart-count">{totalItems}</span>
              )}
            </Link>
          </div>
        </nav>
      </div>
    </header>
  );
};
