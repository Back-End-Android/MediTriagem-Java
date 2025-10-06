package pucpr.meditriagem.project.triagem;

import org.springframework.data.jpa.repository.JpaRepository;
import pucpr.meditriagem.project.triagem.Triagem;
import java.util.Optional;

public interface TriagemRepository extends JpaRepository<Triagem, Long>{
}
