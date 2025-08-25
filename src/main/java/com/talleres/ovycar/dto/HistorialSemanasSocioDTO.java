package com.talleres.ovycar.dto;

import java.util.List;

public class HistorialSemanasSocioDTO {
    private List<ResumenSemanalSocioDTO> semanas;
    private Double totalIngresosNetos;
    private Double totalEgresos;
    private Double totalGananciaSocio;
    private Double promedioSemanal;

    // Constructores
    public HistorialSemanasSocioDTO() {}

    public HistorialSemanasSocioDTO(List<ResumenSemanalSocioDTO> semanas, Double totalIngresosNetos, 
                                   Double totalEgresos, Double totalGananciaSocio, Double promedioSemanal) {
        this.semanas = semanas;
        this.totalIngresosNetos = totalIngresosNetos;
        this.totalEgresos = totalEgresos;
        this.totalGananciaSocio = totalGananciaSocio;
        this.promedioSemanal = promedioSemanal;
    }

    // Getters y Setters
    public List<ResumenSemanalSocioDTO> getSemanas() {
        return semanas;
    }

    public void setSemanas(List<ResumenSemanalSocioDTO> semanas) {
        this.semanas = semanas;
    }

    public Double getTotalIngresosNetos() {
        return totalIngresosNetos;
    }

    public void setTotalIngresosNetos(Double totalIngresosNetos) {
        this.totalIngresosNetos = totalIngresosNetos;
    }

    public Double getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(Double totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public Double getTotalGananciaSocio() {
        return totalGananciaSocio;
    }

    public void setTotalGananciaSocio(Double totalGananciaSocio) {
        this.totalGananciaSocio = totalGananciaSocio;
    }

    public Double getPromedioSemanal() {
        return promedioSemanal;
    }

    public void setPromedioSemanal(Double promedioSemanal) {
        this.promedioSemanal = promedioSemanal;
    }
}
