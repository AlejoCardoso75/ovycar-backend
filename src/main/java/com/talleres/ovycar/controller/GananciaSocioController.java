package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.GananciaSocioMantenimientoDTO;
import com.talleres.ovycar.dto.ResumenSemanalSocioDTO;
import com.talleres.ovycar.dto.HistorialSemanasSocioDTO;
import com.talleres.ovycar.service.GananciaSocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ganancia-socio")
@CrossOrigin(origins = "*")
public class GananciaSocioController {

    @Autowired
    private GananciaSocioService gananciaSocioService;

    @GetMapping("/historial-semanas")
    public ResponseEntity<HistorialSemanasSocioDTO> getHistorialSemanas() {
        try {
            HistorialSemanasSocioDTO historial = gananciaSocioService.getHistorialSemanas();
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/semana/{semana}")
    public ResponseEntity<ResumenSemanalSocioDTO> getResumenSemana(@PathVariable String semana) {
        try {
            ResumenSemanalSocioDTO resumen = gananciaSocioService.getResumenSemana(semana);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<List<GananciaSocioMantenimientoDTO>> getGananciaPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<GananciaSocioMantenimientoDTO> ganancias = gananciaSocioService.getGananciaPorFecha(fechaInicio, fechaFin);
            return ResponseEntity.ok(ganancias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
