package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findByCodigo(String codigo);
    
    List<Producto> findByActivoTrue();
    
    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre%")
    List<Producto> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria")
    List<Producto> findByCategoria(@Param("categoria") String categoria);
    
    @Query("SELECT p FROM Producto p WHERE p.marca = :marca")
    List<Producto> findByMarca(@Param("marca") String marca);
    
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosStockBajo();
    
    @Query("SELECT p FROM Producto p WHERE p.stock = 0")
    List<Producto> findProductosSinStock();
    
    boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:termino% OR p.codigo LIKE %:termino% OR p.categoria LIKE %:termino% OR p.marca LIKE %:termino%")
    List<Producto> findByNombreContainingOrCodigoContainingOrCategoriaContainingOrMarcaContaining(
        @Param("termino") String termino1, 
        @Param("termino") String termino2, 
        @Param("termino") String termino3, 
        @Param("termino") String termino4
    );
} 