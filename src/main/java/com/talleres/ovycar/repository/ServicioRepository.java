package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
    List<Servicio> findByActivoTrue();
    
    @Query("SELECT s FROM Servicio s WHERE s.nombre LIKE %:nombre%")
    List<Servicio> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT s FROM Servicio s WHERE s.categoria = :categoria")
    List<Servicio> findByCategoria(@Param("categoria") String categoria);
    
    @Query("SELECT s FROM Servicio s WHERE s.precio BETWEEN :precioMin AND :precioMax")
    List<Servicio> findByPrecioBetween(@Param("precioMin") Double precioMin, @Param("precioMax") Double precioMax);
} 