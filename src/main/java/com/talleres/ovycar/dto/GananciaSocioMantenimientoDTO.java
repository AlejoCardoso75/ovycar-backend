package com.talleres.ovycar.dto;

import java.time.LocalDate;

public class GananciaSocioMantenimientoDTO {
    private Long id;
    private LocalDate fecha;
    private String concepto;
    private String cliente;
    private String vehiculo;
    private String mecanico;
    private String estado;
    private String semana;
    private Double ingresoNeto;

    // Constructores
    public GananciaSocioMantenimientoDTO() {}

    public GananciaSocioMantenimientoDTO(Long id, LocalDate fecha, String concepto, 
                                        String cliente, String vehiculo, String mecanico, 
                                        String estado, String semana, Double ingresoNeto) {
        this.id = id;
        this.fecha = fecha;
        this.concepto = concepto;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.mecanico = mecanico;
        this.estado = estado;
        this.semana = semana;
        this.ingresoNeto = ingresoNeto;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getMecanico() {
        return mecanico;
    }

    public void setMecanico(String mecanico) {
        this.mecanico = mecanico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSemana() {
        return semana;
    }

    public void setSemana(String semana) {
        this.semana = semana;
    }

    public Double getIngresoNeto() {
        return ingresoNeto;
    }

    public void setIngresoNeto(Double ingresoNeto) {
        this.ingresoNeto = ingresoNeto;
    }
}
