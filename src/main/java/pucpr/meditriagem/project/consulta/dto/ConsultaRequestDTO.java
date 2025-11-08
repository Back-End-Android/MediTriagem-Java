package pucpr.meditriagem.project.consulta.dto;

import jakarta.validation.constraints.NotNull;
import pucpr.meditriagem.project.consulta.ClassificacaoFinal;

import java.time.LocalDateTime;

// DTO para receber os dados de criação ou atualização da consulta
public record ConsultaRequestDTO(
        // Chaves de ligação (Agendamento e Triagem são obrigatórios)
        @NotNull Long agendamentoId,
        @NotNull Long triagemId,

        // Dados do atendimento
        @NotNull LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim, // Pode ser nulo se a consulta não terminou
        String observacoesMedicas,
        String encaminhamentos,
        @NotNull ClassificacaoFinal classificacaoFinal // A classificação final é obrigatória ao criar
) {}