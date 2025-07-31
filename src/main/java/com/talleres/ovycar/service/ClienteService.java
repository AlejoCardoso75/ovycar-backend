package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.ClienteDTO;
import com.talleres.ovycar.entity.Cliente;
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
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    public List<ClienteDTO> findAll() {
        return clienteRepository.findByActivoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ClienteDTO> findById(Long id) {
        return clienteRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<ClienteDTO> findByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento)
                .map(this::convertToDTO);
    }
    
    public List<ClienteDTO> findByNombreContaining(String nombre) {
        return clienteRepository.findByNombreContaining(nombre)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ClienteDTO save(Cliente cliente) {
        if (cliente.getId() == null && clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new RuntimeException("Ya existe un cliente con el documento: " + cliente.getDocumento());
        }
        return convertToDTO(clienteRepository.save(cliente));
    }
    
    public void deleteById(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            cliente.get().setActivo(false);
            clienteRepository.save(cliente.get());
        }
    }
    
    private ClienteDTO convertToDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getDocumento(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion(),
                cliente.getFechaRegistro(),
                cliente.getActivo()
        );
    }
} 