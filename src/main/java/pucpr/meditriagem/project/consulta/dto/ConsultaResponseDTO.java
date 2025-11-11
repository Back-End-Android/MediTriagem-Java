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

        // NOVO: ID do Médico (retorna o profissional que realizou a consulta)
        Long medicoId,

        // Dados do atendimento
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String observacoesMedicas,
        String encaminhamentos,
        ClassificacaoFinal classificacaoFinal
) {
    // Construtor para mapeamento da Entidade Consulta para o DTO
    public ConsultaResponseDTO(Consulta consulta, AgendamentoDTO agendamentoDTO, TriagemResponseDTO triagemDTO) {
        this(
                consulta.getId(),
                agendamentoDTO,
                triagemDTO,
                // Mapeamento do ID do Médico
                consulta.getMedico() != null ? consulta.getMedico().getId() : null,
                consulta.getDataHoraInicio(),
                consulta.getDataHoraFim(),
                consulta.getObservacoesMedicas(),
                consulta.getEncaminhamentos(),
                consulta.getClassificacaoFinal()
        );
    }
}