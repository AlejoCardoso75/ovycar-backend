package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehiculoDTO {
    private Long clienteId;
    private String placa;
    private String marca;
    private String modelo;
    private String anio;
    private String color;
    private String numeroVin;
    private Integer kilometraje;
} 