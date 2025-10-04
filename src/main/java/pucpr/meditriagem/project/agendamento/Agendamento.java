package pucpr.meditriagem.project.agendamento;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mantemos simples: apenas IDs, sem @ManyToOne
    @Setter
    @Column(nullable = false)
    private Long medicoId;

    @Setter
    @Column(nullable = false)
    private Long pacienteId;

    @Setter
    @Column(nullable = false)
    private LocalDateTime inicio;

    @Setter
    @Column(nullable = false)
    private LocalDateTime fim;

    @Setter
    @Column(length = 1000)
    private String observacao;

    // getters/setters
    public Long getId() { return id; }

    public Long getMedicoId() { return medicoId; }

    public Long getPacienteId() { return pacienteId; }

    public LocalDateTime getInicio() { return inicio; }

    public LocalDateTime getFim() { return fim; }

    public String getObservacao() { return observacao; }
}
