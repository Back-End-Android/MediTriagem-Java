package pucpr.meditriagem.project.especialidade.dto;

import pucpr.meditriagem.project.especialidade.Especialidade;

public record EspecialidadeResponseDTO(
        Long id,
        String nome
) {
    public EspecialidadeResponseDTO(Especialidade especialidade) {
        this(especialidade.getId(), especialidade.getNome());
    }
}