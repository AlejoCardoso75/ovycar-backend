package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenEgresoSemanalDTO {
    private String semana;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal totalEgresos;
    private int cantidadEgresos;
    private BigDecimal promedioPorEgreso;
    private BigDecimal crecimientoVsSemanaAnterior;
    private List<EgresoSemanalDTO> egresos;
}
