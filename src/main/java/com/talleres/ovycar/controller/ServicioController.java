package com.talleres.ovycar.controller;

import com.talleres.ovycar.entity.Servicio;
import com.talleres.ovycar.service.ServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServicioController {
    
    private final ServicioService servicioService;
    
    @GetMapping
    public ResponseEntity<List<Servicio>> getAllServicios() {
        return ResponseEntity.ok(servicioService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getServicioById(@PathVariable Long id) {
        return servicioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Servicio>> buscarServicios(@RequestParam String nombre) {
        return ResponseEntity.ok(servicioService.findByNombreContaining(nombre));
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Servicio>> getServiciosByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(servicioService.findByCategoria(categoria));
    }
    
    @GetMapping("/precio")
    public ResponseEntity<List<Servicio>> getServiciosByPrecio(@RequestParam Double precioMin, @RequestParam Double precioMax) {
        return ResponseEntity.ok(servicioService.findByPrecioBetween(precioMin, precioMax));
    }
    
    @PostMapping
    public ResponseEntity<Servicio> createServicio(@RequestBody Servicio servicio) {
        Servicio savedServicio = servicioService.save(servicio);
        return ResponseEntity.ok(savedServicio);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Servicio> updateServicio(@PathVariable Long id, @RequestBody Servicio servicio) {
        servicio.setId(id);
        Servicio updatedServicio = servicioService.save(servicio);
        return ResponseEntity.ok(updatedServicio);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicio(@PathVariable Long id) {
        servicioService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 