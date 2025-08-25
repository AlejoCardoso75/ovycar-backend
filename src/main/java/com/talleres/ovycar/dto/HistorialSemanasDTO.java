package com.talleres.ovycar.dto;

import java.util.List;

public class HistorialSemanasDTO {
    private List<ResumenSemanalDTO> semanas;
    private Double totalGeneral;
    private Double promedioSemanal;
    private Double crecimientoPromedio;
    private Double totalGananciasNetas;

    // Constructores
    public HistorialSemanasDTO() {}

    public HistorialSemanasDTO(List<ResumenSemanalDTO> semanas, Double totalGeneral, 
                              Double promedioSemanal, Double crecimientoPromedio, Double totalGananciasNetas) {
        this.semanas = semanas;
        this.totalGeneral = totalGeneral;
        this.promedioSemanal = promedioSemanal;
        this.crecimientoPromedio = crecimientoPromedio;
        this.totalGananciasNetas = totalGananciasNetas;
    }

    // Getters y Setters
    public List<ResumenSemanalDTO> getSemanas() {
        return semanas;
    }

    public void setSemanas(List<ResumenSemanalDTO> semanas) {
        this.semanas = semanas;
    }

    public Double getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(Double totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public Double getPromedioSemanal() {
        return promedioSemanal;
    }

    public void setPromedioSemanal(Double promedioSemanal) {
        this.promedioSemanal = promedioSemanal;
    }

    public Double getCrecimientoPromedio() {
        return crecimientoPromedio;
    }

    public void setCrecimientoPromedio(Double crecimientoPromedio) {
        this.crecimientoPromedio = crecimientoPromedio;
    }

    public Double getTotalGananciasNetas() {
        return totalGananciasNetas;
    }

    public void setTotalGananciasNetas(Double totalGananciasNetas) {
        this.totalGananciasNetas = totalGananciasNetas;
    }
}
