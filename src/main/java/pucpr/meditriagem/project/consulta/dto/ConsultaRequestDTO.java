package pucpr.meditriagem.project.consulta.dto;

import jakarta.validation.constraints.NotNull;
import pucpr.meditriagem.project.consulta.ClassificacaoFinal;

import java.time.LocalDateTime;

// DTO para receber os dados de criação ou atualização da consulta
public record ConsultaRequestDTO(
        // Chaves de ligação (Agendamento e Triagem são obrigatórios)
        @NotNull(message = "O ID do Agendamento é obrigatório.")
        Long agendamentoId,

        @NotNull(message = "O ID da Triagem é obrigatório.")
        Long triagemId,

        // NOVO: ID do Médico (obrigatório para a criação)
        @NotNull(message = "O ID do Médico é obrigatório.")
        Long medicoId,

        // Dados do atendimento
        @NotNull(message = "A data e hora de início da consulta é obrigatória.")
        LocalDateTime dataHoraInicio,

        LocalDateTime dataHoraFim, // Pode ser nulo se a consulta não terminou

        String observacoesMedicas,

        String encaminhamentos,

        @NotNull(message = "A Classificação Final é obrigatória.")
        ClassificacaoFinal classificacaoFinal // A classificação final é obrigatória ao criar
) {}