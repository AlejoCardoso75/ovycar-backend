package com.talleres.ovycar.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "egresos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Egreso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "concepto", nullable = false)
    private String concepto;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Column(name = "categoria")
    private String categoria;
    
    @Column(name = "fecha_egreso", nullable = false)
    private LocalDateTime fechaEgreso;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(name = "responsable")
    private String responsable;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (fechaEgreso == null) {
            fechaEgreso = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}
