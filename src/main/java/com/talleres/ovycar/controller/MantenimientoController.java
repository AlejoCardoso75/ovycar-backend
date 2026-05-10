package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.MantenimientoDTO;
import com.talleres.ovycar.dto.CreateMantenimientoDTO;
import com.talleres.ovycar.entity.Mantenimiento;
import com.talleres.ovycar.service.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {
    
    private final MantenimientoService mantenimientoService;
    
    @GetMapping
    public ResponseEntity<List<MantenimientoDTO>> getAllMantenimientos() {
        return ResponseEntity.ok(mantenimientoService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MantenimientoDTO> getMantenimientoById(@PathVariable Long id) {
        return mantenimientoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<MantenimientoDTO> getMantenimientoByIdWithDetails(@PathVariable Long id) {
        return mantenimientoService.findByIdWithDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<MantenimientoDTO>> getMantenimientosByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(mantenimientoService.findByClienteId(clienteId));
    }
    
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<MantenimientoDTO>> getMantenimientosByVehiculo(@PathVariable Long vehiculoId) {
        return ResponseEntity.ok(mantenimientoService.findByVehiculoId(vehiculoId));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MantenimientoDTO>> getMantenimientosByEstado(@PathVariable String estado) {
        try {
            Mantenimiento.EstadoMantenimiento estadoEnum = Mantenimiento.EstadoMantenimiento.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(mantenimientoService.findByEstado(estadoEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/programados")
    public ResponseEntity<List<MantenimientoDTO>> getMantenimientosProgramados() {
        return ResponseEntity.ok(mantenimientoService.findMantenimientosProgramados());
    }
    
    @GetMapping("/en-proceso")
    public ResponseEntity<List<MantenimientoDTO>> getMantenimientosEnProceso() {
        return ResponseEntity.ok(mantenimientoService.findMantenimientosEnProceso());
    }
    
    @GetMapping("/completados")
    public ResponseEntity<List<MantenimientoDTO>> getMantenimientosCompletados() {
        return ResponseEntity.ok(mantenimientoService.findMantenimientosCompletados());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<MantenimientoDTO>> buscarMantenimientos(@RequestParam String termino) {
        return ResponseEntity.ok(mantenimientoService.buscarMantenimientos(termino));
    }
    
    @GetMapping("/historial/cliente/{clienteId}")
    public ResponseEntity<List<MantenimientoDTO>> getHistorialCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(mantenimientoService.findHistorialCliente(clienteId));
    }
    
    @GetMapping("/historial/vehiculo/{vehiculoId}")
    public ResponseEntity<List<MantenimientoDTO>> getHistorialVehiculo(@PathVariable Long vehiculoId) {
        return ResponseEntity.ok(mantenimientoService.findHistorialVehiculo(vehiculoId));
    }
    
    @PostMapping
    public ResponseEntity<MantenimientoDTO> createMantenimiento(@RequestBody CreateMantenimientoDTO createMantenimientoDTO) {
        try {
            MantenimientoDTO savedMantenimiento = mantenimientoService.createMantenimiento(createMantenimientoDTO);
            return ResponseEntity.ok(savedMantenimiento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MantenimientoDTO> updateMantenimiento(@PathVariable Long id, @RequestBody CreateMantenimientoDTO updateMantenimientoDTO) {
        try {
            MantenimientoDTO updatedMantenimiento = mantenimientoService.updateFromDTO(id, updateMantenimientoDTO);
            return ResponseEntity.ok(updatedMantenimiento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<MantenimientoDTO> iniciarMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.iniciarMantenimiento(id));
    }
    
    @PutMapping("/{id}/completar")
    public ResponseEntity<MantenimientoDTO> completarMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.completarMantenimiento(id));
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<MantenimientoDTO> cancelarMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(mantenimientoService.cancelarMantenimiento(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMantenimiento(@PathVariable Long id) {
        try {
            mantenimientoService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
