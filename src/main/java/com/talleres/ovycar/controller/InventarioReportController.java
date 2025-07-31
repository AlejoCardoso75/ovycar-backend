package com.talleres.ovycar.controller;

import com.talleres.ovycar.service.InventarioReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class InventarioReportController {

    @Autowired
    private InventarioReportService inventarioReportService;

    @GetMapping("/inventario/pdf")
    public ResponseEntity<byte[]> generarInventarioPdf() {
        try {
            byte[] pdfBytes = inventarioReportService.generarInventarioPdf();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "inventario_ovycar.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/inventario/excel")
    public ResponseEntity<byte[]> generarInventarioExcel() {
        try {
            byte[] excelBytes = inventarioReportService.generarInventarioExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "inventario_ovycar.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 