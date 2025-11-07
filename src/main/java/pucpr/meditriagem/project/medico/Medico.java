package pucpr.meditriagem.project.medico;

import jakarta.persistence.*;
import pucpr.meditriagem.project.especialidade.Especialidade;
import pucpr.meditriagem.project.usuario.Usuario;

import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "Medico")
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;

    @Column(unique = true)
    private String cpf;

    @Column(unique = true)
    private String crm;

    private LocalDate dtNascimento;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "especialidade_id")
    private Especialidade especialidade;

    // --- CONSTRUTORES MANUAIS ---
    public Medico() {
    }

    public Medico(String nomeCompleto, String cpf, String crm, LocalDate dtNascimento, Usuario usuario, Especialidade especialidade) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.crm = crm;
        this.dtNascimento = dtNascimento;
        this.usuario = usuario;
        this.especialidade = especialidade;
    }

    public void atualizarDados(String nome, String crm, LocalDate dtNascimento, Especialidade especialidade) {
        if (nome != null) this.nomeCompleto = nome;
        if (crm != null) this.crm = crm;
        if (dtNascimento != null) this.dtNascimento = dtNascimento;
        if (especialidade != null) this.especialidade = especialidade;
    }

    //  GETTERS E SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public LocalDate getDtNascimento() { return dtNascimento; }
    public void setDtNascimento(LocalDate dtNascimento) { this.dtNascimento = dtNascimento; }

    // getters do MedicoResponseDTO
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Especialidade getEspecialidade() { return especialidade; }
    public void setEspecialidade(Especialidade especialidade) { this.especialidade = especialidade; }

    // --- Equals e HashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medico medico = (Medico) o;
        return Objects.equals(id, medico.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}