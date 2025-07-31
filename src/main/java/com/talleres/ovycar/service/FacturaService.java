package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.FacturaDTO;
import com.talleres.ovycar.dto.DetalleFacturaDTO;
import com.talleres.ovycar.entity.Factura;
import com.talleres.ovycar.entity.DetalleFactura;
import com.talleres.ovycar.repository.FacturaRepository;
import com.talleres.ovycar.repository.ClienteRepository;
import com.talleres.ovycar.repository.MantenimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FacturaService {
    
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final MantenimientoRepository mantenimientoRepository;
    
    public List<FacturaDTO> findAll() {
        return facturaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<FacturaDTO> findById(Long id) {
        return facturaRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<FacturaDTO> findByNumeroFactura(String numeroFactura) {
        return facturaRepository.findByNumeroFactura(numeroFactura)
                .map(this::convertToDTO);
    }
    
    public List<FacturaDTO> findByClienteId(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<FacturaDTO> findByEstado(Factura.EstadoFactura estado) {
        return facturaRepository.findByEstado(estado)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<FacturaDTO> findFacturasVencidas() {
        return facturaRepository.findFacturasVencidas(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Long countFacturasPagadas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepository.countFacturasPagadas(fechaInicio, fechaFin);
    }
    
    public Double sumTotalFacturasPagadas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return facturaRepository.sumTotalFacturasPagadas(fechaInicio, fechaFin);
    }
    
    public FacturaDTO save(Factura factura) {
        if (factura.getId() == null) {
            factura.setNumeroFactura(generarNumeroFactura());
        }
        return convertToDTO(facturaRepository.save(factura));
    }
    
    public void marcarComoPagada(Long id) {
        Optional<Factura> factura = facturaRepository.findById(id);
        if (factura.isPresent()) {
            Factura fac = factura.get();
            fac.setEstado(Factura.EstadoFactura.PAGADA);
            facturaRepository.save(fac);
        }
    }
    
    public void cancelarFactura(Long id) {
        Optional<Factura> factura = facturaRepository.findById(id);
        if (factura.isPresent()) {
            Factura fac = factura.get();
            fac.setEstado(Factura.EstadoFactura.CANCELADA);
            facturaRepository.save(fac);
        }
    }
    
    public List<FacturaDTO> buscarFacturas(String termino) {
        return facturaRepository.buscarFacturas(termino)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void deleteById(Long id) {
        facturaRepository.deleteById(id);
    }
    
    private String generarNumeroFactura() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String hora = String.format("%02d%02d%02d", now.getHour(), now.getMinute(), now.getSecond());
        return "FAC-" + fecha + "-" + hora;
    }
    
    private FacturaDTO convertToDTO(Factura factura) {
        return new FacturaDTO(
                factura.getId(),
                factura.getNumeroFactura(),
                factura.getCliente().getId(),
                factura.getCliente().getNombre() + " " + factura.getCliente().getApellido(),
                factura.getMantenimiento() != null ? factura.getMantenimiento().getId() : null,
                factura.getFechaEmision(),
                factura.getFechaVencimiento(),
                factura.getSubtotal(),
                factura.getImpuestos(),
                factura.getDescuento(),
                factura.getTotal(),
                factura.getEstado().toString(),
                factura.getObservaciones(),
                factura.getFechaRegistro(),
                factura.getDetalles() != null ? 
                    factura.getDetalles().stream()
                        .map(this::convertDetalleToDTO)
                        .collect(Collectors.toList()) : null
        );
    }
    
    private DetalleFacturaDTO convertDetalleToDTO(DetalleFactura detalle) {
        return new DetalleFacturaDTO(
                detalle.getId(),
                detalle.getFactura().getId(),
                detalle.getServicio() != null ? detalle.getServicio().getId() : null,
                detalle.getServicio() != null ? detalle.getServicio().getNombre() : null,
                detalle.getProducto() != null ? detalle.getProducto().getId() : null,
                detalle.getProducto() != null ? detalle.getProducto().getNombre() : null,
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal(),
                detalle.getDescripcion(),
                detalle.getTipoItem().toString()
        );
    }
} 