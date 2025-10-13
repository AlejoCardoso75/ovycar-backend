-- Script de inicialización de datos para OvyCar
-- Insertar usuarios existentes con sus datos reales

-- Crear tabla usuarios si no existe (estructura completa)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    rol VARCHAR(50) DEFAULT 'USER',
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP NULL
);

-- Insertar usuarios existentes con sus datos reales
INSERT IGNORE INTO usuarios (id, username, password, nombre, apellido, email, rol, activo) VALUES
(4, 'Alejo75', '$2a$10$u6UI/67zwsp6yPmCMQXkTOZuZeEfF2ZNgI1qUzZlLLBvvyhCMQu52', 'Alejandro', 'Cardoso', 'alejandro.cardosoparra@gmail.com', 'ADMIN', true),
(5, 'Andres', '$2a$10$7PJPpul2cpfDrniHC0M3AOyL.PW6AzxkjFiwP9.XCnCXQtguL95W2', 'Andres', 'Cardoso', 'andresf.cardosoyusty@gmail.com', 'ADMIN', true),
(6, 'Lizardo', '$2a$10$zazon4I7GvDtQU.j4eJdROr8RUvGTRYSSBMK5z08Mdd4sPaDU9klu', 'Lizardo', 'Oviedo', 'joselizardooviedo1985@hotmail.com', 'ADMIN', true),
(7, 'LuisC', '$2a$10$5zHiZmTa2hLk7Tm73KZ4qO2VzkhH7srEdiGkfWYkwvz2sF6ZCvHi6', 'Luis', 'Cardoso', 'luiscardosoyusty@gmail.com', 'ADMIN', true);

-- Nota: Estos son los usuarios existentes con sus contraseñas hasheadas originales
