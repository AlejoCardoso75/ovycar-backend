package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMantenimientoDTO {
    private Long vehiculoId;
    private String tipoMantenimiento;
    private String descripcion;
    private LocalDate fechaProgramada;
    private String estado;
    private Integer kilometrajeActual;
    private String observaciones;
    private Double costo;
    private String mecanico;
}
