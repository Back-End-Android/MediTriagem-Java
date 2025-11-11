package pucpr.meditriagem.project.agendamento.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AgendamentoDTO(
        @NotNull Long medicoId,
        @NotNull Long pacienteId,
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fim,
        @Size(max = 1000) String observacao
) { }
