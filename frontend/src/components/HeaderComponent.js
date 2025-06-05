// src/components/HeaderComponent.js
import { Link, NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { Dropdown } from "react-bootstrap";
import { PersonCircle } from "react-bootstrap-icons";
import "../styles/Header.css";
import "../styles/CartBadge.css";
import { CartFill } from "react-bootstrap-icons";

export const HeaderComponent = () => {
  const { user, logout } = useAuth();
  const { cart } = useCart();
  const totalItems = Object.values(cart).reduce(
    (sum, item) => sum + item.cantidad,
    0
  );

  const getNombreUsuario = () => {
    if (!user) return "Usuario";

    // Si tenemos nombre completo, devolvemos el primer nombre
    if (user.nombreCompleto && user.nombreCompleto.trim() !== "") {
      return user.nombreCompleto.trim().split(" ")[0];
    }

    return user.email?.split("@")[0] || "Usuario";
  };

  // Función para mostrar el rol de forma legible
  const getRolUsuario = () => {
    if (!user?.rol) return "";

    switch (user.rol) {
      case "ADMIN":
        return "Administrador";
      case "CLIENTE":
        return "Cliente";
      default:
        return user.rol;
    }
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
            {user ? (
              <Dropdown align="end">
                <Dropdown.Toggle variant="link" id="dropdown-user">
                  <PersonCircle size={24} className="user-icon" />
                </Dropdown.Toggle>

                <Dropdown.Menu>
                  <Dropdown.Header>
                    <div>
                      Hola, <strong>{getNombreUsuario()}</strong>
                    </div>
                    <small className="text-muted">{getRolUsuario()}</small>
                  </Dropdown.Header>
                  <Dropdown.Item as={Link} to="/perfil">
                    Perfil
                  </Dropdown.Item>
                  <Dropdown.Item as={Link} to="/pedidos">
                    Mis Pedidos
                  </Dropdown.Item>
                  {user.isAdmin && (
                    <Dropdown.Item as={Link} to="/admin">
                      Panel Admin
                    </Dropdown.Item>
                  )}
                  <Dropdown.Divider />
                  <Dropdown.Item onClick={logout}>Cerrar sesión</Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            ) : (
              <Link to="/login" className="login-button">
                Iniciar Sesión
              </Link>
            )}
<Link to="/carrito" className="cart-icon-wrapper">
  <CartFill size={28} color="#6c757d" />
  {totalItems > 0 && (
    <span className="cart-badge">{totalItems}</span>
  )}
</Link>




          </div>
        </nav>
      </div>
    </header>
  );
};
