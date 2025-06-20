import React, { useEffect, useState } from "react";
import axios from "axios";
import "../styles/Perfil.css";

const Perfil = () => {
  const [activeTab, setActiveTab] = useState("personal");
  const [perfil, setPerfil] = useState({
    nombre: "",
    apellido: "",
    email: "",
    telefono: "",
    direccion: "",
  });

  const [cambioContrasena, setCambioContrasena] = useState({
    contrasenaActual: "",
    nuevaContrasena: "",
    confirmarContrasena: "",
  });

  const userId = localStorage.getItem("userId");

  useEffect(() => {
    if (userId) {
      axios
        .get(`http://localhost:8081/api/auth/perfil/${userId}`)
        .then((res) => {
          const data = res.data;
          setPerfil((prev) => ({
            ...prev,
            nombre: data.nombre || "",
            apellido: data.apellido || "",
            email: data.email || "",
            telefono: data.telefono || "",
            direccion: data.direccion || "",
          }));
        })
        .catch((err) => console.error("Error al obtener perfil:", err));
    }
  }, [userId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPerfil((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setCambioContrasena((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Solo enviar los campos que se pueden editar
    const datosActualizados = {
      email: perfil.email,
      telefono: perfil.telefono,
      direccion: perfil.direccion,
    };

    axios
      .put(`http://localhost:8081/api/auth/perfil/${userId}`, datosActualizados)
      .then(() => {
        alert("Perfil actualizado correctamente");
      })
      .catch((err) => {
        console.error("Error al actualizar perfil:", err);
        const errorMessage =
          err.response?.data || "Hubo un error al actualizar el perfil";
        alert(errorMessage);
      });
  };

  const handlePasswordSubmit = (e) => {
    e.preventDefault();

    if (
      cambioContrasena.nuevaContrasena !== cambioContrasena.confirmarContrasena
    ) {
      alert("Las contraseñas no coinciden");
      return;
    }

    if (cambioContrasena.nuevaContrasena.length < 6) {
      alert("La nueva contraseña debe tener al menos 6 caracteres");
      return;
    }

    const datosContrasena = {
      contrasenaActual: cambioContrasena.contrasenaActual,
      nuevaContrasena: cambioContrasena.nuevaContrasena,
    };

    axios
      .put(
        `http://localhost:8081/api/auth/perfil/${userId}/cambiar-contrasena`,
        datosContrasena
      )
      .then(() => {
        alert("Contraseña cambiada correctamente");
        setCambioContrasena({
          contrasenaActual: "",
          nuevaContrasena: "",
          confirmarContrasena: "",
        });
      })
      .catch((err) => {
        console.error("Error al cambiar contraseña:", err);
        const errorMessage =
          err.response?.data || "Hubo un error al cambiar la contraseña";
        alert(errorMessage);
      });
  };

  return (
    <div className="perfil-container">
      <div className="perfil-wrapper">
        <div className="perfil-menu">
          <h4 style={{ color: "#ff8fab", fontFamily: "Comic Sans MS" }}>
            Configuración
          </h4>
          <button
            className={`btn w-100 mt-2 ${
              activeTab === "personal" ? "btn-primary" : "btn-outline-primary"
            }`}
            onClick={() => setActiveTab("personal")}>
            Información Personal
          </button>
          <button
            className={`btn w-100 mt-2 ${
              activeTab === "password" ? "btn-primary" : "btn-outline-primary"
            }`}
            onClick={() => setActiveTab("password")}>
            Cambiar Contraseña
          </button>
        </div>

        <div className="perfil-form-section">
          {activeTab === "personal" && (
            <>
              <h2 className="perfil-titulo">Información Personal</h2>
              <form className="perfil-formulario" onSubmit={handleSubmit}>
                <div className="perfil-formulario-group">
                  <label>Nombre</label>
                  <input
                    type="text"
                    name="nombre"
                    value={perfil.nombre}
                    disabled
                    className="readonly-field"
                  />
                  <small className="text-muted">
                    Este campo no se puede modificar
                  </small>
                </div>
                <div className="perfil-formulario-group">
                  <label>Apellido</label>
                  <input
                    type="text"
                    name="apellido"
                    value={perfil.apellido}
                    disabled
                    className="readonly-field"
                  />
                  <small className="text-muted">
                    Este campo no se puede modificar
                  </small>
                </div>
                <div className="perfil-formulario-group">
                  <label>Email</label>
                  <input
                    type="email"
                    name="email"
                    value={perfil.email}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="perfil-formulario-group">
                  <label>Teléfono</label>
                  <input
                    type="text"
                    name="telefono"
                    value={perfil.telefono}
                    onChange={handleChange}
                    placeholder="Ingresa tu número de teléfono"
                  />
                </div>
                <div
                  className="perfil-formulario-group"
                  style={{ gridColumn: "1 / -1" }}>
                  <label>Dirección</label>
                  <input
                    type="text"
                    name="direccion"
                    value={perfil.direccion}
                    onChange={handleChange}
                    placeholder="Ingresa tu dirección completa"
                  />
                </div>

                <button type="submit" className="perfil-boton">
                  Guardar Cambios
                </button>
              </form>
            </>
          )}

          {activeTab === "password" && (
            <>
              <h2 className="perfil-titulo">Cambiar Contraseña</h2>
              <form
                className="perfil-formulario"
                onSubmit={handlePasswordSubmit}>
                <div
                  className="perfil-formulario-group"
                  style={{ gridColumn: "1 / -1" }}>
                  <label>Contraseña Actual</label>
                  <input
                    type="password"
                    name="contrasenaActual"
                    value={cambioContrasena.contrasenaActual}
                    onChange={handlePasswordChange}
                    required
                  />
                </div>
                <div
                  className="perfil-formulario-group"
                  style={{ gridColumn: "1 / -1" }}>
                  <label>Nueva Contraseña</label>
                  <input
                    type="password"
                    name="nuevaContrasena"
                    value={cambioContrasena.nuevaContrasena}
                    onChange={handlePasswordChange}
                    minLength="6"
                    required
                  />
                  <small className="text-muted">Mínimo 6 caracteres</small>
                </div>
                <div
                  className="perfil-formulario-group"
                  style={{ gridColumn: "1 / -1" }}>
                  <label>Confirmar Nueva Contraseña</label>
                  <input
                    type="password"
                    name="confirmarContrasena"
                    value={cambioContrasena.confirmarContrasena}
                    onChange={handlePasswordChange}
                    required
                  />
                </div>

                <button type="submit" className="perfil-boton">
                  Cambiar Contraseña
                </button>
              </form>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Perfil;
