package com.talleres.ovycar.dto;

import java.time.LocalDate;
import java.util.List;

public class ResumenSemanalSocioDTO {
    private String semana;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double ingresosNetos;
    private Double egresos;
    private Double gananciaSocio;
    private Integer cantidadMantenimientos;
    private List<GananciaSocioMantenimientoDTO> mantenimientos;

    // Constructores
    public ResumenSemanalSocioDTO() {}

    public ResumenSemanalSocioDTO(String semana, LocalDate fechaInicio, LocalDate fechaFin, 
                                 Double ingresosNetos, Double egresos, Double gananciaSocio, 
                                 Integer cantidadMantenimientos, List<GananciaSocioMantenimientoDTO> mantenimientos) {
        this.semana = semana;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ingresosNetos = ingresosNetos;
        this.egresos = egresos;
        this.gananciaSocio = gananciaSocio;
        this.cantidadMantenimientos = cantidadMantenimientos;
        this.mantenimientos = mantenimientos;
    }

    // Getters y Setters
    public String getSemana() {
        return semana;
    }

    public void setSemana(String semana) {
        this.semana = semana;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Double getIngresosNetos() {
        return ingresosNetos;
    }

    public void setIngresosNetos(Double ingresosNetos) {
        this.ingresosNetos = ingresosNetos;
    }

    public Double getEgresos() {
        return egresos;
    }

    public void setEgresos(Double egresos) {
        this.egresos = egresos;
    }

    public Double getGananciaSocio() {
        return gananciaSocio;
    }

    public void setGananciaSocio(Double gananciaSocio) {
        this.gananciaSocio = gananciaSocio;
    }

    public Integer getCantidadMantenimientos() {
        return cantidadMantenimientos;
    }

    public void setCantidadMantenimientos(Integer cantidadMantenimientos) {
        this.cantidadMantenimientos = cantidadMantenimientos;
    }

    public List<GananciaSocioMantenimientoDTO> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(List<GananciaSocioMantenimientoDTO> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }
}
