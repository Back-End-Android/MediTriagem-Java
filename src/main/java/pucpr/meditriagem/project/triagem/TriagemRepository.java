package pucpr.meditriagem.project.triagem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pucpr.meditriagem.project.triagem.Triagem;
import java.util.List;

public interface TriagemRepository extends JpaRepository<Triagem, Long>{

    // Busca todas as triagens ativas
    @Query("""
        SELECT t
        FROM Triagem t
        WHERE t.isActivated = true
    """)
    List<Triagem> buscarTriagensAtivas();
}

