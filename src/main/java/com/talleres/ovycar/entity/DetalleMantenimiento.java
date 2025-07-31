package com.talleres.ovycar.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_mantenimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMantenimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mantenimiento_id", nullable = false)
    private Mantenimiento mantenimiento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad = 1;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(length = 200)
    private String descripcion;
    
    @Column(name = "tipo_item")
    @Enumerated(EnumType.STRING)
    private TipoItem tipoItem;
    
    public enum TipoItem {
        SERVICIO, PRODUCTO, OTRO
    }
} 