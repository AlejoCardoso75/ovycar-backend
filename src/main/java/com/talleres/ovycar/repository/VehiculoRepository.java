package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    
    Optional<Vehiculo> findByPlaca(String placa);
    
    List<Vehiculo> findByClienteId(Long clienteId);
    
    List<Vehiculo> findByActivoTrue();
    
    @Query("SELECT v FROM Vehiculo v WHERE v.placa LIKE %:placa%")
    List<Vehiculo> findByPlacaContaining(@Param("placa") String placa);
    
    @Query("SELECT v FROM Vehiculo v WHERE v.marca LIKE %:marca%")
    List<Vehiculo> findByMarcaContaining(@Param("marca") String marca);
    
    @Query("SELECT v FROM Vehiculo v WHERE v.modelo LIKE %:modelo%")
    List<Vehiculo> findByModeloContaining(@Param("modelo") String modelo);
    
    boolean existsByPlaca(String placa);
} 