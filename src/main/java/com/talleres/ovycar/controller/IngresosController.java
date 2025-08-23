package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.IngresoMantenimientoDTO;
import com.talleres.ovycar.dto.ResumenSemanalDTO;
import com.talleres.ovycar.dto.HistorialSemanasDTO;
import com.talleres.ovycar.service.IngresosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ingresos")
public class IngresosController {

    @Autowired
    private IngresosService ingresosService;

    @GetMapping("/historial-semanas")
    public ResponseEntity<HistorialSemanasDTO> getHistorialSemanas() {
        try {
            HistorialSemanasDTO historial = ingresosService.getHistorialSemanas();
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/semana/{semana}")
    public ResponseEntity<ResumenSemanalDTO> getResumenSemana(@PathVariable String semana) {
        try {
            ResumenSemanalDTO resumen = ingresosService.getResumenSemana(semana);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<List<IngresoMantenimientoDTO>> getIngresosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<IngresoMantenimientoDTO> ingresos = ingresosService.getIngresosPorFecha(fechaInicio, fechaFin);
            return ResponseEntity.ok(ingresos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
