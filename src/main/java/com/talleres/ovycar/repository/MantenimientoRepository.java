package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    
    List<Mantenimiento> findByClienteId(Long clienteId);
    
    List<Mantenimiento> findByVehiculoId(Long vehiculoId);
    
    List<Mantenimiento> findByEstado(Mantenimiento.EstadoMantenimiento estado);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.fechaProgramada BETWEEN :fechaInicio AND :fechaFin")
    List<Mantenimiento> findByFechaProgramadaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                     @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = 'PROGRAMADO' AND m.fechaProgramada >= :fecha")
    List<Mantenimiento> findMantenimientosProgramados(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = 'EN_PROCESO'")
    List<Mantenimiento> findMantenimientosEnProceso();
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.estado = 'COMPLETADO'")
    List<Mantenimiento> findMantenimientosCompletados();
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.tipoMantenimiento LIKE %:termino% OR m.descripcion LIKE %:termino% OR m.vehiculo.placa LIKE %:termino% OR m.cliente.nombre LIKE %:termino% OR m.cliente.apellido LIKE %:termino%")
    List<Mantenimiento> buscarMantenimientos(@Param("termino") String termino);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.cliente.id = :clienteId AND m.estado = 'COMPLETADO'")
    List<Mantenimiento> findHistorialCliente(@Param("clienteId") Long clienteId);
    
    @Query("SELECT m FROM Mantenimiento m WHERE m.vehiculo.id = :vehiculoId AND m.estado = 'COMPLETADO'")
    List<Mantenimiento> findHistorialVehiculo(@Param("vehiculoId") Long vehiculoId);
} 