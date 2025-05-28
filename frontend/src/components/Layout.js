// src/components/Layout.js
import { Outlet } from 'react-router-dom';
import { HeaderComponent } from './HeaderComponent';
import { FooterComponent } from './FooterComponent';

const Layout = () => {
  return (
    <div className="app-container">
      <HeaderComponent />
      <main className="main-content">
        <Outlet />
      </main>
      <FooterComponent />
    </div>
  );
};

export default Layout;
