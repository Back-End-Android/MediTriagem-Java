package pucpr.meditriagem.project.paciente.dto;

import java.time.LocalDate;

public record PacienteRequestDTO(
        // paciente
        String nomeCompleto,
        String cpf,
        String genero,
        LocalDate dtNascimento,

        // dados que também vão pro user
        String email,
        String senha
) {}

