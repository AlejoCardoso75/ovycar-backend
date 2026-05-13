-- Script de inicialización de datos para OvyCar (Docker: primera carga de MySQL)
-- Contraseña para ambos usuarios iniciales: Manchas123.  (BCrypt cost 10, compatible Spring Security)

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

-- Misma contraseña hasheada (Manchas123.) para los dos admins de arranque
INSERT IGNORE INTO usuarios (id, username, password, nombre, apellido, email, rol, activo) VALUES
(1, 'Alejo75', '$2b$10$IxJm.kdVuTso78scp7wR3emjM69Msz9A6ietwYm39cOFuoEDVyYvC', 'Alejandro', 'Cardoso', 'alejandro.cardosoparra@gmail.com', 'ADMIN', true),
(2, 'AdminOvy', '$2b$10$IxJm.kdVuTso78scp7wR3emjM69Msz9A6ietwYm39cOFuoEDVyYvC', 'Administrador', 'Taller', 'admin.talleresoviedo@gmail.com', 'ADMIN', true);
