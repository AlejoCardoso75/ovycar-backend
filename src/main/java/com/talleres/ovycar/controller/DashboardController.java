package com.talleres.ovycar.controller;

import com.talleres.ovycar.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;
    private final ProductoService productoService;
    private final MantenimientoService mantenimientoService;
    private final FacturaService facturaService;
    
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumen() {
        Map<String, Object> resumen = new HashMap<>();
        
        // Estadísticas generales
        resumen.put("totalClientes", clienteService.findAll().size());
        resumen.put("totalVehiculos", vehiculoService.findAll().size());
        resumen.put("totalProductos", productoService.findAll().size());
        
        // Mantenimientos
        resumen.put("mantenimientosProgramados", mantenimientoService.findMantenimientosProgramados().size());
        resumen.put("mantenimientosEnProceso", mantenimientoService.findMantenimientosEnProceso().size());
        
        // Inventario
        resumen.put("productosStockBajo", productoService.findProductosStockBajo().size());
        resumen.put("productosSinStock", productoService.findProductosSinStock().size());
        
        // Facturación
        resumen.put("facturasPendientes", facturaService.findByEstado(com.talleres.ovycar.entity.Factura.EstadoFactura.PENDIENTE).size());
        resumen.put("facturasVencidas", facturaService.findFacturasVencidas().size());
        
        return ResponseEntity.ok(resumen);
    }
    
    @GetMapping("/alertas")
    public ResponseEntity<Map<String, Object>> getAlertas() {
        Map<String, Object> alertas = new HashMap<>();
        
        // Alertas de inventario
        alertas.put("productosStockBajo", productoService.findProductosStockBajo());
        alertas.put("productosSinStock", productoService.findProductosSinStock());
        
        // Alertas de facturación
        alertas.put("facturasVencidas", facturaService.findFacturasVencidas());
        
        // Alertas de mantenimiento
        alertas.put("mantenimientosProgramados", mantenimientoService.findMantenimientosProgramados());
        
        return ResponseEntity.ok(alertas);
    }
    
    @GetMapping("/estadisticas-ventas")
    public ResponseEntity<Map<String, Object>> getEstadisticasVentas(
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("facturasPagadas", facturaService.countFacturasPagadas(inicio, fin));
            estadisticas.put("montoTotal", facturaService.sumTotalFacturasPagadas(inicio, fin));
            
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 