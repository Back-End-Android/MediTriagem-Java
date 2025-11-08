package pucpr.meditriagem.project.triagem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pucpr.meditriagem.project.triagem.Triagem;
import pucpr.meditriagem.project.triagem.dto.TriagemDTO;
import java.util.List;
import java.util.Optional;

public interface TriagemRepository extends JpaRepository<Triagem, Long>{

    @Query("""
        SELECT new pucpr.meditriagem.project.triagem.dto.TriagemResumoDTO(
            t.id_triagem,
            t.paciente.nome_completo,
            t.paciente.genero,
            t.paciente.dataNascimento
        )
        FROM Triagem t
        WHERE t.isActivated = true
    """)
    List<TriagemDTO> buscarTriagensAtivasResumo();
}

