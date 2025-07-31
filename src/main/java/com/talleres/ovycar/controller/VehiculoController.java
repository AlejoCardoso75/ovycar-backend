package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.VehiculoDTO;
import com.talleres.ovycar.dto.CreateVehiculoDTO;
import com.talleres.ovycar.entity.Vehiculo;
import com.talleres.ovycar.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehiculoController {
    
    private final VehiculoService vehiculoService;
    
    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> getAllVehiculos() {
        return ResponseEntity.ok(vehiculoService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDTO> getVehiculoById(@PathVariable Long id) {
        return vehiculoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/placa/{placa}")
    public ResponseEntity<VehiculoDTO> getVehiculoByPlaca(@PathVariable String placa) {
        return vehiculoService.findByPlaca(placa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VehiculoDTO>> getVehiculosByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(vehiculoService.findByClienteId(clienteId));
    }
    
    @GetMapping("/buscar/placa")
    public ResponseEntity<List<VehiculoDTO>> buscarPorPlaca(@RequestParam String placa) {
        return ResponseEntity.ok(vehiculoService.findByPlacaContaining(placa));
    }
    
    @GetMapping("/buscar/marca")
    public ResponseEntity<List<VehiculoDTO>> buscarPorMarca(@RequestParam String marca) {
        return ResponseEntity.ok(vehiculoService.findByMarcaContaining(marca));
    }
    
    @PostMapping
    public ResponseEntity<VehiculoDTO> createVehiculo(@RequestBody CreateVehiculoDTO createVehiculoDTO) {
        try {
            VehiculoDTO savedVehiculo = vehiculoService.createFromDTO(createVehiculoDTO);
            return ResponseEntity.ok(savedVehiculo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> updateVehiculo(@PathVariable Long id, @RequestBody CreateVehiculoDTO createVehiculoDTO) {
        try {
            VehiculoDTO updatedVehiculo = vehiculoService.updateFromDTO(id, createVehiculoDTO);
            return ResponseEntity.ok(updatedVehiculo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Long id) {
        vehiculoService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 