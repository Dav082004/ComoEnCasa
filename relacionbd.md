# 🔗 Relaciones clave en tu base de datos

### `usuario` ←→ `pedido`
- Un **usuario** puede tener varios **pedidos**.

### `pedido` ←→ `producto_pedido`
- Un **pedido** puede tener varios **productos**.

### `producto` ←→ `producto_pedido`
- Un **producto** puede estar en varios **pedidos**.

### `pedido` ←→ `comprobante`
- Un **pedido** puede generar un **comprobante** (boleta/factura).

### `pedido` ←→ `pago`
- Un **pedido** puede tener uno o más **pagos**.

### `producto` ←→ `categoria_producto`
- Un **producto** pertenece a una **categoría**.

---

# 📦 Funciones y cómo se relacionan con las tablas

## 1. `registrarPedido($usuarioId, $productos, $direccionEntrega, $notas, $necesitaFactura)`

**Tablas involucradas:**
- `pedido`: se crea un nuevo pedido.
- `producto_pedido`: se agregan los productos vinculados al pedido.

**Relaciones:**
- `pedido.usuario_id` ← `$usuarioId`
- `producto_pedido.pedido_id` ← ID generado de `pedido`
- `producto_pedido.producto_id` ← IDs de los productos del array `$productos`

---

## 2. `registrarPago($pedidoId, $metodo, $monto)`

**Tablas involucradas:**
- `pago`: se inserta un nuevo pago asociado a un pedido.

**Relaciones:**
- `pago.pedido_id` ← `$pedidoId`

---

## 3. `generarComprobante($pedidoId, $tipo)`

**Tablas involucradas:**
- `comprobante`: se crea un comprobante (boleta o factura) para un pedido.

**Relaciones:**
- `comprobante.pedido_id` ← `$pedidoId`

---

# 🔍 Ejemplo práctico con tus datos reales

Supón que el **usuario con ID 6 (Juana Pérez)** hace un pedido de la **Torta Red Velvet (producto ID 3)** y pide factura.

### Función `registrarPedido()`:

Se inserta una fila en `pedido`:
- `usuario_id = 6`
- `direccion_entrega = 'Calle Falsa 123'`
- `necesita_factura = 1`

Se inserta en `producto_pedido`:
- `pedido_id = (nuevo ID del pedido)`
- `producto_id = 3` (Red Velvet)
- `precio_unitario = 150.00`
- `costo_unitario = 95.00`

---

### Función `registrarPago()`:

Se inserta una fila en `pago`:
- `pedido_id = (ID del pedido)`
- `metodo = 'Plin'`
- `estado = 'Pagado'`
- `monto = 150.00`

---

### Función `generarComprobante()`:

Se inserta una fila en `comprobante`:
- `pedido_id = (ID del pedido)`
- `tipo = 'Factura'`
- `numero_serie = 'F001'`
- `numero_comprobante = '00005679'`

---

# ✅ Validación de integridad

Gracias a tus `FOREIGN KEY`:

- Si un **pedido** se elimina, su **comprobante** y **pagos** también se eliminan automáticamente (`ON DELETE CASCADE`).
- No se puede insertar un **pago** o **comprobante** si el `pedido_id` no existe.
