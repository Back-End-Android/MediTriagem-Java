package pucpr.meditriagem.project.questionario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QuestionarioRepository extends JpaRepository<QuestionarioSintomas, Long> {

    // Busca todos os questionários por paciente
    @Query("""
        SELECT q
        FROM QuestionarioSintomas q
        WHERE q.pacienteId = :pacienteId
    """)
    List<QuestionarioSintomas> buscarPorPacienteId(Long pacienteId);

    // Busca todos os questionários
    List<QuestionarioSintomas> findAll();
}

