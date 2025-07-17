CREATE TABLE `categoria_producto` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `comprobante` (
  `id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `tipo` enum('Factura','Boleta') NOT NULL,
  `fecha_emision` datetime DEFAULT current_timestamp(),
  `numero_serie` varchar(4) NOT NULL,
  `numero_comprobante` varchar(8) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `total` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `detalle_pedido` (
  `id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `producto_id` bigint(20) NOT NULL,
  `cantidad` int(11) DEFAULT 1,
  `precio_unitario` decimal(10,2) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL,
  `personalizacion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `pago` (
  `id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `fecha` datetime DEFAULT current_timestamp(),
  `metodo` enum('Yape','Plin','Tarjeta','Efectivo') NOT NULL,
  `estado` enum('Pagado','Pendiente','Rechazado') DEFAULT 'Pendiente',
  `monto` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `pedido` (
  `id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `fecha_entrega` datetime DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `subtotal` decimal(38,2) DEFAULT NULL,
  `costo_total` decimal(38,2) DEFAULT NULL,
  `direccion_entrega` varchar(255) DEFAULT NULL,
  `necesita_factura` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `producto` (
  `id` bigint(20) NOT NULL,
  `categoria_id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio_venta` double NOT NULL,
  `costo_produccion` double NOT NULL,
  `disponible` tinyint(1) DEFAULT 1,
  `imagen_url` varchar(255) DEFAULT NULL,
  `cantidad` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `fecha_registro` datetime DEFAULT current_timestamp(),
  `tipo_documento` enum('DNI','RUC','CE') DEFAULT 'DNI',
  `numero_documento` varchar(20) DEFAULT NULL,
  `rol` varchar(255) NOT NULL,
  `activado` tinyint(1) DEFAULT 0,
  `recomendacion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Índices
ALTER TABLE `categoria_producto` ADD PRIMARY KEY (`id`);
ALTER TABLE `comprobante` ADD PRIMARY KEY (`id`), ADD KEY `pedido_id` (`pedido_id`);
ALTER TABLE `detalle_pedido` ADD PRIMARY KEY (`id`), ADD KEY `pedido_id` (`pedido_id`), ADD KEY `producto_id` (`producto_id`);
ALTER TABLE `pago` ADD PRIMARY KEY (`id`), ADD KEY `pedido_id` (`pedido_id`);
ALTER TABLE `pedido` ADD PRIMARY KEY (`id`), ADD KEY `usuario_id` (`usuario_id`);
ALTER TABLE `producto` ADD PRIMARY KEY (`id`), ADD KEY `categoria_id` (`categoria_id`);
ALTER TABLE `usuario` ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `correo` (`correo`);

-- Auto-increment
ALTER TABLE `categoria_producto` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
ALTER TABLE `comprobante` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;
ALTER TABLE `detalle_pedido` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;
ALTER TABLE `pago` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;
ALTER TABLE `pedido` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
ALTER TABLE `producto` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
ALTER TABLE `usuario` MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

-- Claves foráneas
ALTER TABLE `comprobante` ADD CONSTRAINT `comprobante_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE;
ALTER TABLE `detalle_pedido`
  ADD CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`);
ALTER TABLE `pago` ADD CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE;
ALTER TABLE `pedido` ADD CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;
ALTER TABLE `producto` ADD CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_producto` (`id`);
