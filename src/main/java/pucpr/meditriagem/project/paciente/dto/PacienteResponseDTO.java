package pucpr.meditriagem.project.paciente.dto;

import pucpr.meditriagem.project.paciente.Paciente;
import java.time.LocalDate;

public record PacienteResponseDTO(
        Long id,
        String nomeCompleto,
        String cpf,
        String genero,
        LocalDate dtNascimento,
        String email
) {

    public PacienteResponseDTO(Paciente paciente) {
        this(
                paciente.getId(),
                paciente.getNomeCompleto(),
                paciente.getCpf(),
                paciente.getGenero(),
                paciente.getDtNascimento(),
                paciente.getUsuario().getUsername()
        );
    }
}

