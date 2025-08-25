package com.talleres.ovycar.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mantenimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mantenimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(nullable = false, length = 100)
    private String tipoMantenimiento;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;
    
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMantenimiento estado = EstadoMantenimiento.PROGRAMADO;
    
    @Column(name = "kilometraje_actual")
    private Integer kilometrajeActual;
    
    @Column(name = "kilometraje_proximo")
    private Integer kilometrajeProximo;
    
    @Column(length = 200)
    private String observaciones;
    
    @Column(name = "costo")
    private Double costo;
    
    @Column(name = "costo_mano_obra")
    private Double costoManoObra;
    
    @Column(name = "valor_repuestos")
    private Double valorRepuestos;
    
    @Column(name = "costo_adicionales")
    private Double costoAdicionales;
    
    @Column(name = "proveedor_repuestos", length = 50)
    private String proveedorRepuestos;
    
    @Column(name = "garantia", length = 100)
    private String garantia;
    
    @Column(name = "mecanico", length = 100)
    private String mecanico;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @OneToMany(mappedBy = "mantenimiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleMantenimiento> detalles;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
    
    public enum EstadoMantenimiento {
        PROGRAMADO, EN_PROCESO, COMPLETADO, CANCELADO
    }
} 