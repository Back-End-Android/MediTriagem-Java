package pucpr.meditriagem.project.medico.dto;

import pucpr.meditriagem.project.medico.Medico;
import java.time.LocalDate;


public record MedicoResponseDTO(
        Long id,
        String nomeCompleto,
        String crm,
        LocalDate dtNascimento,
        String email,
        String especialidadeNome
) {

    public MedicoResponseDTO(Medico medico) {
        this(
                medico.getId(),
                medico.getNomeCompleto(),
                medico.getCrm(),
                medico.getDtNascimento(),
                medico.getUsuario().getUsername(),
                medico.getEspecialidade().getNome()
        );
    }
}