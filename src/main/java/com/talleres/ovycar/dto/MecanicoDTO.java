package com.talleres.ovycar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MecanicoDTO {
    private Long id;
    private String nombre;
    private Double porcentajeGanancia;
    private Boolean activo;
}
