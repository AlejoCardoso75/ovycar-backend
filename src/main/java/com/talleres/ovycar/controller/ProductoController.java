package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.ProductoDTO;
import com.talleres.ovycar.entity.Producto;
import com.talleres.ovycar.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    
    private final ProductoService productoService;
    
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoDTO> getProductoByCodigo(@PathVariable String codigo) {
        return productoService.findByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(@RequestParam String termino) {
        return ResponseEntity.ok(productoService.findByNombreContaining(termino));
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoDTO>> getProductosByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.findByCategoria(categoria));
    }
    
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<ProductoDTO>> getProductosByMarca(@PathVariable String marca) {
        return ResponseEntity.ok(productoService.findByMarca(marca));
    }
    
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoDTO>> getProductosStockBajo() {
        return ResponseEntity.ok(productoService.findProductosStockBajo());
    }
    
    @GetMapping("/sin-stock")
    public ResponseEntity<List<ProductoDTO>> getProductosSinStock() {
        return ResponseEntity.ok(productoService.findProductosSinStock());
    }
    
    @PostMapping
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody Producto producto) {
        try {
            ProductoDTO savedProducto = productoService.save(producto);
            return ResponseEntity.ok(savedProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        try {
            ProductoDTO updatedProducto = productoService.save(producto);
            return ResponseEntity.ok(updatedProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 