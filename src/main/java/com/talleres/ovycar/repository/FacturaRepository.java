package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    List<Factura> findByClienteId(Long clienteId);
    
    List<Factura> findByEstado(Factura.EstadoFactura estado);
    
    @Query("SELECT f FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    List<Factura> findByFechaEmisionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT f FROM Factura f WHERE f.estado = 'PENDIENTE' AND f.fechaVencimiento <= :fecha")
    List<Factura> findFacturasVencidas(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT f FROM Factura f WHERE f.total >= :montoMin AND f.total <= :montoMax")
    List<Factura> findByTotalBetween(@Param("montoMin") Double montoMin, @Param("montoMax") Double montoMax);
    
    @Query("SELECT COUNT(f) FROM Factura f WHERE f.estado = 'PAGADA' AND f.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    Long countFacturasPagadas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                              @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT SUM(f.total) FROM Factura f WHERE f.estado = 'PAGADA' AND f.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    Double sumTotalFacturasPagadas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                   @Param("fechaFin") LocalDateTime fechaFin);
    
    boolean existsByNumeroFactura(String numeroFactura);

    @Query("SELECT f FROM Factura f WHERE f.numeroFactura LIKE %:termino% OR f.cliente.nombre LIKE %:termino% OR f.cliente.apellido LIKE %:termino% OR f.estado LIKE %:termino%")
    List<Factura> buscarFacturas(@Param("termino") String termino);
    
    List<Factura> findByMantenimientoId(Long mantenimientoId);
    
    boolean existsByMantenimientoId(Long mantenimientoId);
} 