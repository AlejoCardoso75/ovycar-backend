package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.EgresoDTO;
import com.talleres.ovycar.dto.CreateEgresoDTO;
import com.talleres.ovycar.entity.Egreso;
import com.talleres.ovycar.repository.EgresoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EgresoService {
    
    private final EgresoRepository egresoRepository;
    private final AuditoriaService auditoriaService;
    
    public List<EgresoDTO> findAll() {
        return egresoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<EgresoDTO> findById(Long id) {
        return egresoRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public EgresoDTO createEgreso(CreateEgresoDTO createEgresoDTO) {
        Egreso egreso = new Egreso();
        egreso.setConcepto(createEgresoDTO.getConcepto());
        egreso.setDescripcion(createEgresoDTO.getDescripcion());
        egreso.setMonto(createEgresoDTO.getMonto());
        egreso.setCategoria(createEgresoDTO.getCategoria());
        egreso.setFechaEgreso(createEgresoDTO.getFechaEgreso() != null ? 
                             createEgresoDTO.getFechaEgreso() : LocalDateTime.now());
        egreso.setResponsable(createEgresoDTO.getResponsable());
        egreso.setActivo(true);
        
        Egreso savedEgreso = egresoRepository.save(egreso);
        EgresoDTO dtoCreacion = convertToDTO(savedEgreso);
        auditoriaService.registrar(AuditoriaService.MOD_EGRESO, AuditoriaService.ACC_CREAR, dtoCreacion.getId(),
                "Egreso creado · " + dtoCreacion.getConcepto() + " · monto " + dtoCreacion.getMonto()
                        + " · categoría " + safe(dtoCreacion.getCategoria()) + " · activo " + dtoCreacion.getActivo());
        return dtoCreacion;
    }
    
    public Optional<EgresoDTO> updateEgreso(Long id, CreateEgresoDTO createEgresoDTO) {
        return egresoRepository.findById(id)
                .map(egreso -> {
                    boolean activoAntes = Boolean.TRUE.equals(egreso.getActivo());
                    egreso.setConcepto(createEgresoDTO.getConcepto());
                    egreso.setDescripcion(createEgresoDTO.getDescripcion());
                    egreso.setMonto(createEgresoDTO.getMonto());
                    egreso.setCategoria(createEgresoDTO.getCategoria());
                    egreso.setFechaEgreso(createEgresoDTO.getFechaEgreso());
                    egreso.setResponsable(createEgresoDTO.getResponsable());
                    if (createEgresoDTO.getActivo() != null) {
                        egreso.setActivo(createEgresoDTO.getActivo());
                    }

                    Egreso updatedEgreso = egresoRepository.save(egreso);
                    EgresoDTO dto = convertToDTO(updatedEgreso);
                    boolean activoDespues = Boolean.TRUE.equals(updatedEgreso.getActivo());
                    if (createEgresoDTO.getActivo() != null && activoAntes != activoDespues) {
                        auditoriaService.registrar(AuditoriaService.MOD_EGRESO, AuditoriaService.ACC_CAMBIO_ESTADO, id,
                                "Egreso " + id + " · estado activo: " + activoAntes + " → " + activoDespues
                                        + " · " + dto.getConcepto());
                    } else {
                        auditoriaService.registrar(AuditoriaService.MOD_EGRESO, AuditoriaService.ACC_ACTUALIZAR, id,
                                "Egreso " + id + " actualizado · " + dto.getConcepto() + " · monto " + dto.getMonto()
                                        + " · activo " + activoDespues);
                    }
                    return dto;
                });
    }
    
    public boolean deleteEgreso(Long id) {
        return egresoRepository.findById(id)
                .map(egreso -> {
                    String resumen = "Egreso eliminado id " + id + " · " + egreso.getConcepto()
                            + " · activo " + egreso.getActivo();
                    egresoRepository.delete(egreso);
                    auditoriaService.registrar(AuditoriaService.MOD_EGRESO, AuditoriaService.ACC_ELIMINAR, id, resumen);
                    return true;
                })
                .orElse(false);
    }

    private static String safe(String s) {
        return s != null ? s : "-";
    }
    
    private EgresoDTO convertToDTO(Egreso egreso) {
        return new EgresoDTO(
            egreso.getId(),
            egreso.getConcepto(),
            egreso.getDescripcion(),
            egreso.getMonto(),
            egreso.getCategoria(),
            egreso.getFechaEgreso(),
            egreso.getFechaRegistro(),
            egreso.getResponsable(),
            egreso.getActivo()
        );
    }
}
