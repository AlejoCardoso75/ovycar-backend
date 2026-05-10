package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.CreateMecanicoDTO;
import com.talleres.ovycar.dto.MecanicoDTO;
import com.talleres.ovycar.entity.Mecanico;
import jakarta.annotation.PostConstruct;
import com.talleres.ovycar.repository.MecanicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MecanicoService {

    private final MecanicoRepository mecanicoRepository;

    @PostConstruct
    public void seedDefaultMecanicos() {
        if (mecanicoRepository.count() > 0) {
            return;
        }

        mecanicoRepository.save(new Mecanico(null, "Alejandro", 0.35, true, null));
        mecanicoRepository.save(new Mecanico(null, "Luis", 0.40, true, null));
        mecanicoRepository.save(new Mecanico(null, "Carlos", 0.38, true, null));
        mecanicoRepository.save(new Mecanico(null, "Andrés", 0.42, true, null));
    }

    @Transactional(readOnly = true)
    public List<MecanicoDTO> findAll() {
        return mecanicoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MecanicoDTO> findActivos() {
        return mecanicoRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MecanicoDTO> findById(Long id) {
        return mecanicoRepository.findById(id).map(this::toDTO);
    }

    public MecanicoDTO create(CreateMecanicoDTO dto) {
        validar(dto);

        if (mecanicoRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RuntimeException("Ya existe un mecánico con ese nombre.");
        }

        Mecanico mecanico = new Mecanico();
        mecanico.setNombre(dto.getNombre().trim());
        mecanico.setPorcentajeGanancia(dto.getPorcentajeGanancia());
        mecanico.setActivo(dto.getActivo() == null || dto.getActivo());

        return toDTO(mecanicoRepository.save(mecanico));
    }

    public MecanicoDTO update(Long id, CreateMecanicoDTO dto) {
        validar(dto);

        Mecanico mecanico = mecanicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mecánico no encontrado."));

        mecanico.setNombre(dto.getNombre().trim());
        mecanico.setPorcentajeGanancia(dto.getPorcentajeGanancia());
        if (dto.getActivo() != null) {
            mecanico.setActivo(dto.getActivo());
        }

        return toDTO(mecanicoRepository.save(mecanico));
    }

    public void delete(Long id) {
        if (!mecanicoRepository.existsById(id)) {
            throw new RuntimeException("Mecánico no encontrado.");
        }
        mecanicoRepository.deleteById(id);
    }

    private void validar(CreateMecanicoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del mecánico es obligatorio.");
        }
        if (dto.getPorcentajeGanancia() == null) {
            throw new RuntimeException("El porcentaje de ganancia es obligatorio.");
        }
        if (dto.getPorcentajeGanancia() < 0 || dto.getPorcentajeGanancia() > 1) {
            throw new RuntimeException("El porcentaje de ganancia debe estar entre 0 y 1.");
        }
    }

    private MecanicoDTO toDTO(Mecanico mecanico) {
        return new MecanicoDTO(
                mecanico.getId(),
                mecanico.getNombre(),
                mecanico.getPorcentajeGanancia(),
                mecanico.getActivo()
        );
    }
}
