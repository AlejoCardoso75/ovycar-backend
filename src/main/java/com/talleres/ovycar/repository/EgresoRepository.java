package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Egreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    
    List<Egreso> findByActivoTrue();
    
    @Query("SELECT e FROM Egreso e WHERE e.concepto LIKE %:concepto%")
    List<Egreso> findByConceptoContaining(@Param("concepto") String concepto);
    
    @Query("SELECT e FROM Egreso e WHERE e.categoria = :categoria")
    List<Egreso> findByCategoria(@Param("categoria") String categoria);
    
    @Query("SELECT e FROM Egreso e WHERE e.fechaEgreso BETWEEN :fechaInicio AND :fechaFin")
    List<Egreso> findByFechaEgresoBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT e FROM Egreso e WHERE e.responsable LIKE %:responsable%")
    List<Egreso> findByResponsableContaining(@Param("responsable") String responsable);
}
