package com.talleres.ovycar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "usuario", nullable = false, length = 80)
    private String usuario;

    @Column(name = "modulo", nullable = false, length = 40)
    private String modulo;

    @Column(name = "accion", nullable = false, length = 40)
    private String accion;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "resumen", nullable = false, length = 2048)
    private String resumen;

    @PrePersist
    protected void onPersist() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }
}
