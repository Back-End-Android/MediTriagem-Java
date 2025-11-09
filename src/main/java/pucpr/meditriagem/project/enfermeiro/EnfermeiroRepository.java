package pucpr.meditriagem.project.enfermeiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnfermeiroRepository extends JpaRepository<Enfermeiro, Long> {
    boolean existsByCpf(String cpf);
    Optional<Enfermeiro> findByCoren(String coren);
    boolean existsByCoren(String coren);
}
