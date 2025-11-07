package pucpr.meditriagem.project.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TriagemDTO {

    private Long id_triagem;

    // Dados do paciente (que vêm da classe Usuario - Paciente)
    private UserDTO usuario;


//    private Integer idade;
//    private String genero;

    private QuestionarioDTO questionario;
}

