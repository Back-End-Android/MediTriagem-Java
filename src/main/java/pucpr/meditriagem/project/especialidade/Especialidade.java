package pucpr.meditriagem.project.especialidade;

import jakarta.persistence.*;
import pucpr.meditriagem.project.medico.Medico;
import java.util.List;
import java.util.Objects;

@Entity(name = "Especialidade")
@Table(name = "especialidades")
public class Especialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nome;

    @OneToMany(mappedBy = "especialidade")
    private List<Medico> medicos;

    public Especialidade() {}

    public Especialidade(String nome) {
        this.nome = nome;
    }

    // getter e setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Medico> getMedicos() { return medicos; }
    public void setMedicos(List<Medico> medicos) { this.medicos = medicos; }

    // equals e hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Especialidade that = (Especialidade) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}