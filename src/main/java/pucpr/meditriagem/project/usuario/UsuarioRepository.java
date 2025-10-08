package pucpr.meditriagem.project.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //  metodo para validacao e login
    Optional<Usuario> findByEmail(String email);
}