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
    private final AuditoriaService auditoriaService;
    
    public List<ClienteDTO> findAll() {
        return clienteRepository.findAll()
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
        boolean esNuevo = cliente.getId() == null;
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
        
        Cliente saved = clienteRepository.save(cliente);
        ClienteDTO dto = convertToDTO(saved);
        String acc = esNuevo ? AuditoriaService.ACC_CREAR : AuditoriaService.ACC_ACTUALIZAR;
        auditoriaService.registrar(AuditoriaService.MOD_CLIENTE, acc, dto.getId(),
                (esNuevo ? "Cliente creado: " : "Cliente actualizado: ")
                        + dto.getNombre() + " " + dto.getApellido()
                        + (dto.getDocumento() != null ? " · Doc " + dto.getDocumento() : ""));
        return dto;
    }
    
    public void deleteById(Long id) {
        clienteRepository.findById(id).ifPresent(c -> {
            String resumen = "Cliente eliminado: " + c.getNombre() + " " + c.getApellido()
                    + (c.getDocumento() != null ? " · Doc " + c.getDocumento() : "");
            clienteRepository.deleteById(id);
            auditoriaService.registrar(AuditoriaService.MOD_CLIENTE, AuditoriaService.ACC_ELIMINAR, id, resumen);
        });
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