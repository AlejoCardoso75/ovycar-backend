package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EgresoDTO {
    private Long id;
    private String concepto;
    private String descripcion;
    private BigDecimal monto;
    private String categoria;
    private LocalDateTime fechaEgreso;
    private LocalDateTime fechaRegistro;
    private String responsable;
    private Boolean activo;
}
