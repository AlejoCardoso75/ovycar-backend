package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MantenimientoDTO {
    private Long id;
    private Long vehiculoId;
    private String vehiculoPlaca;
    private String vehiculoMarca;
    private String vehiculoModelo;
    private Long clienteId;
    private String clienteNombre;
    private String tipoMantenimiento;
    private String descripcion;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private Integer kilometrajeActual;
    private Integer kilometrajeProximo;
    private String observaciones;
    private Double costo;
    private String mecanico;
    private LocalDateTime fechaRegistro;
    private List<DetalleMantenimientoDTO> detalles;
} 