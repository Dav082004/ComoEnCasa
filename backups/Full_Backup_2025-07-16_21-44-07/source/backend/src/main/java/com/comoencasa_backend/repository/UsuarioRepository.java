package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email); // Debe coincidir con el nombre del campo en la entidad

    boolean existsByEmail(String email);

    List<Usuario> findByRecomendacionIsNotNull(); // Para obtener usuarios con recomendaciones
}
