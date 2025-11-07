package pucpr.meditriagem.project.medico;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByCpf(String cpf);
    Optional<Medico> findByCrm(String crm);
}