package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByDocumento(String documento);
    
    List<Cliente> findByActivoTrue();
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:nombre% OR c.apellido LIKE %:nombre%")
    List<Cliente> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT c FROM Cliente c WHERE c.telefono LIKE %:telefono%")
    List<Cliente> findByTelefonoContaining(@Param("telefono") String telefono);
    
    boolean existsByDocumento(String documento);
} 