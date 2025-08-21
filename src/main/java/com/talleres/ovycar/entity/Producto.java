package com.talleres.ovycar.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(nullable = false, length = 50)
    private String codigo;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(length = 50)
    private String categoria;
    
    @Column(length = 50)
    private String marca;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
} 