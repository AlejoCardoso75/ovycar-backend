package com.talleres.ovycar.service;

import com.talleres.ovycar.entity.Servicio;
import com.talleres.ovycar.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {
    
    private final ServicioRepository servicioRepository;
    
    public List<Servicio> findAll() {
        return servicioRepository.findAll();
    }
    
    public Optional<Servicio> findById(Long id) {
        return servicioRepository.findById(id);
    }
    
    public List<Servicio> findByNombreContaining(String nombre) {
        return servicioRepository.findByNombreContaining(nombre);
    }
    
    public List<Servicio> findByCategoria(String categoria) {
        return servicioRepository.findByCategoria(categoria);
    }
    
    public List<Servicio> findByPrecioBetween(Double precioMin, Double precioMax) {
        return servicioRepository.findByPrecioBetween(precioMin, precioMax);
    }
    
    public Servicio save(Servicio servicio) {
        return servicioRepository.save(servicio);
    }
    
    public void deleteById(Long id) {
        servicioRepository.deleteById(id);
    }
} 