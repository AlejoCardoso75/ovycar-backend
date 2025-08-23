package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.ClienteDTO;
import com.talleres.ovycar.entity.Cliente;
import com.talleres.ovycar.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        return ResponseEntity.ok(clienteService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(@PathVariable Long id) {
        return clienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/documento/{documento}")
    public ResponseEntity<ClienteDTO> getClienteByDocumento(@PathVariable String documento) {
        return clienteService.findByDocumento(documento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscarClientes(@RequestParam String nombre) {
        return ResponseEntity.ok(clienteService.findByNombreContaining(nombre));
    }
    
    @PostMapping
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente) {
        try {
            ClienteDTO savedCliente = clienteService.save(cliente);
            return ResponseEntity.ok(savedCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        cliente.setId(id);
        try {
            ClienteDTO updatedCliente = clienteService.save(cliente);
            return ResponseEntity.ok(updatedCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 