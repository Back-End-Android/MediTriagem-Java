package pucpr.meditriagem.project.questionario.dto;

import lombok.Getter;
import lombok.Setter;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;
import pucpr.meditriagem.project.questionario.LocalCorpo;

@Getter
@Setter
public class QuestionarioResponseDTO {

    private Long id;

    // Dados do paciente
    private Long pacienteId;
    private String nomeCompleto;
    private String genero;
    private Integer idade;

    // Sintomas do questionário
    private Boolean febre;
    private Boolean alteracaoPressao;
    private Boolean tontura;
    private Boolean fraqueza;
    private Boolean faltaDeAr;
    private Boolean diarreia;
    private Boolean nausea;
    private Boolean vomito;
    private Boolean dor;
    private LocalCorpo localDor;
    private Boolean sangramento;
    private LocalCorpo localSangramento;
    private Boolean fratura;
    private LocalCorpo localFratura;
    private Boolean tosse;
    private String tipoTosse;

    public QuestionarioResponseDTO(QuestionarioSintomas questionario) {
        this.id = questionario.getId();
        this.pacienteId = questionario.getPacienteId();
        this.nomeCompleto = questionario.getNomeCompleto();
        this.genero = questionario.getGenero();
        this.idade = questionario.getIdade();
        this.febre = questionario.getFebre();
        this.alteracaoPressao = questionario.getAlteracaoPressao();
        this.tontura = questionario.getTontura();
        this.fraqueza = questionario.getFraqueza();
        this.faltaDeAr = questionario.getFaltaDeAr();
        this.diarreia = questionario.getDiarreia();
        this.nausea = questionario.getNausea();
        this.vomito = questionario.getVomito();
        this.dor = questionario.getDor();
        this.localDor = questionario.getLocalDor();
        this.sangramento = questionario.getSangramento();
        this.localSangramento = questionario.getLocalSangramento();
        this.fratura = questionario.getFratura();
        this.localFratura = questionario.getLocalFratura();
        this.tosse = questionario.getTosse();
        this.tipoTosse = questionario.getTipoTosse();
    }
}

