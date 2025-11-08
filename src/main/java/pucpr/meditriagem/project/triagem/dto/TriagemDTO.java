package pucpr.meditriagem.project.triagem.dto;
import pucpr.meditriagem.project.questionario.dto.QuestionarioDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TriagemDTO {

    private Long id_triagem;

    // Dados específicos do paciente (não o objeto completo)
    private Long pacienteId;
    private String nomeCompleto;
    private String genero;
    private Integer idade;

    // Questionário completo
    private QuestionarioDTO questionario;
}

