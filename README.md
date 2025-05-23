# ComoEnCasa

# Arquitectura General

Tu aplicación sigue el patrón MVC (Modelo-Vista-Controlador):

**Frontend (React):** Se encarga de la Vista (UI) y parte del Controlador (gestión de estado)

**Backend (Spring Boot):** Maneja el Modelo (datos), lógica de negocio y parte del Controlador (API REST)

# Comunicación Frontend-Backend

**Axios:** Librería que usas en React para hacer llamadas HTTP a tu API REST de Spring Boot  
_Ejemplo:_ Cuando un usuario hace login, el frontend envía credenciales al endpoint `/api/auth/login` del backend

**React Router DOM:** Maneja la navegación entre componentes/páginas en el frontend sin recargar la página

**API REST:** Los endpoints que expone tu backend Spring Boot son consumidos por el frontend:

- Autenticación (`/api/auth/**`)
- Productos (`/api/products/**`)
- Carrito (`/api/cart/**`)
- Pedidos (`/api/orders/**`)

# Librerías y su uso

## Backend (Spring Boot)

- **Spring Security:** Para autenticación y autorización (JWT)
- **Lombok:** Para reducir código boilerplate (getters, setters, constructores)
- **Spring Data JPA:** Para interactuar con MySQL
- **Logback:** Para logging de la aplicación
- **Apache Commons:** Utilidades generales (ej: validación de campos)
- **Google Guava:** Utilidades para colecciones, caching, etc.

## Frontend (React)

- **Bootstrap:** Para estilos y componentes UI responsivos
- **React Context API:** Para manejo de estado global (carrito, autenticación)
- **Axios:** Para llamadas HTTP al backend

# Archivos Faltantes

## Backend

**Configuración JWT:**

- `JwtTokenProvider.java` - Para generar/validar tokens JWT
- `JwtAuthenticationFilter.java` - Filtro para validar tokens en requests

**Servicios:**

- `ProductService.java` - Lógica de negocio para productos
- `OrderService.java` - Para manejar pedidos
- `ReportService.java` - Para generar reportes (Apache POI para Excel)

**DTOs adicionales:**

- `ProductDTO.java` - Para transferencia de datos de productos
- `OrderDTO.java` - Para información de pedidos

**Controladores adicionales:**

- `ProductController.java` - Endpoints para CRUD de productos
- `OrderController.java` - Para manejar pedidos
- `ReportController.java` - Para generar reportes

**Excepciones:**

- `GlobalExceptionHandler.java` - Manejo centralizado de excepciones

## Frontend

**Componentes faltantes:**

- `AdminPanel.js` - Vista de administrador
- `OrderHistory.js` - Historial de pedidos
- `ProductForm.js` - Formulario para añadir/editar productos (admin)
- `Reports.js` - Visualización de reportes

**Servicios:**

- `api.js` - Configuración base de Axios (headers, interceptores)
- `productService.js` - Funciones para llamadas API de productos
- `orderService.js` - Funciones para pedidos

**Hooks personalizados:**

- `useAuth.js` - Para manejar lógica de autenticación
- `useCart.js` - Para manejar el carrito

# Flujo de Funcionalidades Clave

**Login:**

1. Frontend: Formulario en `Login.js` → Axios POST a `/api/auth/login`
2. Backend: `AuthController` valida credenciales → genera JWT → devuelve token

**Gestión de Productos:**

1. Frontend: `Products.js` hace GET a `/api/products` → muestra datos
2. Admin: `ProductForm.js` hace POST/PUT/DELETE a `/api/products`

**Carrito de Compras:**

1. Frontend: `CartContext.js` maneja estado local del carrito
2. Checkout: `Checkout.js` envía pedido a `/api/orders`

**Reportes:**

1. Backend: `ReportController` usa Apache POI para generar Excel
2. Frontend: `Reports.js` descarga el archivo generado

# Seguridad

- Spring Security en backend protege endpoints según roles (USER, ADMIN)
- JWT se almacena en frontend (localStorage o cookies) y se envía en cada request
- Validación tanto en frontend (mejor UX) como backend (seguridad)

## Estructura Completa del Proyecto Pastelería

### Backend (Spring Boot)
```plaintext
comoencasa-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── comoencasa_backend/
│   │   │           ├── config/
│   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │           │   ├── JwtTokenProvider.java
│   │   │           │   ├── SwaggerConfig.java
│   │   │           │   └── WebMvcConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AdminController.java
│   │   │           │   ├── CartController.java
│   │   │           │   ├── OrderController.java
│   │   │           │   ├── ProductController.java
│   │   │           │   └── ReportController.java
│   │   │           ├── dto/
│   │   │           │   ├── CartItemDTO.java
│   │   │           │   ├── OrderDTO.java
│   │   │           │   ├── ProductDTO.java
│   │   │           │   ├── ReportDTO.java
│   │   │           │   └── UserDTO.java
│   │   │           ├── exception/
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── ResourceNotFoundException.java
│   │   │           │   └── UnauthorizedException.java
│   │   │           ├── model/
│   │   │           │   ├── CartItem.java
│   │   │           │   ├── Order.java
│   │   │           │   ├── OrderDetail.java
│   │   │           │   ├── Product.java
│   │   │           │   └── Rol.java
│   │   │           ├── repository/
│   │   │           │   ├── CartItemRepository.java
│   │   │           │   ├── OrderRepository.java
│   │   │           │   └── ProductRepository.java
│   │   │           ├── service/
│   │   │           │   ├── CartService.java
│   │   │           │   ├── OrderService.java
│   │   │           │   ├── ProductService.java
│   │   │           │   ├── ReportService.java
│   │   │           │   └── UserService.java
│   │   │           ├── util/
│   │   │           │   ├── Constants.java
│   │   │           │   └── ExcelGenerator.java
│   │   │           └── ComoencasaBackendApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
└── pom.xml

## Frontend (React)

comoticasa/
├── public/
├── src/
│ ├── admin/
│ │ ├── AdminDashboard.js
│ │ ├── AdminProducts.js
│ │ ├── AdminOrders.js
│ │ ├── AdminReports.js
│ │ └── AdminUsers.js
│ ├── components/
│ │ ├── common/
│ │ │ ├── LoadingSpinner.js
│ │ │ ├── Modal.js
│ │ │ └── Notification.js
│ │ ├── product/
│ │ │ ├── ProductCard.js
│ │ │ ├── ProductDetail.js
│ │ │ └── ProductForm.js
│ │ └── checkout/
│ │ ├── CheckoutForm.js
│ │ ├── OrderSummary.js
│ │ └── PaymentMethods.js
│ ├── hooks/
│ │ ├── useAuth.js
│ │ ├── useCart.js
│ │ ├── useProducts.js
│ │ └── useOrders.js
│ ├── services/
│ │ ├── api.js
│ │ ├── authService.js
│ │ ├── cartService.js
│ │ ├── orderService.js
│ │ ├── productService.js
│ │ └── reportService.js
│ ├── utils/
│ │ ├── auth.js
│ │ ├── formatters.js
│ │ └── validators.js
│ └── ... (existing files)
└── package.json

### FRONTEND (React)
comoencasa/
├── public/
├── src/
│   ├── admin/
│   │   ├── AdminDashboard.js
│   │   ├── AdminProducts.js
│   │   ├── AdminOrders.js
│   │   ├── AdminReports.js
│   │   └── AdminUsers.js
│   ├── components/
│   │   ├── common/
│   │   │   ├── LoadingSpinner.js
│   │   │   ├── Modal.js
│   │   │   └── Notification.js
│   │   ├── product/
│   │   │   ├── ProductCard.js
│   │   │   ├── ProductDetail.js
│   │   │   └── ProductForm.js
│   │   └── checkout/
│   │       ├── CheckoutForm.js
│   │       ├── OrderSummary.js
│   │       └── PaymentMethods.js
│   ├── hooks/
│   │   ├── useAuth.js
│   │   ├── useCart.js
│   │   ├── useProducts.js
│   │   └── useOrders.js
│   ├── services/
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── cartService.js
│   │   ├── orderService.js
│   │   ├── productService.js
│   │   └── reportService.js
│   ├── utils/
│   │   ├── auth.js
│   │   ├── formatters.js
│   │   └── validators.js
│   └── ... (existing files)
└── package.json

