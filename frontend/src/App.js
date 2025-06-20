import AppRouter from "./routes/AppRouter";
import { CategoriaProvider } from "./context/CategoriaContext";
import "bootstrap/dist/css/bootstrap.min.css";
import "./styles/Fonts.css";
import "./styles/Layout.css";
import "./App.css";

function App() {
  return (
    <CategoriaProvider>
      <AppRouter />
    </CategoriaProvider>
  );
}

export default App;
