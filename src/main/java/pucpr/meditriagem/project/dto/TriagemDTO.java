package pucpr.meditriagem.project.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pucpr.meditriagem.project.triagem.LocalCorpo;

@Getter
@Setter
public class TriagemDTO {

    private Long id_triagem;

    // Dados do paciente (que vêm da classe Paciente)
    private Long pacienteId;
    @NotNull(message = "{error.nome.notnull}")
    @NotBlank(message = "{error.nome.notblank}")
    private String nomePaciente;
    private Integer idade;
    private String genero;

    // Questionário de sintomas
    private boolean febre;
    private boolean tosse;
    private boolean sangramento;
    private boolean alteracaoPressao;
    private boolean fratura;

    // Campos descritivos extras
    private LocalCorpo localDor;
    private LocalCorpo localFratura;
    private LocalCorpo localSangramento;
    private String tipoTosse;
}

