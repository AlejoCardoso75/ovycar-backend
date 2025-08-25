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
        return convertToDTO(savedEgreso);
    }
    
    public Optional<EgresoDTO> updateEgreso(Long id, CreateEgresoDTO createEgresoDTO) {
        return egresoRepository.findById(id)
                .map(egreso -> {
                    egreso.setConcepto(createEgresoDTO.getConcepto());
                    egreso.setDescripcion(createEgresoDTO.getDescripcion());
                    egreso.setMonto(createEgresoDTO.getMonto());
                    egreso.setCategoria(createEgresoDTO.getCategoria());
                    egreso.setFechaEgreso(createEgresoDTO.getFechaEgreso());
                    egreso.setResponsable(createEgresoDTO.getResponsable());
                    
                    Egreso updatedEgreso = egresoRepository.save(egreso);
                    return convertToDTO(updatedEgreso);
                });
    }
    
    public boolean deleteEgreso(Long id) {
        return egresoRepository.findById(id)
                .map(egreso -> {
                    egresoRepository.delete(egreso);
                    return true;
                })
                .orElse(false);
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
