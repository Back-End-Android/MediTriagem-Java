package pucpr.meditriagem.project.triagem;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;

@Entity
@Getter
@Setter
@Table(name = "triagem")
@NoArgsConstructor
@AllArgsConstructor
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_triagem;

    private Boolean isActivated = Boolean.TRUE;

    // ID do paciente para controle (referência ao Paciente)
    @Column(name = "paciente_id")
    private Long pacienteId;

    // Dados específicos do paciente armazenados na triagem
    private String nomeCompleto;
    private String genero;
    private Integer idade;

    // Relacionamento com a classe questionário (objeto completo)
    @OneToOne(cascade = CascadeType.ALL)
    private QuestionarioSintomas questionario;


    // Desativa a triagem
    public Boolean setIsActivated(){
        this.isActivated = Boolean.FALSE;
        return isActivated;
    }

}
