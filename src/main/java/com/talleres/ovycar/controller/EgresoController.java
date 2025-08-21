package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.EgresoDTO;
import com.talleres.ovycar.dto.CreateEgresoDTO;
import com.talleres.ovycar.service.EgresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/egresos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EgresoController {
    
    private final EgresoService egresoService;
    
    @GetMapping
    public ResponseEntity<List<EgresoDTO>> getAllEgresos() {
        return ResponseEntity.ok(egresoService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EgresoDTO> getEgresoById(@PathVariable Long id) {
        return egresoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EgresoDTO> createEgreso(@RequestBody CreateEgresoDTO createEgresoDTO) {
        EgresoDTO createdEgreso = egresoService.createEgreso(createEgresoDTO);
        return ResponseEntity.ok(createdEgreso);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EgresoDTO> updateEgreso(@PathVariable Long id, @RequestBody CreateEgresoDTO createEgresoDTO) {
        return egresoService.updateEgreso(id, createEgresoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEgreso(@PathVariable Long id) {
        boolean deleted = egresoService.deleteEgreso(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
