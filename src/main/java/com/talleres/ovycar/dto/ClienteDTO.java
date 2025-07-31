package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    private String email;
    private String direccion;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
} 