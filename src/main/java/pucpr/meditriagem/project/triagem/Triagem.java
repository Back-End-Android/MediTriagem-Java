package pucpr.meditriagem.project.triagem;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;
import pucpr.meditriagem.project.triagem.NivelUrgencia;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "triagem")
@NoArgsConstructor
@AllArgsConstructor
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_triagem")
    private Long id_triagem;

    // Data e hora de criação da triagem
    @Column(name = "data_hora_criacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated = Boolean.TRUE;

    // IDs de relacionamento
    @Column(name = "enfermeiro_id", nullable = false)
    private Long enfermeiroId;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    // Relacionamento com a classe questionário (objeto completo que contém dados do paciente)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "questionario_id", referencedColumnName = "id")
    private QuestionarioSintomas questionario;

    // Dados vitais medidos pelo enfermeiro
    @Column(name = "pressao_arterial", length = 20)
    private String pressaoArterial; // Ex: "120/80"

    @Column(name = "frequencia_cardiaca")
    private Integer frequenciaCardiaca; // batimentos por minuto

    @Column(name = "saturacao")
    private Integer saturacao; // porcentagem de saturação de oxigênio

    @Column(name = "sinais_desidratacao", length = 500)
    private String sinaisDesidratacao; // descrição dos sinais

    @Column(name = "temperatura")
    private Double temperatura; // temperatura em graus Celsius (medida se houver febre no questionário)

    // Perguntas adicionais
    @Column(name = "pre_condicao", length = 500)
    private String preCondicao; // pré-condição do paciente (ex: diabetes, hipertensão, etc.)

    @Column(name = "toma_remedio_controlado")
    private Boolean tomaRemedioControlado;

    @Column(name = "qual_remedio", length = 500)
    private String qualRemedio; // qual remédio controlado (se toma)

    @Column(name = "historico_familiar", length = 1000)
    private String historicoFamiliar; // histórico familiar de doenças

    @Column(name = "observacao", length = 2000)
    private String observacao; // observações gerais do enfermeiro

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_urgencia", nullable = false)
    private NivelUrgencia nivelUrgencia; // será calculado depois baseado nos sintomas

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    // Desativa a triagem
    public void desativar() {
        this.isActivated = Boolean.FALSE;
    }
}
