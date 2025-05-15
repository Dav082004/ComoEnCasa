package com.comoencasa_backend.repository;

import com.comoencasa_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);
    boolean existsByEmail(String email);
}