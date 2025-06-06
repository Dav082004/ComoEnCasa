import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/Perfil.css';

const Perfil = () => {
  const [perfil, setPerfil] = useState({
    nombreCompleto: '',
    Email: '',
    telefono: '',
    direccion: '',
    nuevaContraseña: ''
  });

  const userId = localStorage.getItem('userId'); // Asegúrate de guardar esto en login

  useEffect(() => {
    if (userId) {
      axios.get(`http://localhost:8081/api/auth/perfil/${userId}`)
        .then(res => {
          setPerfil(prev => ({
            ...prev,
            nombreCompleto: res.data.nombreCompleto || '',
            Email: res.data.Email || '',
            telefono: res.data.telefono || '',
            direccion: res.data.direccion || ''
          }));
        })
        .catch(err => console.error('Error al obtener perfil:', err));
    }
  }, [userId]);

  const handleChange = e => {
    const { name, value } = e.target;
    setPerfil(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = e => {
    e.preventDefault();
    const datosActualizados = {
      nombreCompleto: perfil.nombreCompleto,
      Email: perfil.Email,
      telefono: perfil.telefono,
      direccion: perfil.direccion,
      nuevaContraseña: perfil.nuevaContraseña
    };

    axios.put(`http://localhost:8081/api/auth/perfil/${userId}`, datosActualizados)
      .then(() => {
        alert('Perfil actualizado correctamente');
        setPerfil(prev => ({ ...prev, nuevaContraseña: '' }));
      })
      .catch(err => console.error('Error al actualizar perfil:', err));
  };

  return (
    <div className="perfil-container">
      <div className="perfil-wrapper">
        <div className="perfil-menu">
          <h4 style={{ color: '#ff8fab', fontFamily: 'Comic Sans MS' }}>Configuración</h4>
          <button className="btn btn-primary w-100 mt-2">General</button>
        </div>

        <div className="perfil-form-section">
          <h2 className="perfil-titulo">Datos Generales</h2>
          <form className="perfil-formulario" onSubmit={handleSubmit}>
            <div className="perfil-formulario-group">
              <label>Nombre</label>
              <input
                type="text"
                name="nombreCompleto"
                value={perfil.nombreCompleto}
                onChange={handleChange}
              />
            </div>
            <div className="perfil-formulario-group">
              <label>Email</label>
              <input
                type="Email"
                name="Email"
                value={perfil.Email}
                onChange={handleChange}
              />
            </div>
            <div className="perfil-formulario-group">
              <label>Teléfono</label>
              <input
                type="text"
                name="telefono"
                value={perfil.telefono}
                onChange={handleChange}
              />
            </div>
            <div className="perfil-formulario-group">
              <label>Dirección</label>
              <input
                type="text"
                name="direccion"
                value={perfil.direccion}
                onChange={handleChange}
              />
            </div>

            <div className="perfil-formulario-group" style={{ gridColumn: '1 / -1' }}>
              <label>Nueva Contraseña</label>
              <input
                type="password"
                name="nuevaContraseña"
                value={perfil.nuevaContraseña}
                onChange={handleChange}
              />
            </div>

            <button type="submit" className="perfil-boton">Guardar</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Perfil;
