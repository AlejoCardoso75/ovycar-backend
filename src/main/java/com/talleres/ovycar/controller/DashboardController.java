package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.IngresoMantenimientoDTO;
import com.talleres.ovycar.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;
    private final ProductoService productoService;
    private final MantenimientoService mantenimientoService;
    private final IngresosService ingresosService;
    
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumen() {
        Map<String, Object> resumen = new HashMap<>();
        
        resumen.put("totalClientes", clienteService.findAll().size());
        resumen.put("totalVehiculos", vehiculoService.findAll().size());
        resumen.put("totalProductos", productoService.findAll().size());
        
        resumen.put("mantenimientosProgramados", mantenimientoService.findMantenimientosProgramados().size());
        resumen.put("mantenimientosEnProceso", mantenimientoService.findMantenimientosEnProceso().size());
        
        resumen.put("productosStockBajo", productoService.findProductosStockBajo().size());
        resumen.put("productosSinStock", productoService.findProductosSinStock().size());
        
        return ResponseEntity.ok(resumen);
    }
    
    @GetMapping("/alertas")
    public ResponseEntity<Map<String, Object>> getAlertas() {
        Map<String, Object> alertas = new HashMap<>();
        
        alertas.put("productosStockBajo", productoService.findProductosStockBajo());
        alertas.put("productosSinStock", productoService.findProductosSinStock());
        alertas.put("mantenimientosProgramados", mantenimientoService.findMantenimientosProgramados());
        
        return ResponseEntity.ok(alertas);
    }
    
    /** Ingresos por mantenimientos completados en el rango (reemplaza estadísticas por facturas). */
    @GetMapping("/estadisticas-ventas")
    public ResponseEntity<Map<String, Object>> getEstadisticasVentas(
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            List<IngresoMantenimientoDTO> ingresos = ingresosService.getIngresosPorFecha(
                    inicio.toLocalDate(), fin.toLocalDate());
            double montoTotal = ingresos.stream()
                    .mapToDouble(i -> i.getMonto() != null ? i.getMonto() : 0.0)
                    .sum();
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("ingresosRegistrados", ingresos.size());
            estadisticas.put("montoTotal", montoTotal);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
