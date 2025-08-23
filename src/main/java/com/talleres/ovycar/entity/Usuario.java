package com.talleres.ovycar.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "apellido", nullable = false)
    private String apellido;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "rol", nullable = false)
    private String rol; // ADMIN, USER, etc.
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }
}
