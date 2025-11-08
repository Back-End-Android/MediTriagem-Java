package pucpr.meditriagem.project.paciente;

import jakarta.persistence.*;
import pucpr.meditriagem.project.usuario.Usuario;

import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "Paciente")
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;

    @Column(unique = true)
    private String cpf;

    private String genero;

    private LocalDate dtNascimento;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    // --- CONSTRUTORES MANUAIS ---
    public Paciente() {
    }

    public Paciente(String nomeCompleto, String cpf, String genero, LocalDate dtNascimento, Usuario usuario) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.genero = genero;
        this.dtNascimento = dtNascimento;
        this.usuario = usuario;
    }

    public void atualizarDados(String nome, String genero, LocalDate dtNascimento) {
        if (nome != null) this.nomeCompleto = nome;
        if (genero != null) this.genero = genero;
        if (dtNascimento != null) this.dtNascimento = dtNascimento;
    }

    //  GETTERS E SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public LocalDate getDtNascimento() { return dtNascimento; }
    public void setDtNascimento(LocalDate dtNascimento) { this.dtNascimento = dtNascimento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // --- Equals e HashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(id, paciente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
