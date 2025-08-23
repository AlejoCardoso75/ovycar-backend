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
        // Validar campos obligatorios
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new RuntimeException("El apellido es obligatorio");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new RuntimeException("El teléfono es obligatorio");
        }
        
        // Limpiar campos opcionales si están vacíos
        if (cliente.getDocumento() != null && cliente.getDocumento().trim().isEmpty()) {
            cliente.setDocumento(null);
        }
        if (cliente.getEmail() != null && cliente.getEmail().trim().isEmpty()) {
            cliente.setEmail(null);
        }
        if (cliente.getDireccion() != null && cliente.getDireccion().trim().isEmpty()) {
            cliente.setDireccion(null);
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