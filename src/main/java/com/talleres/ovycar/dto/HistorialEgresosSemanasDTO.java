package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEgresosSemanasDTO {
    private List<ResumenEgresoSemanalDTO> semanas;
    private BigDecimal totalGeneral;
    private BigDecimal promedioSemanal;
    private BigDecimal crecimientoPromedio;
}
