package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String username;
    private String nombre;
    private String apellido;
    private String rol;
    private String message;
    private boolean success;
}
