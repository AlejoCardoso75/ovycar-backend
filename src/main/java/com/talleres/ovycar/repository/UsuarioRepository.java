package com.talleres.ovycar.repository;

import com.talleres.ovycar.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByUsernameAndActivoTrue(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByActivoTrue();
    
    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password AND u.activo = true")
    Optional<Usuario> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
