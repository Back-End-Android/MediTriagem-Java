package pucpr.meditriagem.project.questionario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questionarios_sintomas")
public class QuestionarioSintomas { // Classe para realizar o questionário online de sintomas

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Data e hora de criação do questionário
    @Column(name = "data_hora_criacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao;

    // Dados do paciente armazenados no questionário
    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;
    
    @Column(name = "nome_completo", nullable = false, length = 255)
    private String nomeCompleto;
    
    @Column(name = "genero", length = 50)
    private String genero;
    
    @Column(name = "idade")
    private Integer idade;

    // Sintomas do questionário
    @Column(name = "febre")
    private Boolean febre;
    
    @Column(name = "alteracao_pressao")
    private Boolean alteracaoPressao;
    
    @Column(name = "tontura")
    private Boolean tontura;
    
    @Column(name = "fraqueza")
    private Boolean fraqueza;
    
    @Column(name = "falta_de_ar")
    private Boolean faltaDeAr;
    
    @Column(name = "diarreia")
    private Boolean diarreia;
    
    @Column(name = "nausea")
    private Boolean nausea;
    
    @Column(name = "vomito")
    private Boolean vomito;

    @Column(name = "dor")
    private Boolean dor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "local_dor")
    private LocalCorpo localDor;

    @Column(name = "sangramento")
    private Boolean sangramento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "local_sangramento")
    private LocalCorpo localSangramento;

    @Column(name = "fratura")
    private Boolean fratura;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "local_fratura")
    private LocalCorpo localFratura;

    @Column(name = "tosse")
    private Boolean tosse;
    
    @Column(name = "tipo_tosse", length = 100)
    private String tipoTosse;
}
