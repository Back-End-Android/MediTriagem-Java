package pucpr.meditriagem.project.paciente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf);
    
    @Query("SELECT p FROM Paciente p WHERE p.usuario.id = :usuarioId")
    Optional<Paciente> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
