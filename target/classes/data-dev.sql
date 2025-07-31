-- Datos de ejemplo para el sistema OvyCar (Perfil de Desarrollo)

-- Insertar clientes de ejemplo
INSERT INTO clientes (nombre, apellido, documento, telefono, email, direccion, fecha_registro, activo) VALUES
('Juan', 'Pérez', '12345678', '3001234567', 'juan.perez@email.com', 'Calle 123 #45-67, Bogotá', NOW(), true),
('María', 'González', '87654321', '3007654321', 'maria.gonzalez@email.com', 'Carrera 78 #12-34, Medellín', NOW(), true),
('Carlos', 'Rodríguez', '11223344', '3001122334', 'carlos.rodriguez@email.com', 'Avenida 5 #23-45, Cali', NOW(), true),
('Ana', 'López', '55667788', '3005566778', 'ana.lopez@email.com', 'Calle 10 #67-89, Barranquilla', NOW(), true),
('Luis', 'Martínez', '99887766', '3009988776', 'luis.martinez@email.com', 'Carrera 15 #34-56, Bucaramanga', NOW(), true);

-- Insertar vehículos de ejemplo
INSERT INTO vehiculos (cliente_id, placa, marca, modelo, año, color, numero_vin, kilometraje, fecha_registro, activo) VALUES
(1, 'ABC123', 'Toyota', 'Corolla', '2020', 'Blanco', '1HGBH41JXMN109186', 45000, NOW(), true),
(1, 'XYZ789', 'Honda', 'Civic', '2019', 'Negro', '2T1BURHE0JC123456', 38000, NOW(), true),
(2, 'DEF456', 'Ford', 'Focus', '2021', 'Azul', '3VWDX7AJ5DM123456', 25000, NOW(), true),
(3, 'GHI789', 'Chevrolet', 'Spark', '2018', 'Rojo', 'KL1TD5DE0BB123456', 65000, NOW(), true),
(4, 'JKL012', 'Nissan', 'Sentra', '2022', 'Gris', '3N1AB6AP0BL123456', 15000, NOW(), true);

-- Insertar productos de ejemplo
INSERT INTO productos (nombre, descripcion, codigo, precio_compra, precio_venta, stock, stock_minimo, categoria, marca, fecha_registro, activo) VALUES
('Aceite de Motor 5W-30', 'Aceite sintético para motor', 'ACE001', 25000.00, 35000.00, 50, 10, 'Lubricantes', 'Mobil', NOW(), true),
('Filtro de Aceite', 'Filtro de aceite universal', 'FIL001', 15000.00, 25000.00, 30, 5, 'Filtros', 'Fram', NOW(), true),
('Frenos Delanteros', 'Pastillas de freno delanteras', 'FRE001', 45000.00, 65000.00, 20, 8, 'Frenos', 'Brembo', NOW(), true),
('Batería 60Ah', 'Batería automotriz 60 amperios', 'BAT001', 180000.00, 250000.00, 15, 3, 'Baterías', 'Bosch', NOW(), true),
('Llantas 195/65R15', 'Llantas radiales 195/65R15', 'LLA001', 120000.00, 180000.00, 40, 10, 'Llantas', 'Michelin', NOW(), true),
('Aceite de Transmisión', 'Aceite para transmisión automática', 'ACE002', 35000.00, 50000.00, 25, 5, 'Lubricantes', 'Castrol', NOW(), true),
('Filtro de Aire', 'Filtro de aire de motor', 'FIL002', 12000.00, 20000.00, 35, 8, 'Filtros', 'K&N', NOW(), true),
('Amortiguadores', 'Amortiguadores delanteros', 'AMO001', 80000.00, 120000.00, 12, 4, 'Suspensión', 'Monroe', NOW(), true);

-- Insertar servicios de ejemplo
INSERT INTO servicios (nombre, descripcion, precio, categoria, tiempo_estimado, fecha_registro, activo) VALUES
('Cambio de Aceite', 'Cambio de aceite y filtro de motor', 50000.00, 'Mantenimiento', 60, NOW(), true),
('Cambio de Frenos', 'Cambio de pastillas y discos de freno', 120000.00, 'Frenos', 120, NOW(), true),
('Alineación y Balanceo', 'Alineación y balanceo de llantas', 80000.00, 'Suspensión', 90, NOW(), true),
('Diagnóstico Computarizado', 'Diagnóstico completo del vehículo', 35000.00, 'Diagnóstico', 45, NOW(), true),
('Cambio de Batería', 'Cambio de batería automotriz', 30000.00, 'Eléctrico', 30, NOW(), true),
('Cambio de Llantas', 'Cambio de llantas completas', 50000.00, 'Llantas', 60, NOW(), true),
('Lavado de Motor', 'Lavado y limpieza del motor', 40000.00, 'Limpieza', 45, NOW(), true),
('Revisión General', 'Revisión completa del vehículo', 25000.00, 'Mantenimiento', 30, NOW(), true);

-- Insertar mantenimientos de ejemplo
INSERT INTO mantenimientos (vehiculo_id, cliente_id, tipo_mantenimiento, descripcion, fecha_programada, estado, kilometraje_actual, kilometraje_proximo, observaciones, fecha_registro) VALUES
(1, 1, 'Preventivo', 'Cambio de aceite y filtros', DATE_ADD(NOW(), INTERVAL 1 DAY), 'PROGRAMADO', 45000, 50000, 'Cliente solicita cambio de aceite sintético', NOW()),
(2, 1, 'Correctivo', 'Reparación de sistema de frenos', DATE_ADD(NOW(), INTERVAL 2 DAY), 'PROGRAMADO', 38000, 40000, 'Cliente reporta ruido en frenos', NOW()),
(3, 2, 'Preventivo', 'Mantenimiento general', NOW(), 'EN_PROCESO', 25000, 30000, 'Mantenimiento rutinario', NOW()),
(4, 3, 'Correctivo', 'Cambio de batería', DATE_ADD(NOW(), INTERVAL -1 DAY), 'COMPLETADO', 65000, 70000, 'Batería descargada completamente', NOW()),
(5, 4, 'Preventivo', 'Cambio de llantas', DATE_ADD(NOW(), INTERVAL 3 DAY), 'PROGRAMADO', 15000, 20000, 'Cambio de llantas por desgaste', NOW());

-- Insertar detalles de mantenimiento de ejemplo
INSERT INTO detalles_mantenimiento (mantenimiento_id, servicio_id, producto_id, cantidad, precio_unitario, subtotal, descripcion, tipo_item) VALUES
(1, 1, 1, 1, 50000.00, 50000.00, 'Cambio de aceite sintético', 'SERVICIO'),
(1, NULL, 2, 1, 25000.00, 25000.00, 'Filtro de aceite nuevo', 'PRODUCTO'),
(2, 2, 3, 1, 120000.00, 120000.00, 'Cambio de frenos delanteros', 'SERVICIO'),
(2, NULL, 3, 1, 65000.00, 65000.00, 'Pastillas de freno Brembo', 'PRODUCTO'),
(3, 8, NULL, 1, 25000.00, 25000.00, 'Revisión general del vehículo', 'SERVICIO'),
(4, 5, 4, 1, 30000.00, 30000.00, 'Cambio de batería', 'SERVICIO'),
(4, NULL, 4, 1, 250000.00, 250000.00, 'Batería Bosch 60Ah', 'PRODUCTO'),
(5, 6, 5, 4, 50000.00, 200000.00, 'Cambio de llantas completas', 'SERVICIO'),
(5, NULL, 5, 4, 180000.00, 720000.00, 'Llantas Michelin 195/65R15', 'PRODUCTO');

-- Insertar facturas de ejemplo
INSERT INTO facturas (numero_factura, cliente_id, mantenimiento_id, fecha_emision, fecha_vencimiento, subtotal, impuestos, descuento, total, estado, observaciones, fecha_registro) VALUES
('FAC-20241225-143022', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 85000.00, 16150.00, 0.00, 101150.00, 'PENDIENTE', 'Factura por mantenimiento programado', NOW()),
('FAC-20241225-143023', 1, 2, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 185000.00, 35150.00, 10000.00, 210150.00, 'PENDIENTE', 'Factura por reparación de frenos', NOW()),
('FAC-20241225-143024', 2, 3, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 105000.00, 19950.00, 0.00, 124950.00, 'PAGADA', 'Factura por mantenimiento general', NOW()),
('FAC-20241225-143025', 3, 4, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL 29 DAY), 280000.00, 53200.00, 0.00, 333200.00, 'PAGADA', 'Factura por cambio de batería', NOW()),
('FAC-20241225-143026', 4, 5, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 230000.00, 43700.00, 15000.00, 258700.00, 'PENDIENTE', 'Factura por cambio de llantas', NOW());

-- Insertar detalles de factura de ejemplo
INSERT INTO detalles_factura (factura_id, servicio_id, producto_id, cantidad, precio_unitario, subtotal, descripcion, tipo_item) VALUES
(1, 1, NULL, 1, 50000.00, 50000.00, 'Cambio de aceite', 'SERVICIO'),
(1, NULL, 2, 1, 25000.00, 25000.00, 'Filtro de aceite', 'PRODUCTO'),
(1, NULL, 1, 1, 10000.00, 10000.00, 'Aceite sintético', 'PRODUCTO'),
(2, 2, NULL, 1, 120000.00, 120000.00, 'Cambio de frenos', 'SERVICIO'),
(2, NULL, 3, 1, 65000.00, 65000.00, 'Pastillas de freno', 'PRODUCTO'),
(3, 8, NULL, 1, 25000.00, 25000.00, 'Revisión general', 'SERVICIO'),
(3, 3, NULL, 1, 80000.00, 80000.00, 'Alineación y balanceo', 'SERVICIO'),
(4, 5, NULL, 1, 30000.00, 30000.00, 'Cambio de batería', 'SERVICIO'),
(4, NULL, 4, 1, 250000.00, 250000.00, 'Batería Bosch', 'PRODUCTO'),
(5, 6, NULL, 1, 50000.00, 50000.00, 'Cambio de llantas', 'SERVICIO'),
(5, NULL, 5, 4, 180000.00, 720000.00, 'Llantas Michelin', 'PRODUCTO'); 