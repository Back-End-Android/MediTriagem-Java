package pucpr.meditriagem.project.triagem.dto;

import lombok.Getter;
import lombok.Setter;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;
import pucpr.meditriagem.project.triagem.NivelUrgencia;
import pucpr.meditriagem.project.triagem.Triagem;

import java.time.LocalDateTime;

@Getter
@Setter
public class TriagemResponseDTO {

    private Long id_triagem;
    private LocalDateTime dataHoraCriacao;
    private Boolean isActivated;

    // IDs de relacionamento
    private Long enfermeiroId;
    private Long pacienteId;
    private Long questionarioId;

    // Dados vitais medidos pelo enfermeiro
    private String pressaoArterial;
    private Integer frequenciaCardiaca;
    private Integer saturacao;
    private String sinaisDesidratacao;
    private Double temperatura;

    // Perguntas adicionais
    private String preCondicao;
    private Boolean tomaRemedioControlado;
    private String qualRemedio;
    private String historicoFamiliar;
    private String observacao;
    private NivelUrgencia nivelUrgencia;

    // Questionário completo
    private QuestionarioSintomas questionario;

    public TriagemResponseDTO(Triagem triagem) {
        this.id_triagem = triagem.getId_triagem();
        this.dataHoraCriacao = triagem.getDataHoraCriacao();
        this.isActivated = triagem.getIsActivated();
        this.enfermeiroId = triagem.getEnfermeiroId();
        this.pacienteId = triagem.getPacienteId();
        this.questionarioId = triagem.getQuestionario() != null ? triagem.getQuestionario().getId() : null;
        this.pressaoArterial = triagem.getPressaoArterial();
        this.frequenciaCardiaca = triagem.getFrequenciaCardiaca();
        this.saturacao = triagem.getSaturacao();
        this.sinaisDesidratacao = triagem.getSinaisDesidratacao();
        this.temperatura = triagem.getTemperatura();
        this.preCondicao = triagem.getPreCondicao();
        this.tomaRemedioControlado = triagem.getTomaRemedioControlado();
        this.qualRemedio = triagem.getQualRemedio();
        this.historicoFamiliar = triagem.getHistoricoFamiliar();
        this.observacao = triagem.getObservacao();
        this.nivelUrgencia = triagem.getNivelUrgencia();
        this.questionario = triagem.getQuestionario();
    }
}

