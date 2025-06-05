import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

const tabs = [
  { id: 'General', label: 'General' },
  { id: 'Contraseña', label: 'Contraseña' },
  { id: 'Direccion', label: 'Dirección' },
  { id: 'Historial', label: 'Historial' },
];

function AccountSettings() {
  const [activeTab, setActiveTab] = useState('General');
  const [perfil, setPerfil] = useState({
    nombreCompleto: '',
    email: '',
    telefono: '',
    direccion: '',
    dni: '',
    nuevaContraseña: '',
  });

  const usuarioId = localStorage.getItem('usuarioId'); // Asegúrate de guardarlo al hacer login

  // Obtener perfil al cargar
  useEffect(() => {
    fetch(`http://localhost:3000/api/auth/perfil/${usuarioId}`)
      .then((res) => res.json())
      .then((data) => setPerfil((prev) => ({ ...prev, ...data })))
      .catch((err) => console.error('Error al obtener perfil:', err));
  }, [usuarioId]);

  // Manejar cambios en los campos
  const handleChange = (e) => {
    const { name, value } = e.target;
    setPerfil((prev) => ({ ...prev, [name]: value }));
  };

  // Guardar cambios
  const handleGuardar = () => {
    fetch(`http://localhost:3000/api/auth/perfil/${usuarioId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(perfil),
    })
      .then((res) => res.ok ? alert('Perfil actualizado') : alert('Error al guardar'))
      .catch((err) => console.error(err));
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'General':
        return (
          <div>
            <h5>Datos Generales</h5>
            <div className="form-group">
              <label>Nombre</label>
              <input type="text" className="form-control" name="nombreCompleto" value={perfil.nombreCompleto} onChange={handleChange} />
              <label>Email</label>
              <input type="email" className="form-control" name="email" value={perfil.email} onChange={handleChange} />
              <label>DNI</label>
              <input type="text" className="form-control" name="dni" value={perfil.dni} onChange={handleChange} />
            </div>
            <button className="btn btn-primary mt-2" onClick={handleGuardar}>Guardar</button>
          </div>
        );
      case 'Contraseña':
        return (
          <div>
            <h5>Cambiar Contraseña</h5>
            <div className="form-group">
              <label>Nueva Contraseña</label>
              <input type="password" className="form-control" name="nuevaContraseña" value={perfil.nuevaContraseña} onChange={handleChange} />
            </div>
            <button className="btn btn-primary mt-2" onClick={handleGuardar}>Guardar</button>
          </div>
        );
      case 'Direccion':
        return (
          <div>
            <h5>Dirección y Teléfono</h5>
            <div className="form-group">
              <label>Dirección</label>
              <input type="text" className="form-control" name="direccion" value={perfil.direccion} onChange={handleChange} />
              <label>Teléfono</label>
              <input type="text" className="form-control" name="telefono" value={perfil.telefono} onChange={handleChange} />
            </div>
            <button className="btn btn-primary mt-2" onClick={handleGuardar}>Guardar</button>
          </div>
        );
      case 'Historial':
        return (
          <div>
            <h5>Historial</h5>
            <p>Aquí estarán tus compras anteriores. (Aún no implementado)</p>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="container py-4">
      <h4 className="font-weight-bold mb-4">Configuración de la Cuenta</h4>
      <div className="row">
        <div className="col-md-3">
          <div className="list-group">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                className={`list-group-item list-group-item-action ${activeTab === tab.id ? 'active' : ''}`}
                onClick={() => setActiveTab(tab.id)}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>
        <div className="col-md-9">
          <div className="card p-3 shadow-sm">{renderTabContent()}</div>
        </div>
      </div>
    </div>
  );
}

export default AccountSettings;
