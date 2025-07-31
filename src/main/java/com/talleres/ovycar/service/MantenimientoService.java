package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.MantenimientoDTO;
import com.talleres.ovycar.dto.DetalleMantenimientoDTO;
import com.talleres.ovycar.entity.Mantenimiento;
import com.talleres.ovycar.entity.DetalleMantenimiento;
import com.talleres.ovycar.repository.MantenimientoRepository;
import com.talleres.ovycar.repository.ClienteRepository;
import com.talleres.ovycar.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.talleres.ovycar.entity.Vehiculo;

@Service
@RequiredArgsConstructor
@Transactional
public class MantenimientoService {
    
    private final MantenimientoRepository mantenimientoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    
    public List<MantenimientoDTO> findAll() {
        return mantenimientoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<MantenimientoDTO> findById(Long id) {
        return mantenimientoRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public List<MantenimientoDTO> findByClienteId(Long clienteId) {
        return mantenimientoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findByVehiculoId(Long vehiculoId) {
        return mantenimientoRepository.findByVehiculoId(vehiculoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findByEstado(Mantenimiento.EstadoMantenimiento estado) {
        return mantenimientoRepository.findByEstado(estado)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findMantenimientosProgramados() {
        return mantenimientoRepository.findMantenimientosProgramados(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findMantenimientosEnProceso() {
        return mantenimientoRepository.findMantenimientosEnProceso()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findMantenimientosCompletados() {
        return mantenimientoRepository.findMantenimientosCompletados()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> buscarMantenimientos(String termino) {
        return mantenimientoRepository.buscarMantenimientos(termino)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findHistorialCliente(Long clienteId) {
        return mantenimientoRepository.findHistorialCliente(clienteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findHistorialVehiculo(Long vehiculoId) {
        return mantenimientoRepository.findHistorialVehiculo(vehiculoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public MantenimientoDTO save(Mantenimiento mantenimiento) {
        // Si es un mantenimiento nuevo, obtener el cliente del vehículo
        if (mantenimiento.getId() == null) {
            Vehiculo vehiculo = vehiculoRepository.findById(mantenimiento.getVehiculo().getId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            mantenimiento.setCliente(vehiculo.getCliente());
        } else {
            // Para actualizaciones, obtener el mantenimiento existente y actualizar solo los campos necesarios
            Mantenimiento existingMantenimiento = mantenimientoRepository.findById(mantenimiento.getId())
                    .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
            
            existingMantenimiento.setTipoMantenimiento(mantenimiento.getTipoMantenimiento());
            existingMantenimiento.setDescripcion(mantenimiento.getDescripcion());
            existingMantenimiento.setFechaProgramada(mantenimiento.getFechaProgramada());
            existingMantenimiento.setEstado(mantenimiento.getEstado());
            existingMantenimiento.setKilometrajeActual(mantenimiento.getKilometrajeActual());
            existingMantenimiento.setKilometrajeProximo(mantenimiento.getKilometrajeProximo());
            existingMantenimiento.setObservaciones(mantenimiento.getObservaciones());
            existingMantenimiento.setCosto(mantenimiento.getCosto());
            
            return convertToDTO(mantenimientoRepository.save(existingMantenimiento));
        }
        
        return convertToDTO(mantenimientoRepository.save(mantenimiento));
    }
    
    public MantenimientoDTO iniciarMantenimiento(Long id) {
        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);
        if (mantenimiento.isPresent()) {
            Mantenimiento mant = mantenimiento.get();
            mant.setEstado(Mantenimiento.EstadoMantenimiento.EN_PROCESO);
            mant.setFechaInicio(LocalDateTime.now());
            return convertToDTO(mantenimientoRepository.save(mant));
        }
        throw new RuntimeException("Mantenimiento no encontrado");
    }
    
    public MantenimientoDTO completarMantenimiento(Long id) {
        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);
        if (mantenimiento.isPresent()) {
            Mantenimiento mant = mantenimiento.get();
            mant.setEstado(Mantenimiento.EstadoMantenimiento.COMPLETADO);
            mant.setFechaFin(LocalDateTime.now());
            return convertToDTO(mantenimientoRepository.save(mant));
        }
        throw new RuntimeException("Mantenimiento no encontrado");
    }
    
    public MantenimientoDTO cancelarMantenimiento(Long id) {
        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);
        if (mantenimiento.isPresent()) {
            Mantenimiento mant = mantenimiento.get();
            mant.setEstado(Mantenimiento.EstadoMantenimiento.CANCELADO);
            return convertToDTO(mantenimientoRepository.save(mant));
        }
        throw new RuntimeException("Mantenimiento no encontrado");
    }
    
    public void deleteById(Long id) {
        mantenimientoRepository.deleteById(id);
    }
    
    private MantenimientoDTO convertToDTO(Mantenimiento mantenimiento) {
        return new MantenimientoDTO(
                mantenimiento.getId(),
                mantenimiento.getVehiculo().getId(),
                mantenimiento.getVehiculo().getPlaca(),
                mantenimiento.getVehiculo().getMarca(),
                mantenimiento.getVehiculo().getModelo(),
                mantenimiento.getCliente().getId(),
                mantenimiento.getCliente().getNombre() + " " + mantenimiento.getCliente().getApellido(),
                mantenimiento.getTipoMantenimiento(),
                mantenimiento.getDescripcion(),
                mantenimiento.getFechaProgramada(),
                mantenimiento.getFechaInicio(),
                mantenimiento.getFechaFin(),
                mantenimiento.getEstado().toString(),
                mantenimiento.getKilometrajeActual(),
                mantenimiento.getKilometrajeProximo(),
                mantenimiento.getObservaciones(),
                mantenimiento.getCosto(),
                mantenimiento.getFechaRegistro(),
                mantenimiento.getDetalles() != null ? 
                    mantenimiento.getDetalles().stream()
                        .map(this::convertDetalleToDTO)
                        .collect(Collectors.toList()) : null
        );
    }
    
    private DetalleMantenimientoDTO convertDetalleToDTO(DetalleMantenimiento detalle) {
        return new DetalleMantenimientoDTO(
                detalle.getId(),
                detalle.getMantenimiento().getId(),
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