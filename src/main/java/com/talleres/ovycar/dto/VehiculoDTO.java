package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String placa;
    private String marca;
    private String modelo;
    private String anio;
    private String color;
    private String numeroVin;
    private Integer kilometraje;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
} 