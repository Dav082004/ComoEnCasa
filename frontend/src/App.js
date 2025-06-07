import AppRouter from "./routes/AppRouter";
import { CartProvider } from "./context/CartContext";
import "bootstrap/dist/css/bootstrap.min.css";
import "./styles/Fonts.css";
import "./styles/Layout.css";
import "./App.css";
import Perfil from './pages/Perfil';

function App() {
  return <AppRouter />;
}

export default App;
