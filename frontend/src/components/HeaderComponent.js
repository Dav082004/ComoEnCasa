// src/components/HeaderComponent.js
import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { Dropdown } from "react-bootstrap";
import { PersonCircle } from "react-bootstrap-icons";
import "../styles/Header.css";

export const HeaderComponent = () => {
  const { user, logout } = useAuth();
  const { cart } = useCart();
  const totalItems = Object.values(cart).reduce(
    (sum, item) => sum + item.cantidad,
    0
  );

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
            {user ? (
              <Dropdown align="end">
                <Dropdown.Toggle variant="link" id="dropdown-user">
                  <PersonCircle size={24} className="user-icon" />
                </Dropdown.Toggle>

                <Dropdown.Menu>
                  <Dropdown.Header>
                    Hola, {user.nombreCompleto.split(" ")[0]}
                  </Dropdown.Header>
                  <Dropdown.Item as={Link} to="/perfil">
                    Perfil
                  </Dropdown.Item>
                  <Dropdown.Item as={Link} to="/pedidos">
                    Mis Pedidos
                  </Dropdown.Item>
                  <Dropdown.Divider />
                  <Dropdown.Item onClick={logout}>Cerrar sesión</Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            ) : (
              <Link to="/login" className="login-button">
                Iniciar Sesión
              </Link>
            )}
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
