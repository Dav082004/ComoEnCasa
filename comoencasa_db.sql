-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 24-05-2025 a las 00:50:05
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
(7, 3, 'Suspiro a la limeña', 'Clásico postre peruano hecho con manjarblanco y merengue italiano, espolvoreado con canela. Textura cremosa y dulce intenso.', 5.00, 3.50, 1, 'https://portal.andina.pe/EDPfotografia2/Thumbnail/2008/05/17/000062608W.jpg', 4),
(8, 3, 'Leche Asada', 'Tradicional postre peruano de horneo, similar al flan pero sin huevo. Textura sedosa con caramelo dorado.', 3.00, 2.00, 1, 'https://i.pinimg.com/736x/e5/1d/1b/e51d1b7904c4e88b4574b5c6be50097a.jpg', 5),
(9, 3, 'Arroz con leche', 'Reconfortante postre tradicional con arroz cocido en leche condensada, canela y clavo de olor. Toque final de canela molida.', 3.00, 2.00, 1, 'https://www.bekiacocina.com/images/cocina/0000/96-h.jpg', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto_pedido`
--

CREATE TABLE `producto_pedido` (
  `id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `producto_id` bigint(20) NOT NULL,
  `cantidad` int(11) DEFAULT 1,
  `precio_unitario` decimal(10,2) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL,
  `personalizacion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reporte_categoria`
--

CREATE TABLE `reporte_categoria` (
  `id` bigint(20) NOT NULL,
  `categoria_id` bigint(20) NOT NULL,
  `fecha` date NOT NULL,
  `tipo_periodo` enum('Diario','Semanal','Mensual','Anual') NOT NULL,
  `total_ventas` decimal(12,2) NOT NULL,
  `cantidad_vendida` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL,
  `nombre_completo` varchar(100) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `direccion` varchar(200) NOT NULL,
  `password` varchar(255) NOT NULL,
  `fecha_registro` datetime DEFAULT current_timestamp(),
  `tipo_documento` enum('DNI','RUC','CE') DEFAULT 'DNI',
  `numero_documento` varchar(20) DEFAULT NULL,
  `rol` varchar(255) NOT NULL,
  `activo` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre_completo`, `correo`, `telefono`, `direccion`, `password`, `fecha_registro`, `tipo_documento`, `numero_documento`, `rol`, `activo`) VALUES
(1, 'ola', 'ola@g.com', '', '', '$2a$10$VG1UYeKyUMKss6w6JHqs4eJzMET8ruG0VyA7M0GSBHFSonQym7K.2', '2025-05-22 23:44:27', 'DNI', NULL, 'CLIENTE', b'1'),
(2, 'ola2', 'ola@c.com', '', '', '$2a$10$4fDgsWjwfs0Q.8LJuRPG/urcsRhWPtovIGPqZPZZGY/LFy82R9a/u', '2025-05-22 23:47:27', 'DNI', NULL, 'CLIENTE', b'1'),
(3, 'as', 'as@gm.com', '', '', '$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C', '2025-05-23 00:07:14', 'DNI', NULL, 'CLIENTE', b'1'),
(4, 'ola ', 'ola4@i.com', '', '', '$2a$10$2PVIZjwduHoXIDUO8glHa.6CqEg8RuZfGcbcMhebUjxBnqEPY//fu', '2025-05-23 03:28:50', 'DNI', NULL, 'CLIENTE', b'1'),
(6, 'Administrador Sistema', 'admin@c.com', '999888777', 'Oficina Central', '$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C', '2025-05-22 23:17:00', 'DNI', '87654321', 'ADMIN', b'1');

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
-- Indices de la tabla `producto_pedido`
--
ALTER TABLE `producto_pedido`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pedido_id` (`pedido_id`),
  ADD KEY `producto_id` (`producto_id`);

--
-- Indices de la tabla `reporte_categoria`
--
ALTER TABLE `reporte_categoria`
  ADD PRIMARY KEY (`id`),
  ADD KEY `categoria_id` (`categoria_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `correo` (`correo`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categoria_producto`
--
ALTER TABLE `categoria_producto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `comprobante`
--
ALTER TABLE `comprobante`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `producto_pedido`
--
ALTER TABLE `producto_pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `reporte_categoria`
--
ALTER TABLE `reporte_categoria`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `comprobante`
--
ALTER TABLE `comprobante`
  ADD CONSTRAINT `comprobante_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`);

--
-- Filtros para la tabla `pago`
--
ALTER TABLE `pago`
  ADD CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`);

--
-- Filtros para la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_producto` (`id`);

--
-- Filtros para la tabla `producto_pedido`
--
ALTER TABLE `producto_pedido`
  ADD CONSTRAINT `producto_pedido_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`),
  ADD CONSTRAINT `producto_pedido_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`);

--
-- Filtros para la tabla `reporte_categoria`
--
ALTER TABLE `reporte_categoria`
  ADD CONSTRAINT `reporte_categoria_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_producto` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
