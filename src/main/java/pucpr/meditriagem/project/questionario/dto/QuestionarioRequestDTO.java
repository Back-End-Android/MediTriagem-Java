package pucpr.meditriagem.project.questionario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionarioRequestDTO {

    // ID do paciente
    private Long pacienteId;

    // Questionário de sintomas
    private QuestionarioDTO questionario;
}

