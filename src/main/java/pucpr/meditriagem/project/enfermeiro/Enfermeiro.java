package pucpr.meditriagem.project.enfermeiro;

import jakarta.persistence.*;
import pucpr.meditriagem.project.usuario.Usuario;

import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "Enfermeiro")
@Table(name = "enfermeiro",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_enf_cpf", columnNames = "cpf"),
                @UniqueConstraint(name="uk_enf_coren", columnNames = "coren")
        })

public class Enfermeiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nome_completo", nullable=false, length=120)
    private String nomeCompleto;

    @Column(nullable=false, length=11)
    private String cpf;

    @Column(nullable=false, length=20)
    private String coren;

    @Column(name="dt_nascimento", nullable=false)
    private LocalDate dtNascimento;

    // login/autorização
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="usuario_id", nullable=false)
    private Usuario usuario;

    public Enfermeiro() {}

    public Enfermeiro(String nomeCompleto, String cpf, String coren, LocalDate dtNascimento, Usuario usuario) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.coren = coren;
        this.dtNascimento = dtNascimento;
        this.usuario = usuario;
    }

    // atualização parcial (para o PUT)
    public void atualizar(String nomeCompleto, String coren, LocalDate dtNascimento) {
        if (nomeCompleto != null && !nomeCompleto.isBlank()) this.nomeCompleto = nomeCompleto;
        if (coren != null && !coren.isBlank()) this.coren = coren;
        if (dtNascimento != null) this.dtNascimento = dtNascimento;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCoren() { return coren; }
    public void setCoren(String coren) { this.coren = coren; }
    public LocalDate getDtNascimento() { return dtNascimento; }
    public void setDtNascimento(LocalDate dtNascimento) { this.dtNascimento = dtNascimento; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enfermeiro that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
