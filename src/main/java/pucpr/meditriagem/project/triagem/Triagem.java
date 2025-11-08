package pucpr.meditriagem.project.triagem;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;
import pucpr.meditriagem.project.usuario.Usuario;

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

    // Relacionamento com a classe paciente, trazendo: nome, idade, gênero e pré-disposição a doenças;
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Usuario paciente;

    // Relacionamento com a classe questionário
    @OneToOne(cascade = CascadeType.ALL)
    private QuestionarioSintomas questionario;


    // Desativa a triagem
    public Boolean setIsActivated(){
        this.isActivated = Boolean.FALSE;
        return isActivated;
    }

}
