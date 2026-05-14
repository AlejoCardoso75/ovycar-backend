package com.talleres.ovycar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo para crear usuario desde administración (mismos campos que init-data / registro).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUsuarioRequestDTO {
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    /** ADMIN o USER (por defecto USER si viene null o vacío). */
    private String rol;
    /** Por defecto true si no se envía. */
    private Boolean activo;
}
