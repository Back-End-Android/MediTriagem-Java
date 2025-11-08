package pucpr.meditriagem.project.triagem.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pucpr.meditriagem.project.questionario.*;
import pucpr.meditriagem.project.paciente.Paciente;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TriagemDTO {

    private Long id_triagem;

    // Dados do paciente (que vêm da classe Usuario - Paciente)
    Long id;
    String nome_completo;
    String genero;
    String dataNascimento;

    // Questionário
    private pucpr.meditriagem.project.dto.QuestionarioDTO questionario;
}

