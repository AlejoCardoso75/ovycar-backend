package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.VehiculoDTO;
import com.talleres.ovycar.dto.CreateVehiculoDTO;
import com.talleres.ovycar.entity.Vehiculo;
import com.talleres.ovycar.entity.Cliente;
import com.talleres.ovycar.repository.VehiculoRepository;
import com.talleres.ovycar.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculoService {
    
    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    
    public List<VehiculoDTO> findAll() {
        return vehiculoRepository.findByActivoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<VehiculoDTO> findById(Long id) {
        return vehiculoRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<VehiculoDTO> findByPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa)
                .map(this::convertToDTO);
    }
    
    public List<VehiculoDTO> findByClienteId(Long clienteId) {
        return vehiculoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<VehiculoDTO> findByPlacaContaining(String placa) {
        return vehiculoRepository.findByPlacaContaining(placa)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<VehiculoDTO> findByMarcaContaining(String marca) {
        return vehiculoRepository.findByMarcaContaining(marca)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public VehiculoDTO save(Vehiculo vehiculo) {
        if (vehiculo.getId() == null && vehiculoRepository.existsByPlaca(vehiculo.getPlaca())) {
            throw new RuntimeException("Ya existe un vehículo con la placa: " + vehiculo.getPlaca());
        }
        return convertToDTO(vehiculoRepository.save(vehiculo));
    }
    
    public VehiculoDTO createFromDTO(CreateVehiculoDTO createVehiculoDTO) {
        // Verificar que el cliente existe
        Cliente cliente = clienteRepository.findById(createVehiculoDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + createVehiculoDTO.getClienteId()));
        
        // Verificar que la placa no existe
        if (vehiculoRepository.existsByPlaca(createVehiculoDTO.getPlaca())) {
            throw new RuntimeException("Ya existe un vehículo con la placa: " + createVehiculoDTO.getPlaca());
        }
        
        // Crear el vehículo
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setCliente(cliente);
        vehiculo.setPlaca(createVehiculoDTO.getPlaca());
        vehiculo.setMarca(createVehiculoDTO.getMarca());
        vehiculo.setModelo(createVehiculoDTO.getModelo());
        vehiculo.setAnio(createVehiculoDTO.getAnio());
        vehiculo.setColor(createVehiculoDTO.getColor());
        vehiculo.setNumeroVin(createVehiculoDTO.getNumeroVin());
        vehiculo.setKilometraje(createVehiculoDTO.getKilometraje());
        vehiculo.setActivo(true);
        
        return convertToDTO(vehiculoRepository.save(vehiculo));
    }
    
    public VehiculoDTO updateFromDTO(Long id, CreateVehiculoDTO createVehiculoDTO) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + id));
        
        // Verificar que el cliente existe
        Cliente cliente = clienteRepository.findById(createVehiculoDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + createVehiculoDTO.getClienteId()));
        
        // Verificar que la placa no existe en otro vehículo
        if (!vehiculo.getPlaca().equals(createVehiculoDTO.getPlaca()) && 
            vehiculoRepository.existsByPlaca(createVehiculoDTO.getPlaca())) {
            throw new RuntimeException("Ya existe un vehículo con la placa: " + createVehiculoDTO.getPlaca());
        }
        
        // Actualizar el vehículo
        vehiculo.setCliente(cliente);
        vehiculo.setPlaca(createVehiculoDTO.getPlaca());
        vehiculo.setMarca(createVehiculoDTO.getMarca());
        vehiculo.setModelo(createVehiculoDTO.getModelo());
        vehiculo.setAnio(createVehiculoDTO.getAnio());
        vehiculo.setColor(createVehiculoDTO.getColor());
        vehiculo.setNumeroVin(createVehiculoDTO.getNumeroVin());
        vehiculo.setKilometraje(createVehiculoDTO.getKilometraje());
        
        return convertToDTO(vehiculoRepository.save(vehiculo));
    }
    
    public void deleteById(Long id) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(id);
        if (vehiculo.isPresent()) {
            vehiculo.get().setActivo(false);
            vehiculoRepository.save(vehiculo.get());
        }
    }
    
    private VehiculoDTO convertToDTO(Vehiculo vehiculo) {
        return new VehiculoDTO(
                vehiculo.getId(),
                vehiculo.getCliente().getId(),
                vehiculo.getCliente().getNombre() + " " + vehiculo.getCliente().getApellido(),
                vehiculo.getPlaca(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getColor(),
                vehiculo.getNumeroVin(),
                vehiculo.getKilometraje(),
                vehiculo.getFechaRegistro(),
                vehiculo.getActivo()
        );
    }
} 