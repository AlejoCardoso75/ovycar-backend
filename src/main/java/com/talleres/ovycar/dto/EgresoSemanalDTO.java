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
public class EgresoSemanalDTO {
    private Long id;
    private LocalDateTime fechaEgreso;
    private String concepto;
    private BigDecimal monto;
    private String categoria;
    private String responsable;
    private String semana;
}
