import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

const tabs = [
  { id: 'general', label: 'General' },
  { id: 'change-password', label: 'Change password' },
  { id: 'info', label: 'Info' },
  { id: 'social-links', label: 'Social links' },
  { id: 'connections', label: 'Connections' },
  { id: 'notifications', label: 'Notifications' },
];

function AccountSettings() {
  const [activeTab, setActiveTab] = useState('general');

  const renderTabContent = () => {
    switch (activeTab) {
      case 'general':
        return (
          <div>
            <h5>General</h5>
            <p>Contenido del perfil general.</p>
          </div>
        );
      case 'change-password':
        return (
          <div>
            <h5>Change Password</h5>
            <p>Formulario para cambiar contraseña.</p>
          </div>
        );
      case 'info':
        return (
          <div>
            <h5>Info</h5>
            <p>Información adicional del perfil.</p>
          </div>
        );
      case 'social-links':
        return (
          <div>
            <h5>Social Links</h5>
            <p>Conecta tus redes sociales.</p>
          </div>
        );
      case 'connections':
        return (
          <div>
            <h5>Connections</h5>
            <p>Conexiones externas.</p>
          </div>
        );
      case 'notifications':
        return (
          <div>
            <h5>Notifications</h5>
            <p>Preferencias de notificación.</p>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="container py-4">
      <h4 className="font-weight-bold mb-4">Account Settings</h4>
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
