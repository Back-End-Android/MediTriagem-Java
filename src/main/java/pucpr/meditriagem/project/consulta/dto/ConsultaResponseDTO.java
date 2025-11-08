package pucpr.meditriagem.project.consulta.dto;

import pucpr.meditriagem.project.consulta.Consulta;
import pucpr.meditriagem.project.consulta.ClassificacaoFinal;
import pucpr.meditriagem.project.agendamento.dto.AgendamentoDTO;
import pucpr.meditriagem.project.triagem.dto.TriagemResponseDTO;

import java.time.LocalDateTime;

// DTO para retornar a consulta com todos os detalhes agregados
public record ConsultaResponseDTO(
        Long id,

        // DTOs agregados
        AgendamentoDTO agendamento,
        TriagemResponseDTO triagem,

        // Dados do atendimento
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String observacoesMedicas,
        String encaminhamentos,
        ClassificacaoFinal classificacaoFinal
) {
    // Construtor para mapeamento
    public ConsultaResponseDTO(Consulta consulta, AgendamentoDTO agendamentoDTO, TriagemResponseDTO triagemDTO) {
        this(
                consulta.getId(),
                agendamentoDTO,
                triagemDTO,
                consulta.getDataHoraInicio(),
                consulta.getDataHoraFim(),
                consulta.getObservacoesMedicas(),
                consulta.getEncaminhamentos(),
                consulta.getClassificacaoFinal()
        );
    }
}