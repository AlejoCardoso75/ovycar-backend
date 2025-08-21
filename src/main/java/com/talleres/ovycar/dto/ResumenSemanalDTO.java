package com.talleres.ovycar.dto;

import java.time.LocalDate;
import java.util.List;

public class ResumenSemanalDTO {
    private String semana;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double totalIngresos;
    private Integer cantidadMantenimientos;
    private Double promedioPorMantenimiento;
    private Double crecimientoVsSemanaAnterior;
    private List<IngresoMantenimientoDTO> mantenimientos;

    // Constructores
    public ResumenSemanalDTO() {}

    public ResumenSemanalDTO(String semana, LocalDate fechaInicio, LocalDate fechaFin, 
                            Double totalIngresos, Integer cantidadMantenimientos, 
                            Double promedioPorMantenimiento, Double crecimientoVsSemanaAnterior,
                            List<IngresoMantenimientoDTO> mantenimientos) {
        this.semana = semana;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalIngresos = totalIngresos;
        this.cantidadMantenimientos = cantidadMantenimientos;
        this.promedioPorMantenimiento = promedioPorMantenimiento;
        this.crecimientoVsSemanaAnterior = crecimientoVsSemanaAnterior;
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

    public Double getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(Double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public Integer getCantidadMantenimientos() {
        return cantidadMantenimientos;
    }

    public void setCantidadMantenimientos(Integer cantidadMantenimientos) {
        this.cantidadMantenimientos = cantidadMantenimientos;
    }

    public Double getPromedioPorMantenimiento() {
        return promedioPorMantenimiento;
    }

    public void setPromedioPorMantenimiento(Double promedioPorMantenimiento) {
        this.promedioPorMantenimiento = promedioPorMantenimiento;
    }

    public Double getCrecimientoVsSemanaAnterior() {
        return crecimientoVsSemanaAnterior;
    }

    public void setCrecimientoVsSemanaAnterior(Double crecimientoVsSemanaAnterior) {
        this.crecimientoVsSemanaAnterior = crecimientoVsSemanaAnterior;
    }

    public List<IngresoMantenimientoDTO> getMantenimientos() {
        return mantenimientos;
    }

    public void setMantenimientos(List<IngresoMantenimientoDTO> mantenimientos) {
        this.mantenimientos = mantenimientos;
    }
}
