package pucpr.meditriagem.project.medico.dto;

import java.time.LocalDate;


public record MedicoRequestDTO(
        // medico
        String nomeCompleto,
        String cpf,
        String crm,
        LocalDate dtNascimento,
        Long especialidadeId,

        //dados que tambem vao pro user
        String email,
        String senha
) {}