package pucpr.meditriagem.project.consulta;

import jakarta.persistence.*;
import pucpr.meditriagem.project.agendamento.Agendamento;
import pucpr.meditriagem.project.triagem.Triagem;
import java.time.LocalDateTime;

@Entity(name = "Consulta")
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento 1:1 com o Agendamento (Lado Dono/Ativo, cria a FK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id", unique = true, nullable = false)
    private Agendamento agendamento;

    // Relacionamento 1:1 com a Triagem (Lado Dono/Ativo, cria a FK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triagem_id", unique = true, nullable = false)
    private Triagem triagem;

    // --- Dados do Atendimento Médico ---

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    private LocalDateTime dataHoraFim;

    @Column(columnDefinition = "TEXT")
    private String observacoesMedicas;

    @Column(columnDefinition = "TEXT")
    private String encaminhamentos;

    // Classificação de risco reavaliada pelo médico
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private ClassificacaoFinal classificacaoFinal;

    // Construtores
    public Consulta() {
    }

    public Consulta(Long id, Agendamento agendamento, Triagem triagem, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, String observacoesMedicas, String encaminhamentos, ClassificacaoFinal classificacaoFinal) {
        this.id = id;
        this.agendamento = agendamento;
        this.triagem = triagem;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.observacoesMedicas = observacoesMedicas;
        this.encaminhamentos = encaminhamentos;
        this.classificacaoFinal = classificacaoFinal;
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public Triagem getTriagem() {
        return triagem;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public String getObservacoesMedicas() {
        return observacoesMedicas;
    }

    public String getEncaminhamentos() {
        return encaminhamentos;
    }

    public ClassificacaoFinal getClassificacaoFinal() {
        return classificacaoFinal;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public void setTriagem(Triagem triagem) {
        this.triagem = triagem;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public void setObservacoesMedicas(String observacoesMedicas) {
        this.observacoesMedicas = observacoesMedicas;
    }

    public void setEncaminhamentos(String encaminhamentos) {
        this.encaminhamentos = encaminhamentos;
    }

    public void setClassificacaoFinal(ClassificacaoFinal classificacaoFinal) {
        this.classificacaoFinal = classificacaoFinal;
    }
}
