package com.talleres.ovycar.dto;

import java.time.LocalDate;

public class IngresoMantenimientoDTO {
    private Long id;
    private LocalDate fecha;
    private String concepto;
    private Double monto;
    private String cliente;
    private String vehiculo;
    private String mecanico;
    private String estado;
    private String semana;
    private Double costoManoObra;
    private Double valorRepuestos;
    private Double porcentajeRepuestos;
    private Double ingresoNeto;
    private Double ingresoAdicional;
    private Double gananciaManoObra;

    // Constructores
    public IngresoMantenimientoDTO() {}

    public IngresoMantenimientoDTO(Long id, LocalDate fecha, String concepto, Double monto, 
                                  String cliente, String vehiculo, String mecanico, 
                                  String estado, String semana, Double costoManoObra,
                                  Double valorRepuestos, Double porcentajeRepuestos, Double ingresoNeto, Double ingresoAdicional, Double gananciaManoObra) {
        this.id = id;
        this.fecha = fecha;
        this.concepto = concepto;
        this.monto = monto;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.mecanico = mecanico;
        this.estado = estado;
        this.semana = semana;
        this.costoManoObra = costoManoObra;
        this.valorRepuestos = valorRepuestos;
        this.porcentajeRepuestos = porcentajeRepuestos;
        this.ingresoNeto = ingresoNeto;
        this.ingresoAdicional = ingresoAdicional;
        this.gananciaManoObra = gananciaManoObra;
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

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
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

    public Double getCostoManoObra() {
        return costoManoObra;
    }

    public void setCostoManoObra(Double costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    public Double getValorRepuestos() {
        return valorRepuestos;
    }

    public void setValorRepuestos(Double valorRepuestos) {
        this.valorRepuestos = valorRepuestos;
    }

    public Double getPorcentajeRepuestos() {
        return porcentajeRepuestos;
    }

    public void setPorcentajeRepuestos(Double porcentajeRepuestos) {
        this.porcentajeRepuestos = porcentajeRepuestos;
    }

    public Double getIngresoNeto() {
        return ingresoNeto;
    }

    public void setIngresoNeto(Double ingresoNeto) {
        this.ingresoNeto = ingresoNeto;
    }

    public Double getIngresoAdicional() {
        return ingresoAdicional;
    }

    public void setIngresoAdicional(Double ingresoAdicional) {
        this.ingresoAdicional = ingresoAdicional;
    }

    public Double getGananciaManoObra() {
        return gananciaManoObra;
    }

    public void setGananciaManoObra(Double gananciaManoObra) {
        this.gananciaManoObra = gananciaManoObra;
    }
}
