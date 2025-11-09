package pucpr.meditriagem.project.enfermeiro.dto;

import pucpr.meditriagem.project.enfermeiro.Enfermeiro;
import java.time.LocalDate;

public record EnfermeiroResponseDTO(
        Long id,
        String nomeCompleto,
        String cpf,
        String coren,
        LocalDate dtNascimento,
        String email
) {
    public EnfermeiroResponseDTO(Enfermeiro e) {
        this(
                e.getId(),
                e.getNomeCompleto(),
                e.getCpf(),
                e.getCoren(),
                e.getDtNascimento(),
                e.getUsuario().getUsername()  // email
        );
    }
}
