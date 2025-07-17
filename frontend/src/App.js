import { PayPalScriptProvider } from "@paypal/react-paypal-js";
import AppRouter from "./routes/AppRouter";
import { CategoriaProvider } from "./context/CategoriaContext";
import "bootstrap/dist/css/bootstrap.min.css";
import "./styles/Fonts.css";
import "./styles/Layout.css";
import "./App.css";

function App() {
  return (
    <PayPalScriptProvider
      options={{
        "client-id": "Ad9hQoI_7QEPjeKHvmJpOwNbM3l7-svfCZKpU2BBaPuY9FngdUnpBcRoGx5izWeNdFpGrhQ-PPmmmXF9",
        currency: "PEN", 
        locale: "es_PE",
        intent: "capture",
        components: "buttons,funding-eligibility"
      }}
    >
      <CategoriaProvider>
        <AppRouter />
      </CategoriaProvider>
    </PayPalScriptProvider>
  );
}

export default App;
