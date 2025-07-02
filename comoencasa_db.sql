-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 02-07-2025 a las 06:08:54
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
  `nombre` varchar(100) NOT NULL,
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
(5, 4, 'Factura', '2025-06-03 11:00:00', 'F002', '00007890', 100.00, 100.00),
(15, 14, 'Factura', '2025-06-20 18:23:57', '004', '00000004', 240.00, 250.00),
(16, 15, 'Boleta', '2025-06-20 22:42:01', '003', '00000003', 120.00, 130.00),
(17, 16, 'Boleta', '2025-06-23 20:18:45', '004', '00000004', 320.00, 330.00),
(18, 17, 'Boleta', '2025-06-25 15:41:59', '005', '00000005', 15.00, 25.00),
(19, 18, 'Boleta', '2025-06-25 16:17:52', '006', '00000006', 10.00, 20.00),
(22, 21, 'Boleta', '2025-06-25 23:18:53', '007', '00000007', 6.00, 16.00),
(23, 22, 'Factura', '2025-06-25 23:19:55', '004', '00000004', 3.00, 13.00);

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
(4, 4, 2, 1, 100.00, 65.00, 'Mensaje: Feliz Día'),
(16, 14, 1, 2, 120.00, 80.00, 'jhhj'),
(17, 15, 1, 1, 120.00, 80.00, ''),
(20, 17, 8, 5, 3.00, 2.00, 'sfdsfds'),
(21, 18, 7, 2, 5.00, 3.50, 'wedsadas'),
(24, 21, 9, 2, 3.00, 2.00, 'asda'),
(25, 22, 9, 1, 3.00, 2.00, 'jnas'),
(27, 24, 7, 1, 5.00, 3.50, 'adas'),
(28, 25, 7, 1, 5.00, 3.50, '');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

CREATE TABLE `pago` (
  `id` bigint(20) NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `fecha` datetime DEFAULT current_timestamp(),
  `metodo` enum('Yape','Plin','Tarjeta','Efectivo','PayPal') NOT NULL,
  `estado` enum('Pagado','Pendiente','Rechazado') DEFAULT 'Pendiente',
  `monto` decimal(10,2) NOT NULL,
  `payer_id` varchar(255) DEFAULT NULL,
  `paypal_email` varchar(255) DEFAULT NULL,
  `paypal_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pago`
--

INSERT INTO `pago` (`id`, `pedido_id`, `fecha`, `metodo`, `estado`, `monto`, `payer_id`, `paypal_email`, `paypal_id`) VALUES
(1, 1, '2025-06-05 15:50:22', 'Yape', 'Pagado', 120.00, NULL, NULL, NULL),
(2, 1, '2025-06-05 15:50:22', 'Yape', 'Pagado', 120.00, NULL, NULL, NULL),
(3, 2, '2025-06-05 15:50:22', 'Plin', 'Pagado', 150.00, NULL, NULL, NULL),
(4, 3, '2025-06-04 13:00:00', 'Plin', 'Pagado', 80.00, NULL, NULL, NULL),
(5, 4, '2025-06-03 11:00:00', 'Tarjeta', 'Pagado', 100.00, NULL, NULL, NULL),
(15, 14, '2025-06-20 18:23:57', 'Yape', 'Pagado', 250.00, NULL, NULL, NULL),
(16, 15, '2025-06-20 22:42:01', 'Plin', 'Pagado', 130.00, NULL, NULL, NULL),
(17, 16, '2025-06-23 20:18:45', 'Yape', 'Pagado', 330.00, NULL, NULL, NULL),
(18, 17, '2025-06-25 15:41:59', 'Yape', 'Pagado', 25.00, NULL, NULL, NULL),
(19, 18, '2025-06-25 16:17:52', 'Yape', 'Pagado', 20.00, NULL, NULL, NULL),
(22, 21, '2025-06-25 23:18:53', 'Yape', 'Pagado', 16.00, NULL, NULL, NULL),
(23, 22, '2025-06-25 23:19:55', 'Yape', 'Pagado', 13.00, NULL, NULL, NULL),
(24, 24, '2025-07-01 22:05:15', 'PayPal', 'Rechazado', 15.00, '5BTTTMTJ2F73E', 'sb-478b8h44302552@business.example.com', '45A03638RU3316143'),
(25, 25, '2025-07-02 03:39:07', 'PayPal', 'Rechazado', 15.00, '5BTTTMTJ2F73E', 'sb-478b8h44302552@business.example.com', '1E7566416K920143T');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido`
--

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

--
-- Volcado de datos para la tabla `pedido`
--

INSERT INTO `pedido` (`id`, `usuario_id`, `fecha_creacion`, `fecha_entrega`, `estado`, `subtotal`, `costo_total`, `direccion_entrega`, `necesita_factura`) VALUES
(1, 1, '2025-06-05 15:50:22', '2025-06-10 14:00:00', 'Entregado', 120.00, 120.00, 'Av. Siempre Viva 123', 1),
(2, 6, '2025-06-05 15:50:22', '2025-06-11 16:30:00', 'Entregado', 150.00, 150.00, 'Calle Los Almendros 456', 0),
(3, 2, '2025-06-04 12:20:00', '2025-06-09 15:00:00', 'Entregado', 80.00, 80.00, 'Jr. Las Flores 789', 0),
(4, 3, '2025-06-03 10:10:00', '2025-06-08 18:30:00', 'Entregado', 100.00, 100.00, 'Av. Primavera 321', 1),
(14, 5, '2025-06-20 18:23:57', '2025-06-25 16:16:59', 'Entregado', 240.00, 250.00, 'ddsfsd, Breña', 1),
(15, 8, '2025-06-20 22:42:01', '2025-06-20 22:43:37', 'Entregado', 120.00, 130.00, 'sa, Los Olivos (wqewq)', 0),
(16, 8, '2025-06-23 20:18:45', '2025-06-23 20:20:36', 'Entregado', 320.00, 330.00, 'sada, Barranco (asdasda)', 0),
(17, 8, '2025-06-25 15:41:59', '2025-06-25 16:16:15', 'Entregado', 15.00, 25.00, 'dsfdsf, Breña (as)', 0),
(18, 8, '2025-06-25 16:17:52', '2025-06-25 16:18:25', 'Entregado', 10.00, 20.00, 'dsadasd, Breña (as)', 0),
(21, 5, '2025-06-25 23:18:53', '2025-06-29 04:18:53', 'Pendiente', 6.00, 16.00, 'as, Barranco (ads)', 0),
(22, 8, '2025-06-25 23:19:55', '2025-06-29 04:19:55', 'Pendiente', 3.00, 13.00, 'dsadasd, Ate (as)', 1),
(24, 5, '2025-07-01 22:05:15', '2025-07-05 03:05:15', 'Pendiente', 5.00, 15.00, 'asdasdas, Breña (asdas)', 0),
(25, 8, '2025-07-02 03:39:07', '2025-07-05 08:39:07', 'Pendiente', 5.00, 15.00, 'xzc, Breña (wqewq)', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

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

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id`, `categoria_id`, `nombre`, `descripcion`, `precio_venta`, `costo_produccion`, `disponible`, `imagen_url`, `cantidad`) VALUES
(1, 1, 'Torta Corazon Selva Negra', 'Torta cora', 85, 40, 1, 'https://i.postimg.cc/gJGK5KK1/Torta-Corazon-Selva-Negra.jpg', 4),
(2, 1, 'Torta Humeda Chocolate', 'Torta Humeda', 85, 45, 1, 'https://i.postimg.cc/ZRjLhy7n/Torta-Humeda-Chocolate.jpg', 4),
(3, 1, 'Torta Sublime', 'Torta Sublime', 85, 45, 1, 'https://i.postimg.cc/9fdpJkyG/Torta-Sublime.jpg', 4),
(4, 2, 'Torta Choco Real Madrid', 'Tematica Real Madrid', 180, 90, 1, 'https://i.postimg.cc/SN9z6M69/Torta-Dechocolate-Contematica-Real-Madrid.jpg', 3),
(5, 2, 'Torta Frozen', 'Torta Frozen', 250, 125, 1, 'https://i.postimg.cc/KzYMBpfc/Torta-Frozen.jpg', 3),
(6, 2, 'Torta Matrimonio', 'Torta Matri', 400, 200, 1, 'https://i.postimg.cc/bvpbNw2t/Torta-Matrimonio.jpg', 2),
(7, 3, 'Suspiro a la lime&ntilde;a', 'Cl&aacute;sico postre peruano hecho con manjarblanco y merengue italiano, espolvoreado con canela.', 5, 3.5, 1, 'https://portal.andina.pe/EDPfotografia2/Thumbnail/2008/05/17/000062608W.jpg', 3),
(8, 3, 'Leche Asada', 'Tradicional postre peruano similar al flan.', 3, 1.5, 1, 'https://i.postimg.cc/vZqMDP4t/Leche-Asada.jpg', 3),
(9, 3, 'Tartaleta Fresa', 'Tartaleta fresa', 5, 2.5, 1, 'https://i.postimg.cc/mg2BLxgr/Tartaleta-De-Fresa.jpg', 2),
(14, 2, 'Torta Univesitario Deportes', 'Torta U', 180, 90, 1, 'https://i.postimg.cc/fbCd3Qxg/Torta-Tematica-Dela-U.jpg', 2),
(15, 3, 'Brownie', 'Brownie', 4.5, 1.75, 1, 'https://i.postimg.cc/YSv7F1gK/brownie.jpg', 2);

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
  `rol` varchar(255) NOT NULL,
  `activado` tinyint(1) DEFAULT 0,
  `recomendacion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `nombre`, `apellido`, `correo`, `telefono`, `direccion`, `password`, `fecha_registro`, `tipo_documento`, `numero_documento`, `rol`, `activado`, `recomendacion`) VALUES
(1, 'Ola', '', 'ola@g.com', '', '', '$2a$10$VG1UYeKyUMKss6w6JHqs4eJzMET8ruG0VyA7M0GSBHFSonQym7K.2', '2025-05-22 23:44:27', 'DNI', NULL, 'CLIENTE', 1, NULL),
(2, 'Ola2', '', 'ola@c.com', '', '', '$2a$10$4fDgsWjwfs0Q.8LJuRPG/urcsRhWPtovIGPqZPZZGY/LFy82R9a/u', '2025-05-22 23:47:27', 'DNI', NULL, 'CLIENTE', 0, NULL),
(3, 'As', '', 'as@gm.com', '', '', '$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C', '2025-05-23 00:07:14', 'DNI', NULL, 'CLIENTE', 1, 'dsfsdfdsfs'),
(4, 'Ola', '', 'ola4@i.com', '', '', '$2a$10$2PVIZjwduHoXIDUO8glHa.6CqEg8RuZfGcbcMhebUjxBnqEPY//fu', '2025-05-23 03:28:50', 'DNI', NULL, 'CLIENTE', 0, NULL),
(5, 'Administrador', 'Sistema', 'admin@c.com', '999888777', 'Oficina Central', '$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C', '2025-05-22 23:17:00', 'DNI', '21312321', 'ADMIN', 1, NULL),
(6, 'Juana', 'Pérez', 'juana@correo.com', '987654321', 'Calle Falsa 123', '$2a$10$demo', '2025-06-05 15:50:22', 'DNI', '12345678', 'CLIENTE', 0, NULL),
(8, 'dav', 'david', 'davidestudio123@gmail.com', '', '', '$2a$10$pD6CtYTDqLPikuKqHx6KTuA36pLkD9vAOAmR2dtNHBAxkgw0vHG12', '2025-06-20 22:39:09', 'DNI', '12321213', 'CLIENTE', 1, 'Buenas tortaas');

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
-- Indices de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pedido_id` (`pedido_id`),
  ADD KEY `producto_id` (`producto_id`);

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
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `comprobante`
--
ALTER TABLE `comprobante`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `comprobante`
--
ALTER TABLE `comprobante`
  ADD CONSTRAINT `comprobante_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`);

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
