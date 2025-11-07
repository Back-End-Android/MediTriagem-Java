package pucpr.meditriagem.project.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AgendamentoDTO(
        Long id,
        @NotNull Long medicoId,
        @NotNull Long pacienteId,
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fim,
        @Size(max = 1000) String observacao
) { }
