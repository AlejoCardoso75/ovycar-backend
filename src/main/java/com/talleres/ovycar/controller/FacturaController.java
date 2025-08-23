package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.FacturaDTO;
import com.talleres.ovycar.entity.Factura;
import com.talleres.ovycar.service.FacturaService;
import com.talleres.ovycar.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {
    
    private final FacturaService facturaService;
    private final PdfService pdfService;
    
    @GetMapping
    public ResponseEntity<List<FacturaDTO>> getAllFacturas() {
        return ResponseEntity.ok(facturaService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getFacturaById(@PathVariable Long id) {
        return facturaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/numero/{numeroFactura}")
    public ResponseEntity<FacturaDTO> getFacturaByNumero(@PathVariable String numeroFactura) {
        return facturaService.findByNumeroFactura(numeroFactura)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<FacturaDTO>> getFacturasByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(facturaService.findByClienteId(clienteId));
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<FacturaDTO>> getFacturasByEstado(@PathVariable String estado) {
        try {
            Factura.EstadoFactura estadoEnum = Factura.EstadoFactura.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(facturaService.findByEstado(estadoEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/vencidas")
    public ResponseEntity<List<FacturaDTO>> getFacturasVencidas() {
        return ResponseEntity.ok(facturaService.findFacturasVencidas());
    }
    
    @GetMapping("/estadisticas")
    public ResponseEntity<Object> getEstadisticas(@RequestParam String fechaInicio, @RequestParam String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            
            Long facturasPagadas = facturaService.countFacturasPagadas(inicio, fin);
            Double totalPagado = facturaService.sumTotalFacturasPagadas(inicio, fin);
            
            return ResponseEntity.ok(new Object() {
                public final Long totalFacturasPagadas = facturasPagadas;
                public final Double montoTotalPagado = totalPagado;
            });
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<FacturaDTO>> buscarFacturas(@RequestParam String termino) {
        return ResponseEntity.ok(facturaService.buscarFacturas(termino));
    }
    
    @PostMapping
    public ResponseEntity<FacturaDTO> createFactura(@RequestBody Factura factura) {
        try {
            FacturaDTO savedFactura = facturaService.save(factura);
            return ResponseEntity.ok(savedFactura);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FacturaDTO> updateFactura(@PathVariable Long id, @RequestBody Factura factura) {
        factura.setId(id);
        try {
            FacturaDTO updatedFactura = facturaService.save(factura);
            return ResponseEntity.ok(updatedFactura);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/pagar")
    public ResponseEntity<Void> marcarComoPagada(@PathVariable Long id) {
        facturaService.marcarComoPagada(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarFactura(@PathVariable Long id) {
        facturaService.cancelarFactura(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(@PathVariable Long id) {
        try {
            facturaService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPdfFactura(@PathVariable Long id) {
        try {
            FacturaDTO factura = facturaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
            
            byte[] pdfBytes = pdfService.generarFacturaPdf(factura);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                "factura_" + factura.getNumeroFactura() + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 