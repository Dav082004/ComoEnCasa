-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: comoencasa_db
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categoria_producto`
--

DROP TABLE IF EXISTS `categoria_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria_producto` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria_producto`
--

LOCK TABLES `categoria_producto` WRITE;
/*!40000 ALTER TABLE `categoria_producto` DISABLE KEYS */;
INSERT INTO `categoria_producto` VALUES (1,'Tortas','Deliciosas tortas personalizadas para toda ocasión'),(2,'Eventos','Productos especiales para eventos y celebraciones'),(3,'Postres','Variedad de postres y dulces para complementar tu comida');
/*!40000 ALTER TABLE `categoria_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comprobante`
--

DROP TABLE IF EXISTS `comprobante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comprobante` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pedido_id` bigint(20) NOT NULL,
  `tipo` enum('Factura','Boleta') NOT NULL,
  `fecha_emision` datetime DEFAULT current_timestamp(),
  `numero_serie` varchar(4) NOT NULL,
  `numero_comprobante` varchar(8) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pedido_id` (`pedido_id`),
  CONSTRAINT `comprobante_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comprobante`
--

LOCK TABLES `comprobante` WRITE;
/*!40000 ALTER TABLE `comprobante` DISABLE KEYS */;
INSERT INTO `comprobante` VALUES (2,1,'Boleta','2025-06-05 15:50:22','B001','00001234',120.00,120.00),(4,3,'Boleta','2025-06-04 13:00:00','B002','00004567',80.00,80.00),(5,4,'Factura','2025-06-03 11:00:00','F002','00007890',100.00,100.00),(15,14,'Factura','2025-06-20 18:23:57','004','00000004',240.00,250.00),(22,21,'Boleta','2025-06-25 23:18:53','007','00000007',6.00,16.00);
/*!40000 ALTER TABLE `comprobante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalle_pedido` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pedido_id` bigint(20) NOT NULL,
  `producto_id` bigint(20) NOT NULL,
  `cantidad` int(11) DEFAULT 1,
  `precio_unitario` decimal(10,2) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL,
  `personalizacion` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pedido_id` (`pedido_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE,
  CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_pedido`
--

LOCK TABLES `detalle_pedido` WRITE;
/*!40000 ALTER TABLE `detalle_pedido` DISABLE KEYS */;
INSERT INTO `detalle_pedido` VALUES (1,1,1,1,120.00,80.00,'Con dedicatoria'),(3,3,4,1,80.00,45.00,'Cupcakes temáticos'),(4,4,2,1,100.00,65.00,'Mensaje: Feliz Día'),(16,14,1,2,120.00,80.00,'jhhj'),(24,21,9,2,3.00,2.00,'asda'),(27,24,7,1,5.00,3.50,'adas');
/*!40000 ALTER TABLE `detalle_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pago` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pedido_id` bigint(20) NOT NULL,
  `fecha` datetime DEFAULT current_timestamp(),
  `metodo` enum('Yape','Plin','Tarjeta','Efectivo','PayPal') NOT NULL,
  `estado` enum('Pagado','Pendiente','Rechazado') DEFAULT 'Pendiente',
  `monto` decimal(10,2) NOT NULL,
  `payer_id` varchar(255) DEFAULT NULL,
  `paypal_email` varchar(255) DEFAULT NULL,
  `paypal_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pedido_id` (`pedido_id`),
  CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
INSERT INTO `pago` VALUES (1,1,'2025-06-05 15:50:22','Yape','Pagado',120.00,NULL,NULL,NULL),(2,1,'2025-06-05 15:50:22','Yape','Pagado',120.00,NULL,NULL,NULL),(4,3,'2025-06-04 13:00:00','Plin','Pagado',80.00,NULL,NULL,NULL),(5,4,'2025-06-03 11:00:00','Tarjeta','Pagado',100.00,NULL,NULL,NULL),(15,14,'2025-06-20 18:23:57','Yape','Pagado',250.00,NULL,NULL,NULL),(22,21,'2025-06-25 23:18:53','Yape','Pagado',16.00,NULL,NULL,NULL),(24,24,'2025-07-01 22:05:15','PayPal','Rechazado',15.00,'5BTTTMTJ2F73E','sb-478b8h44302552@business.example.com','45A03638RU3316143');
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint(20) NOT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `fecha_entrega` datetime DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `subtotal` decimal(38,2) DEFAULT NULL,
  `costo_total` decimal(38,2) DEFAULT NULL,
  `direccion_entrega` varchar(255) DEFAULT NULL,
  `necesita_factura` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` VALUES (1,1,'2025-06-05 15:50:22','2025-06-10 14:00:00','Entregado',120.00,120.00,'Av. Siempre Viva 123',1),(3,2,'2025-06-04 12:20:00','2025-06-09 15:00:00','Entregado',80.00,80.00,'Jr. Las Flores 789',0),(4,3,'2025-06-03 10:10:00','2025-06-08 18:30:00','Entregado',100.00,100.00,'Av. Primavera 321',1),(14,5,'2025-06-20 18:23:57','2025-06-25 16:16:59','Entregado',240.00,250.00,'ddsfsd, Breña',1),(21,5,'2025-06-25 23:18:53','2025-06-29 04:18:53','Pendiente',6.00,16.00,'as, Barranco (ads)',0),(24,5,'2025-07-01 22:05:15','2025-07-05 03:05:15','Pendiente',5.00,15.00,'asdasdas, Breña (asdas)',0);
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `producto` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `categoria_id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio_venta` double NOT NULL,
  `costo_produccion` double NOT NULL,
  `disponible` tinyint(1) DEFAULT 1,
  `imagen_url` varchar(255) DEFAULT NULL,
  `cantidad` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `categoria_id` (`categoria_id`),
  CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categoria_producto` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,1,'Torta Corazon Selva Negra','Torta cora',85,40,1,'https://i.postimg.cc/gJGK5KK1/Torta-Corazon-Selva-Negra.jpg',0),(2,1,'Torta Humeda Chocolate','Torta Humeda',85,45,1,'https://i.postimg.cc/ZRjLhy7n/Torta-Humeda-Chocolate.jpg',0),(3,1,'Torta Sublime','Torta Sublime',85,45,1,'https://i.postimg.cc/9fdpJkyG/Torta-Sublime.jpg',4),(4,2,'Torta Choco Real Madrid','Tematica Real Madrid',180,90,1,'https://i.postimg.cc/SN9z6M69/Torta-Dechocolate-Contematica-Real-Madrid.jpg',3),(5,2,'Torta Frozen','Torta Frozen',250,125,1,'https://i.postimg.cc/KzYMBpfc/Torta-Frozen.jpg',3),(6,2,'Torta Matrimonio','Torta Matri',400,200,1,'https://i.postimg.cc/CMJzjp6j/Torta-Matrimonio.jpg',2),(7,3,'Suspiro a la lime&ntilde;a','Clasico postre peruano hecho con manjarblanco y merengue italiano, espolvoreado con canela.',5,3.5,1,'https://portal.andina.pe/EDPfotografia2/Thumbnail/2008/05/17/000062608W.jpg',3),(8,3,'Leche Asada','Tradicional postre peruano similar al flan.',3,1.5,1,'https://i.postimg.cc/vZqMDP4t/Leche-Asada.jpg',3),(9,3,'Tartaleta Fresa','Tartaleta fresa',5,2.5,1,'https://i.postimg.cc/mg2BLxgr/Tartaleta-De-Fresa.jpg',2),(14,2,'Torta Univesitario Deportes','Torta U',180,90,1,'https://i.postimg.cc/fbCd3Qxg/Torta-Tematica-Dela-U.jpg',2),(15,3,'Brownie','Brownie',4.5,1.75,1,'https://i.postimg.cc/YSv7F1gK/brownie.jpg',2);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
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
  `recomendacion` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Ola','','ola@g.com','','','$2a$10$VG1UYeKyUMKss6w6JHqs4eJzMET8ruG0VyA7M0GSBHFSonQym7K.2','2025-05-22 23:44:27','DNI',NULL,'CLIENTE',1,NULL),(2,'Ola2','','ola@c.com','','','$2a$10$4fDgsWjwfs0Q.8LJuRPG/urcsRhWPtovIGPqZPZZGY/LFy82R9a/u','2025-05-22 23:47:27','DNI',NULL,'CLIENTE',0,NULL),(3,'As','','as@gm.com','','','$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C','2025-05-23 00:07:14','DNI',NULL,'CLIENTE',1,'dsfsdfdsfs'),(4,'Ola','','ola4@i.com','','','$2a$10$2PVIZjwduHoXIDUO8glHa.6CqEg8RuZfGcbcMhebUjxBnqEPY//fu','2025-05-23 03:28:50','DNI',NULL,'CLIENTE',0,NULL),(5,'Administrador','Sistema','admin@c.com','999888777','Oficina Central','$2a$10$N7CmTg0nuYhgQcLYmxVswe0c1rn9uxuwVlJ9sK5geXraJCupT.Z1C','2025-05-22 23:17:00','DNI','21312321','ADMIN',1,NULL),(10,'David','Contre','davidestudio123@gmail.com','','','$2a$10$mTHZdFZ8ctipiKp1UQg01.jHvtS4wSGLwlc20DYzI6qB8MDOkDyh2','2025-07-03 23:59:44','DNI',NULL,'CLIENTE',0,NULL),(11,'D','A','daviestudio123@gmail.com','','','$2a$10$7R/gnTsxuM4uBFI4c6124ORG6LZz0WomXnn9AAeDYmLHFFMqpJNOS','2025-07-04 00:08:43','DNI',NULL,'CLIENTE',0,NULL),(12,'d','asd','admin2@c.com','','','$2a$10$ABKYUKKhB2ZhGbMvp3bdO.Uv.X.ebwfKHgA32d7BUQ2btrAuLLOU6','2025-07-04 00:09:45','DNI',NULL,'CLIENTE',0,NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-16 19:16:02
