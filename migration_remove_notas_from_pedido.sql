-- Migración para remover la columna 'notas' de la tabla 'pedido'
-- Fecha: 19 de junio de 2025
-- Razón: Las notas específicas ahora se manejan a nivel de detalle_pedido en la columna 'personalizacion'

USE prueba_db;

-- Eliminar la columna notas de la tabla pedido
ALTER TABLE `pedido` DROP COLUMN `notas`;

-- Verificar la estructura actualizada
DESCRIBE `pedido`;
