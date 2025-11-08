package pucpr.meditriagem.project.consulta;

import org.springframework.data.jpa.repository.JpaRepository;
import pucpr.meditriagem.project.agendamento.Agendamento;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    // Validação para garantir que um Agendamento só tenha uma Consulta
    boolean existsByAgendamento(Agendamento agendamento);
}