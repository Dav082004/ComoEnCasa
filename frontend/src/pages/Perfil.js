import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/Perfil.css';

const Perfil = () => {
  const [perfil, setPerfil] = useState({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    direccion: '',
    nuevaContrasena: ''
  });

  const userId = localStorage.getItem('userId');

useEffect(() => {
  if (userId) {
    axios.get(`http://localhost:8081/api/auth/perfil/${userId}`)
      .then(res => {
        const data = res.data;
        setPerfil(prev => ({
          ...prev,
          nombre: data.nombre || '',
          apellido: data.apellido || '',
          email: data.email || '',
          telefono: data.telefono || '',
          direccion: data.direccion || ''
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
      nombre: perfil.nombre,
      apellido: perfil.apellido,
      email: perfil.email,
      telefono: perfil.telefono,
      direccion: perfil.direccion,
      nuevaContrasena: perfil.nuevaContrasena
    };

    axios.put(`http://localhost:8081/api/auth/perfil/${userId}`, datosActualizados)
      .then(() => {
        alert('Perfil actualizado correctamente');
        setPerfil(prev => ({ ...prev, nuevaContrasena: '' }));
      })
      .catch(err => {
        console.error('Error al actualizar perfil:', err);
        alert('Hubo un error al actualizar el perfil');
      });
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
                name="nombre"
                value={perfil.nombre}
                onChange={handleChange}
              />
            </div>
            <div className="perfil-formulario-group">
              <label>Apellido</label>
              <input
                type="text"
                name="apellido"
                value={perfil.apellido}
                onChange={handleChange}
              />
            </div>
            <div className="perfil-formulario-group">
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={perfil.email}
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
                name="nuevaContrasena"
                value={perfil.nuevaContrasena}
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
