package pucpr.meditriagem.project.enfermeiro.dto;

import java.time.LocalDate;

public record EnfermeiroRequestDTO(
        String nomeCompleto,
        String cpf,
        String coren,
        LocalDate dtNascimento,
        String email,
        String senha
) {}
