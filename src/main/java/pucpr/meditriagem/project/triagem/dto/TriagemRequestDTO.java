package pucpr.meditriagem.project.triagem.dto;

import lombok.Getter;
import lombok.Setter;
import pucpr.meditriagem.project.triagem.NivelUrgencia;

@Getter
@Setter
public class TriagemRequestDTO {

    // IDs de relacionamento
    private Long enfermeiroId;
    private Long pacienteId;
    private Long questionarioId;

    // Dados vitais medidos pelo enfermeiro
    private String pressaoArterial;
    private Integer frequenciaCardiaca;
    private Integer saturacao;
    private String sinaisDesidratacao;
    private Double temperatura; // medida se houver febre no questionário

    // Perguntas adicionais
    private String preCondicao;
    private Boolean tomaRemedioControlado;
    private String qualRemedio;
    private String historicoFamiliar;
    private String observacao;
    private NivelUrgencia nivelUrgencia; // será calculado depois
}

