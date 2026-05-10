package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.MantenimientoDTO;
import com.talleres.ovycar.dto.DetalleMantenimientoDTO;
import com.talleres.ovycar.dto.CreateMantenimientoDTO;
import com.talleres.ovycar.entity.Mantenimiento;
import com.talleres.ovycar.entity.DetalleMantenimiento;
import com.talleres.ovycar.entity.Mecanico;
import com.talleres.ovycar.repository.MantenimientoRepository;
import com.talleres.ovycar.repository.ClienteRepository;
import com.talleres.ovycar.repository.VehiculoRepository;
import com.talleres.ovycar.repository.MecanicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
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
    private final MecanicoRepository mecanicoRepository;
    private final AuditoriaService auditoriaService;
    
    @Cacheable(value = "mantenimientos", key = "'all'")
    public List<MantenimientoDTO> findAll() {
        return mantenimientoRepository.findAllWithBasicRelationsOnly()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<MantenimientoDTO> findById(Long id) {
        return mantenimientoRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<MantenimientoDTO> findByIdWithDetails(Long id) {
        return mantenimientoRepository.findById(id)
                .map(mantenimiento -> {
                    // Forzar la carga de detalles si es necesario
                    if (mantenimiento.getDetalles() != null) {
                        mantenimiento.getDetalles().size(); // Esto fuerza la carga lazy
                    }
                    return convertToDTO(mantenimiento);
                });
    }
    
    public List<MantenimientoDTO> findByClienteId(Long clienteId) {
        return mantenimientoRepository.findByClienteIdWithRelations(clienteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findByVehiculoId(Long vehiculoId) {
        return mantenimientoRepository.findByVehiculoIdWithRelations(vehiculoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findByEstado(Mantenimiento.EstadoMantenimiento estado) {
        return mantenimientoRepository.findByEstadoWithRelations(estado)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findMantenimientosProgramados() {
        return mantenimientoRepository.findMantenimientosProgramadosWithRelations(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findMantenimientosEnProceso() {
        return mantenimientoRepository.findMantenimientosEnProcesoWithRelations()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findMantenimientosCompletados() {
        return mantenimientoRepository.findMantenimientosCompletadosWithRelations()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> buscarMantenimientos(String termino) {
        return mantenimientoRepository.buscarMantenimientosWithRelations(termino)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findHistorialCliente(Long clienteId) {
        return mantenimientoRepository.findHistorialClienteWithRelations(clienteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MantenimientoDTO> findHistorialVehiculo(Long vehiculoId) {
        return mantenimientoRepository.findHistorialVehiculoWithRelations(vehiculoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "mantenimientos", allEntries = true)
    public MantenimientoDTO createMantenimiento(CreateMantenimientoDTO createMantenimientoDTO) {
        // Obtener el vehículo y el cliente
        Vehiculo vehiculo = vehiculoRepository.findById(createMantenimientoDTO.getVehiculoId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        
        // Crear el mantenimiento
        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setCliente(vehiculo.getCliente());
        mantenimiento.setTipoMantenimiento(createMantenimientoDTO.getTipoMantenimiento());
        mantenimiento.setDescripcion(createMantenimientoDTO.getDescripcion());
        // Convertir LocalDate a LocalDateTime (a las 00:00:00)
        LocalDateTime fechaProgramada = createMantenimientoDTO.getFechaProgramada().atStartOfDay();
        mantenimiento.setFechaProgramada(fechaProgramada);
        mantenimiento.setEstado(Mantenimiento.EstadoMantenimiento.valueOf(createMantenimientoDTO.getEstado()));
        mantenimiento.setKilometrajeActual(createMantenimientoDTO.getKilometrajeActual());
        mantenimiento.setObservaciones(createMantenimientoDTO.getObservaciones());
        mantenimiento.setCosto(createMantenimientoDTO.getCosto());
        mantenimiento.setCostoManoObra(createMantenimientoDTO.getCostoManoObra());
        mantenimiento.setValorRepuestos(createMantenimientoDTO.getValorRepuestos());
        mantenimiento.setCostoAdicionales(createMantenimientoDTO.getCostoAdicionales());
        mantenimiento.setProveedorRepuestos(createMantenimientoDTO.getProveedorRepuestos());
        mantenimiento.setGarantia(createMantenimientoDTO.getGarantia());
        asignarMecanicoYGanancia(mantenimiento, createMantenimientoDTO.getMecanicoId(), createMantenimientoDTO.getMecanico());

        MantenimientoDTO dto = convertToDTO(mantenimientoRepository.save(mantenimiento));
        auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_CREAR, dto.getId(),
                "Mantenimiento creado · id " + dto.getId() + " · placa " + dto

                        .getVehiculoPlaca()
                        + " · " + dto.getVehiculoMarca() + " " + dto.getVehiculoModelo()
                        + " · tipo " + dto.getTipoMantenimiento() + " · estado " + dto.getEstado()
                        + " · cliente " + dto.getClienteNombre());
        return dto;
    }

    @CacheEvict(value = "mantenimientos", allEntries = true)
    public MantenimientoDTO updateFromDTO(Long id, CreateMantenimientoDTO updateMantenimientoDTO) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        if (updateMantenimientoDTO.getVehiculoId() != null &&
                !mantenimiento.getVehiculo().getId().equals(updateMantenimientoDTO.getVehiculoId())) {
            Vehiculo vehiculo = vehiculoRepository.findById(updateMantenimientoDTO.getVehiculoId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
            mantenimiento.setVehiculo(vehiculo);
            mantenimiento.setCliente(vehiculo.getCliente());
        }

        mantenimiento.setTipoMantenimiento(updateMantenimientoDTO.getTipoMantenimiento());
        mantenimiento.setDescripcion(updateMantenimientoDTO.getDescripcion());
        if (updateMantenimientoDTO.getFechaProgramada() != null) {
            mantenimiento.setFechaProgramada(updateMantenimientoDTO.getFechaProgramada().atStartOfDay());
        }
        mantenimiento.setEstado(Mantenimiento.EstadoMantenimiento.valueOf(updateMantenimientoDTO.getEstado()));
        mantenimiento.setKilometrajeActual(updateMantenimientoDTO.getKilometrajeActual());
        mantenimiento.setObservaciones(updateMantenimientoDTO.getObservaciones());
        mantenimiento.setCosto(updateMantenimientoDTO.getCosto());
        mantenimiento.setCostoManoObra(updateMantenimientoDTO.getCostoManoObra());
        mantenimiento.setValorRepuestos(updateMantenimientoDTO.getValorRepuestos());
        mantenimiento.setCostoAdicionales(updateMantenimientoDTO.getCostoAdicionales());
        mantenimiento.setProveedorRepuestos(updateMantenimientoDTO.getProveedorRepuestos());
        mantenimiento.setGarantia(updateMantenimientoDTO.getGarantia());
        asignarMecanicoYGanancia(mantenimiento, updateMantenimientoDTO.getMecanicoId(), updateMantenimientoDTO.getMecanico());

        if (mantenimiento.getEstado() == Mantenimiento.EstadoMantenimiento.COMPLETADO &&
                mantenimiento.getFechaFin() == null) {
            mantenimiento.setFechaFin(LocalDateTime.now());
        }

        MantenimientoDTO dtoUpd = convertToDTO(mantenimientoRepository.save(mantenimiento));
        auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_ACTUALIZAR, dtoUpd.getId(),
                "Mantenimiento actualizado · id " + dtoUpd.getId() + " · placa " + dtoUpd.getVehiculoPlaca()
                        + " · estado " + dtoUpd.getEstado() + " · tipo " + dtoUpd.getTipoMantenimiento());
        return dtoUpd;
    }
    
    @CacheEvict(value = "mantenimientos", allEntries = true)
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
            // existingMantenimiento.setKilometrajeProximo(mantenimiento.getKilometrajeProximo());
            existingMantenimiento.setObservaciones(mantenimiento.getObservaciones());
            existingMantenimiento.setCosto(mantenimiento.getCosto());
            existingMantenimiento.setCostoManoObra(mantenimiento.getCostoManoObra());
            existingMantenimiento.setValorRepuestos(mantenimiento.getValorRepuestos());
            existingMantenimiento.setCostoAdicionales(mantenimiento.getCostoAdicionales());
            existingMantenimiento.setProveedorRepuestos(mantenimiento.getProveedorRepuestos());
            existingMantenimiento.setGarantia(mantenimiento.getGarantia());
            existingMantenimiento.setMecanico(mantenimiento.getMecanico());
            recalcularGananciaMecanico(existingMantenimiento);
            
            // Si el estado cambia a COMPLETADO, establecer la fecha de fin
            if (mantenimiento.getEstado() == Mantenimiento.EstadoMantenimiento.COMPLETADO && 
                existingMantenimiento.getFechaFin() == null) {
                existingMantenimiento.setFechaFin(LocalDateTime.now());
            }
            
            MantenimientoDTO dtoSave = convertToDTO(mantenimientoRepository.save(existingMantenimiento));
            auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_ACTUALIZAR, dtoSave.getId(),
                    "Mantenimiento guardado (actualización) · id " + dtoSave.getId() + " · placa " + dtoSave.getVehiculoPlaca());
            return dtoSave;
        }

        MantenimientoDTO dtoNuevo = convertToDTO(mantenimientoRepository.save(mantenimiento));
        auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_CREAR, dtoNuevo.getId(),
                "Mantenimiento creado (save) · id " + dtoNuevo.getId() + " · placa " + dtoNuevo.getVehiculoPlaca());
        return dtoNuevo;
    }
    
    @CacheEvict(value = "mantenimientos", allEntries = true)
    public MantenimientoDTO iniciarMantenimiento(Long id) {
        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);
        if (mantenimiento.isPresent()) {
            Mantenimiento mant = mantenimiento.get();
            mant.setEstado(Mantenimiento.EstadoMantenimiento.EN_PROCESO);
            mant.setFechaInicio(LocalDateTime.now());
            MantenimientoDTO dtoIni = convertToDTO(mantenimientoRepository.save(mant));
            auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_CAMBIO_ESTADO, dtoIni.getId(),
                    "Mantenimiento iniciado · id " + dtoIni.getId() + " · placa " + dtoIni.getVehiculoPlaca() + " · EN_PROCESO");
            return dtoIni;
        }
        throw new RuntimeException("Mantenimiento no encontrado");
    }
    
    @CacheEvict(value = "mantenimientos", allEntries = true)
    public MantenimientoDTO completarMantenimiento(Long id) {
        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);
        if (mantenimiento.isPresent()) {
            Mantenimiento mant = mantenimiento.get();
            mant.setEstado(Mantenimiento.EstadoMantenimiento.COMPLETADO);
            mant.setFechaFin(LocalDateTime.now());
            MantenimientoDTO dtoCmp = convertToDTO(mantenimientoRepository.save(mant));
            auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_CAMBIO_ESTADO, dtoCmp.getId(),
                    "Mantenimiento completado · id " + dtoCmp.getId() + " · placa " + dtoCmp.getVehiculoPlaca() + " · COMPLETADO");
            return dtoCmp;
        }
        throw new RuntimeException("Mantenimiento no encontrado");
    }
    
    @CacheEvict(value = "mantenimientos", allEntries = true)
    public MantenimientoDTO cancelarMantenimiento(Long id) {
        Optional<Mantenimiento> mantenimiento = mantenimientoRepository.findById(id);
        if (mantenimiento.isPresent()) {
            Mantenimiento mant = mantenimiento.get();
            mant.setEstado(Mantenimiento.EstadoMantenimiento.CANCELADO);
            MantenimientoDTO dtoCan = convertToDTO(mantenimientoRepository.save(mant));
            auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_CAMBIO_ESTADO, dtoCan.getId(),
                    "Mantenimiento cancelado · id " + dtoCan.getId() + " · placa " + dtoCan.getVehiculoPlaca() + " · CANCELADO");
            return dtoCan;
        }
        throw new RuntimeException("Mantenimiento no encontrado");
    }
    
    @CacheEvict(value = "mantenimientos", allEntries = true)
    public void deleteById(Long id) {
        // Check if mantenimiento exists
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        String resumen = "Mantenimiento eliminado · id " + id + " · placa " + mantenimiento.getVehiculo().getPlaca()
                + " · estado " + mantenimiento.getEstado();
        mantenimientoRepository.deleteById(id);
        auditoriaService.registrar(AuditoriaService.MOD_MANTENIMIENTO, AuditoriaService.ACC_ELIMINAR, id, resumen);
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
                mantenimiento.getCostoManoObra(),
                mantenimiento.getValorRepuestos(),
                mantenimiento.getCostoAdicionales(),
                mantenimiento.getProveedorRepuestos(),
                mantenimiento.getGarantia(),
                mantenimiento.getMecanicoAsignado() != null ? mantenimiento.getMecanicoAsignado().getId() : null,
                mantenimiento.getMecanico(),
                mantenimiento.getPorcentajeMecanico(),
                mantenimiento.getGananciaMecanico(),
                mantenimiento.getFechaRegistro(),
                // Solo cargar detalles si están disponibles (lazy loading)
                mantenimiento.getDetalles() != null && !mantenimiento.getDetalles().isEmpty() ? 
                    mantenimiento.getDetalles().stream()
                        .map(this::convertDetalleToDTO)
                        .collect(Collectors.toList()) : null
        );
    }

    private void asignarMecanicoYGanancia(Mantenimiento mantenimiento, Long mecanicoId, String mecanicoNombrePlano) {
        if (mecanicoId != null) {
            Mecanico mecanico = mecanicoRepository.findById(mecanicoId)
                    .orElseThrow(() -> new RuntimeException("Mecánico no encontrado"));
            mantenimiento.setMecanicoAsignado(mecanico);
            mantenimiento.setMecanico(mecanico.getNombre());
            mantenimiento.setPorcentajeMecanico(mecanico.getPorcentajeGanancia());
        } else {
            mantenimiento.setMecanicoAsignado(null);
            mantenimiento.setMecanico(mecanicoNombrePlano);
            mantenimiento.setPorcentajeMecanico(0.0);
        }
        recalcularGananciaMecanico(mantenimiento);
    }

    private void recalcularGananciaMecanico(Mantenimiento mantenimiento) {
        double manoObra = mantenimiento.getCostoManoObra() != null ? mantenimiento.getCostoManoObra() : 0.0;
        double porcentaje = mantenimiento.getPorcentajeMecanico() != null ? mantenimiento.getPorcentajeMecanico() : 0.0;
        mantenimiento.setGananciaMecanico(manoObra * porcentaje);
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