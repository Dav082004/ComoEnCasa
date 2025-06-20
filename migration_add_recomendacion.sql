-- Migración para agregar la columna recomendacion a la tabla usuario
-- Ejecutar este script en la base de datos MySQL/MariaDB

USE prueba_db;

-- Verificar si la columna ya existe antes de agregarla
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'prueba_db'
    AND TABLE_NAME = 'usuario'
    AND COLUMN_NAME = 'recomendacion'
);

-- Agregar la columna solo si no existe
SET @sql = CASE 
    WHEN @column_exists = 0 THEN 'ALTER TABLE usuario ADD COLUMN recomendacion TEXT DEFAULT NULL'
    ELSE 'SELECT "La columna recomendacion ya existe" as mensaje'
END;

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar que la columna se agregó correctamente
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'prueba_db' 
AND TABLE_NAME = 'usuario' 
AND COLUMN_NAME = 'recomendacion';

-- Opcional: Insertar algunas recomendaciones de ejemplo para testing
-- UPDATE usuario SET recomendacion = 'Excelente servicio, las tortas son deliciosas y el personal muy amable. ¡Totalmente recomendado!' WHERE id = 5;
-- UPDATE usuario SET recomendacion = 'Me encanta la calidad de los productos, siempre frescos y con un sabor increíble.' WHERE id = 6;
