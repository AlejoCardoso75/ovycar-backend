package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.ProductoDTO;
import com.talleres.ovycar.entity.Producto;
import com.talleres.ovycar.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    
    public List<ProductoDTO> findAll() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ProductoDTO> findById(Long id) {
        return productoRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<ProductoDTO> findByCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .map(this::convertToDTO);
    }
    
    public List<ProductoDTO> findByNombreContaining(String termino) {
        return productoRepository.findByNombreContainingOrCodigoContainingOrCategoriaContainingOrMarcaContaining(termino, termino, termino, termino)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductoDTO> findByCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductoDTO> findByMarca(String marca) {
        return productoRepository.findByMarca(marca)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductoDTO> findProductosStockBajo() {
        return productoRepository.findProductosStockBajo()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductoDTO> findProductosSinStock() {
        return productoRepository.findProductosSinStock()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ProductoDTO save(Producto producto) {
        if (producto.getId() == null && productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new RuntimeException("Ya existe un producto con el código: " + producto.getCodigo());
        }
        return convertToDTO(productoRepository.save(producto));
    }
    
    public void deleteById(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            producto.get().setActivo(false);
            productoRepository.save(producto.get());
        }
    }
    
    public void actualizarStock(Long id, Integer cantidad) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            Producto prod = producto.get();
            prod.setStock(prod.getStock() + cantidad);
            productoRepository.save(prod);
        }
    }
    
    private ProductoDTO convertToDTO(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getCodigo(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getCategoria(),
                producto.getMarca(),
                producto.getFechaRegistro(),
                producto.getActivo()
        );
    }
} 