import { createContext, useState, useContext, useEffect } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const login = (userData) => {
    const userWithRole = {
      id: userData.id,
      email: userData.email || "",
      nombreCompleto: userData.nombreCompleto || "Usuario", // Asegura que siempre tenga valor
      rol: userData.rol || "CLIENTE",
      isAdmin: userData.rol === "ADMIN",
    };
    localStorage.setItem("user", JSON.stringify(userWithRole));
    setUser(userWithRole);
  };

  const logout = () => {
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
