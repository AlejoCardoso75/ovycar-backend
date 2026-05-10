package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.CreateMecanicoDTO;
import com.talleres.ovycar.dto.MecanicoDTO;
import com.talleres.ovycar.service.MecanicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mecanicos")
@RequiredArgsConstructor
public class MecanicoController {

    private final MecanicoService mecanicoService;

    @GetMapping
    public ResponseEntity<List<MecanicoDTO>> getAllMecanicos() {
        return ResponseEntity.ok(mecanicoService.findAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MecanicoDTO>> getMecanicosActivos() {
        return ResponseEntity.ok(mecanicoService.findActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MecanicoDTO> getMecanicoById(@PathVariable Long id) {
        return mecanicoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createMecanico(@RequestBody CreateMecanicoDTO dto) {
        try {
            return ResponseEntity.ok(mecanicoService.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMecanico(@PathVariable Long id, @RequestBody CreateMecanicoDTO dto) {
        try {
            return ResponseEntity.ok(mecanicoService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMecanico(@PathVariable Long id) {
        try {
            mecanicoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}
