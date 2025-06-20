# Implementación del Perfil de Usuario

## Resumen

Se ha implementado una funcionalidad completa de perfil de usuario con dos secciones: Información Personal y Cambio de Contraseña.

## Backend - Cambios Realizados

### 1. PerfilUsuarioDTO.java

```java
@Data
public class PerfilUsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDateTime fechaRegistro;
}
```

### 2. ActualizarPerfilRequest.java

```java
@Data
public class ActualizarPerfilRequest {
    private String email;
    private String telefono;
    private String direccion;
    // Nota: nombre y apellido no se incluyen porque no son editables
}
```

### 3. CambiarContrasenaRequest.java

```java
@Data
public class CambiarContrasenaRequest {
    private String contrasenaActual;
    private String nuevaContrasena;
}
```

### 4. Endpoints en AuthController

#### GET /api/auth/perfil/{userId}

- **Propósito**: Obtener información del perfil del usuario
- **Respuesta**: PerfilUsuarioDTO con datos del usuario
- **Validaciones**: Verifica que el usuario exista

#### PUT /api/auth/perfil/{userId}

- **Propósito**: Actualizar información del perfil (solo campos editables)
- **Campos editables**: email, teléfono, dirección
- **Campos NO editables**: nombre, apellido
- **Validaciones**:
  - Formato de email válido
  - Email único (no usado por otro usuario)

#### PUT /api/auth/perfil/{userId}/cambiar-contrasena

- **Propósito**: Cambiar contraseña del usuario
- **Validaciones**:
  - Contraseña actual correcta
  - Nueva contraseña mínimo 6 caracteres
  - Confirmación de contraseña

## Frontend - Cambios Realizados

### 1. Componente Perfil.js

**Características implementadas:**

- **Navegación por tabs**: Información Personal y Cambiar Contraseña
- **Campos de solo lectura**: Nombre y apellido (disabled con estilos especiales)
- **Campos editables**: Email, teléfono y dirección
- **Validaciones**: Formularios con validación HTML5 y JavaScript
- **Manejo de errores**: Mensajes específicos del servidor

### 2. Estados del componente

```javascript
const [activeTab, setActiveTab] = useState("personal");
const [perfil, setPerfil] = useState({
  nombre: "",
  apellido: "",
  email: "",
  telefono: "",
  direccion: "",
});
const [cambioContrasena, setCambioContrasena] = useState({
  contrasenaActual: "",
  nuevaContrasena: "",
  confirmarContrasena: "",
});
```

### 3. Funcionalidades

#### Información Personal

- **Carga automática** de datos desde la base de datos
- **Campos no editables**: Nombre y apellido (estilo visual distintivo)
- **Campos editables**: Email, teléfono, dirección
- **Validación de email**: Formato requerido
- **Placeholders informativos** para teléfono y dirección

#### Cambio de Contraseña

- **Formulario separado** en su propio tab
- **Validación de contraseña actual**
- **Requisitos de nueva contraseña**: Mínimo 6 caracteres
- **Confirmación de contraseña**: Debe coincidir con la nueva
- **Limpieza automática** del formulario tras éxito

### 4. Estilos CSS (Perfil.css)

#### Nuevos estilos agregados:

```css
.perfil-formulario-group input.readonly-field {
  background-color: #f8f9fa;
  color: #6c757d;
  border-color: #e9ecef;
  cursor: not-allowed;
}

.perfil-menu button.btn-outline-primary {
  background-color: transparent;
  color: #ff8fab;
  border: 2px solid #ff8fab;
}

.perfil-formulario-group input:focus {
  outline: none;
  border-color: #ff8fab;
  box-shadow: 0 0 0 2px rgba(255, 143, 171, 0.2);
}
```

## Endpoints Utilizados

### Obtener Perfil

```
GET /api/auth/perfil/{userId}
```

**Respuesta:**

```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "telefono": "987654321",
  "direccion": "Av. Principal 123",
  "fechaRegistro": "2025-06-19T10:30:00"
}
```

### Actualizar Perfil

```
PUT /api/auth/perfil/{userId}
```

**Request Body:**

```json
{
  "email": "nuevo@example.com",
  "telefono": "987654321",
  "direccion": "Nueva dirección"
}
```

### Cambiar Contraseña

```
PUT /api/auth/perfil/{userId}/cambiar-contrasena
```

**Request Body:**

```json
{
  "contrasenaActual": "contraseña123",
  "nuevaContrasena": "nuevaContraseña456"
}
```

## Seguridad y Validaciones

### Backend

- **Validación de existencia de usuario**
- **Validación de formato de email**
- **Validación de unicidad de email**
- **Verificación de contraseña actual**
- **Encriptación de nueva contraseña con BCrypt**

### Frontend

- **Validación HTML5**: Campos requeridos, formato de email
- **Validación de coincidencia de contraseñas**
- **Validación de longitud mínima de contraseña**
- **Manejo de errores del servidor**

## UX/UI

### Mejoras de experiencia de usuario:

- **Navegación clara** entre secciones con tabs
- **Campos visualmente diferenciados** (editables vs no editables)
- **Placeholders informativos**
- **Mensajes de éxito y error claros**
- **Estilos consistentes** con el tema de la aplicación
- **Responsive design** para dispositivos móviles

## Estado de Implementación

✅ Backend: Completo y funcional
✅ Frontend: Completo con navegación por tabs
✅ Validaciones: Implementadas en ambos extremos
✅ Seguridad: Contraseñas encriptadas y validaciones
✅ UX: Interfaz intuitiva y responsive
✅ Restricciones: Nombre y apellido no editables
