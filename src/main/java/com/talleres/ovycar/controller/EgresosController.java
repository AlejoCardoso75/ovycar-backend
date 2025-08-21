package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.EgresoSemanalDTO;
import com.talleres.ovycar.dto.ResumenEgresoSemanalDTO;
import com.talleres.ovycar.dto.HistorialEgresosSemanasDTO;
import com.talleres.ovycar.service.EgresosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/egresos-reportes")
@CrossOrigin(origins = "*")
public class EgresosController {

    @Autowired
    private EgresosService egresosService;

    @GetMapping("/historial-semanas")
    public ResponseEntity<HistorialEgresosSemanasDTO> getHistorialSemanas() {
        HistorialEgresosSemanasDTO historial = egresosService.getHistorialSemanas();
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/semana/{semana}")
    public ResponseEntity<ResumenEgresoSemanalDTO> getResumenSemana(@PathVariable String semana) {
        ResumenEgresoSemanalDTO resumen = egresosService.getResumenSemana(semana);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/por-fecha")
    public ResponseEntity<List<EgresoSemanalDTO>> getEgresosPorFecha(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        List<EgresoSemanalDTO> egresos = egresosService.getEgresosPorFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(egresos);
    }
}
