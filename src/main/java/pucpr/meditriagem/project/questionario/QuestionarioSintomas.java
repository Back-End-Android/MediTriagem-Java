package pucpr.meditriagem.project.questionario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionarioSintomas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean febre;
    private String detalheFebre;

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

    private Boolean alteracaoPressao;
    private String detalhePressao;

    private Boolean fratura;
    private LocalCorpo localFratura;

    private Boolean tosse;
    private String tipoTosse;
}
