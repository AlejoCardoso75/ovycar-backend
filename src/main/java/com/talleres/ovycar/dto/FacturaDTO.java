package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Long id;
    private String numeroFactura;
    private Long clienteId;
    private String clienteNombre;
    private Long mantenimientoId;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaVencimiento;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal descuento;
    private BigDecimal total;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaRegistro;
    private List<DetalleFacturaDTO> detalles;
} 