-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 05-06-2025 a las 22:56:01
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `comoencasa_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria_producto`
--

CREATE TABLE `categoria_producto` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `categoria_producto`
--

INSERT INTO `categoria_producto` (`id`, `nombre`, `descripcion`) VALUES
(1, 'Tortas', 'Deliciosas tortas personalizadas para toda ocasión'),
(2, 'Eventos', 'Productos especiales para eventos y celebraciones'),
(3, 'Postres', 'Variedad de postres y dulces para complementar tu comida');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comprobante`
--

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

--
-- Volcado de datos para la tabla `comprobante`
--

INSERT INTO `comprobante` (`id`, `pedido_id`, `tipo`, `fecha_emision`, `numero_serie`, `numero_comprobante`, `subtotal`, `total`) VALUES
(2, 1, 'Boleta', '2025-06-05 15:50:22', 'B001', '00001234', 120.00, 120.00),
(3, 2, 'Factura', '2025-06-05 15:50:22', 'F001', '00005678', 150.00, 150.00),
(4, 3, 'Boleta', '2025-06-04 13:00:00', 'B002', '00004567', 80.00, 80.00),
(5, 4, 'Factura', '2025-06-03 11:00:00', 'F002', '00007890', 100.00, 100.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

CREATE TABLE `pago` (
  `id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `fecha` datetime DEFAULT current_timestamp(),
  `metodo` enum('Yape','Plin','Tarjeta','Efectivo') NOT NULL,
  `estado` enum('Pagado','Pendiente','Rechazado') DEFAULT 'Pendiente',
  `monto` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pago`
--

INSERT INTO `pago` (`id`, `pedido_id`, `fecha`, `metodo`, `estado`, `monto`) VALUES
(1, 1, '2025-06-05 15:50:22', 'Yape', 'Pagado', 120.00),
(2, 1, '2025-06-05 15:50:22', 'Yape', 'Pagado', 120.00),
(3, 2, '2025-06-05 15:50:22', 'Plin', 'Pagado', 150.00),
(4, 3, '2025-06-04 13:00:00', 'Plin', 'Pagado', 80.00),
(5, 4, '2025-06-03 11:00:00', 'Tarjeta', 'Pagado', 100.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido`
--

CREATE TABLE `pedido` (
  `id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `fecha_entrega` datetime DEFAULT NULL,
  `estado` enum('Pendiente','En preparación','Entregado','Cancelado') DEFAULT 'Pendiente',
  `subtotal` decimal(10,2) NOT NULL,
  `costo_total` decimal(10,2) NOT NULL,
  `direccion_entrega` varchar(200) DEFAULT NULL,
  `notas` text DEFAULT NULL,
  `necesita_factura` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedido`
--

INSERT INTO `pedido` (`id`, `usuario_id`, `fecha_creacion`, `fecha_entrega`, `estado`, `subtotal`, `costo_total`, `direccion_entrega`, `notas`, `necesita_factura`) VALUES
(1, 1, '2025-06-05 15:50:22', '2025-06-10 14:00:00', 'En preparación', 120.00, 120.00, 'Av. Siempre Viva 123', 'Sin maní', 1),
(2, 6, '2025-06-05 15:50:22', '2025-06-11 16:30:00', 'Pendiente', 150.00, 150.00, 'Calle Los Almendros 456', 'Pastel con logo', 0),
(3, 2, '2025-06-04 12:20:00', '2025-06-09 15:00:00', 'Entregado', 80.00, 80.00, 'Jr. Las Flores 789', 'Cupcakes personalizados', 0),
(4, 3, '2025-06-03 10:10:00', '2025-06-08 18:30:00', 'Entregado', 100.00, 100.00, 'Av. Primavera 321', 'Sin azúcar', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id` bigint(20) NOT NULL,
  `categoria_id` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `costo_produccion` decimal(10,2) NOT NULL,
  `disponible` tinyint(1) DEFAULT 1,
  `imagen_url` varchar(255) DEFAULT NULL,
  `cantidad` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id`, `categoria_id`, `nombre`, `descripcion`, `precio_venta`, `costo_produccion`, `disponible`, `imagen_url`, `cantidad`) VALUES
(1, 1, 'Torta de Chocolate', 'Torta de tres pisos con relleno de chocolate', 120.00, 80.00, 1, 'https://cdn0.recetasgratis.net/es/posts/1/9/6/torta_de_chocolate_esponjosa_10691_600.jpg', 3),
(2, 1, 'Torta de Vainilla', 'Torta esponjosa con relleno de crema chantilly', 100.00, 65.00, 1, 'https://peopleenespanol.com/thmb/195yL5HKvkX-V5hK4Xtdt7jvbaU=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/pastel-de-vainilla-bc3a1sico-2000-d5538c31da3b4b6dba9940393128f2b2.jpg', 4),
(3, 1, 'Red Velvet', 'Clásica torta roja con cubierta de queso crema', 150.00, 95.00, 1, 'https://i.ytimg.com/vi/aQOJEu77Pxs/maxresdefault.jpg', 3),
(4, 2, 'Pack Cupcakes', 'Set de 24 cupcakes decorados', 80.00, 45.00, 1, 'https://lithdechocolat.pe/wp-content/uploads/2024/08/y8oI8EWV.jpeg', 2),
(5, 2, 'Pack alfajorcitos', 'Perfecto para regalar a alguien especial', 20.00, 14.00, 1, 'https://res.cloudinary.com/riqra/image/upload/w_656,h_656,c_limit,q_auto,f_auto/v1742914440/sellers/tortas-gaby/products/yghr1jqwsb9yybwzo1vy.png', 3),
(6, 2, 'Pastel futbol', 'Pastel con diseño perfecto para un amante del futbol', 80.00, 50.00, 1, 'https://i.ytimg.com/vi/v7sxI7kPNZQ/maxresdefault.jpg', 3),
(7, 3, 'Suspiro a la limeña', 'Clásico postre peruano hecho con manjarblanco y merengue italiano, espolvoreado con canela.', 5.00, 3.50, 1, 'https://portal.andina.pe/EDPfotografia2/Thumbnail/2008/05/17/000062608W.jpg', 4),
(8, 3, 'Leche Asada', 'Tradicional postre peruano similar al flan.', 3.00, 2.00, 1, 'https://i.pinimg.com/736x/e5/1d/1b/e51d1b7904c4e88b4574b5c6be50097a.jpg', 5),
(9, 3, 'Arroz con leche', 'Postre con arroz cocido en leche condensada, canela y clavo.', 3.00, 2.00, 1, 'https://www.bekiacocina.com/images/cocina/0000/96-h.jpg', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_pedido`
--

CREATE TABLE `detalle_pedido` (
  `id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `producto_id` bigint(20) NOT NULL,
  `cantidad` int(11) DEFAULT 1,
  `precio_unitario` decimal(10,2) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL,
  `personalizacion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detalle_pedido`
--

INSERT INTO `detalle_pedido` (`id`, `pedido_id`, `producto_id`, `cantidad`, `precio_unitario`, `costo_unitario`, `personalizacion`) VALUES
(1, 1, 1, 1, 120.00, 80.00, 'Con dedicatoria'),
(2, 2, 3, 1, 150.00, 95.00, 'Agregar logo personalizado'),
(3, 3, 4, 1, 80.00, 45.00, 'Cupcakes temáticos'),
(4, 4, 2, 1, 100.00, 65.00, 'Mensaje: Feliz Día');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

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
  `rol` enum('CLIENTE','ADMIN') NOT NULL DEFAULT 'CLIENTE',
  `activado` tinyint(1) DEFAULT 0,
  `recomendacion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `nombre`, `apellido`, `correo`, `telefono`, `direccion`, `password`, `fecha_registro`, `tipo_documento`, `numero_documento`, `rol`, `activado`) VALUES
(1, 'Ola', '', 'ola@g.com', '', '', '$2a$10$VG1UYeKyUMKss6w6JHqs4eJzMET8ruG0VyA7M0GSBHFSonQym7K.2', '2025-05-22 23:44:27', 'DNI', NULL, 'CLIENTE', 0),
(2, 'Ola2', '', 'ola@c.com', '', '', '$2a$10$4fDgsWjwfs0Q.8LJuRPG/urcsRhWPtovIGPqZPZZGY/LFy82R9a/u', '2025-05-22 23:47:27', 'DNI', NULL, 'CLIENTE', 0),
(3, 'As', '', 'as@gm.com', '', '', '$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C', '2025-05-23 00:07:14', 'DNI', NULL, 'CLIENTE', 0),
(4, 'Ola', '', 'ola4@i.com', '', '', '$2a$10$2PVIZjwduHoXIDUO8glHa.6CqEg8RuZfGcbcMhebUjxBnqEPY//fu', '2025-05-23 03:28:50', 'DNI', NULL, 'CLIENTE', 0),
(5, 'Administrador', 'Sistema', 'admin@c.com', '999888777', 'Oficina Central', '$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C', '2025-05-22 23:17:00', 'DNI', '87654321', 'ADMIN', 0),
(6, 'Juana', 'Pérez', 'juana@correo.com', '987654321', 'Calle Falsa 123', '$2a$10$demo', '2025-06-05 15:50:22', 'DNI', '12345678', 'CLIENTE', 0);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categoria_producto`
--
ALTER TABLE `categoria_producto`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `comprobante`
--
ALTER TABLE `comprobante`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pedido_id` (`pedido_id`);

--
-- Indices de la tabla `pago`
--
ALTER TABLE `pago`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pedido_id` (`pedido_id`);

--
-- Indices de la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `categoria_id` (`categoria_id`);

--
-- Indices de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pedido_id` (`pedido_id`),
  ADD KEY `producto_id` (`producto_id`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `correo` (`correo`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categoria_producto`
--
ALTER TABLE `categoria_producto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `comprobante`
--
ALTER TABLE `comprobante`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `comprobante`
--
ALTER TABLE `comprobante`
  ADD CONSTRAINT `comprobante_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `pago`
--
ALTER TABLE `pago`
  ADD CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_producto` (`id`);

--
-- Filtros para la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
