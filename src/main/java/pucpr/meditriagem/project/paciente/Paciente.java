package pucpr.meditriagem.project.paciente;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pucpr.meditriagem.project.usuario.Usuario;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
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

    private String email;

    private String senha;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    // --- CONSTRUTORES MANUAIS ---
    public Paciente() {
    }

    public Paciente(String nomeCompleto, String cpf, String genero, LocalDate dtNascimento, Usuario usuario, String email, String senha) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.genero = genero;
        this.dtNascimento = dtNascimento;
        this.usuario = usuario;
        this.email = email;
        this.senha = senha;
    }

    public void atualizarDados(String nome, String genero, LocalDate dtNascimento, String email, String senha) {
        if (nome != null) this.nomeCompleto = nome;
        if (genero != null) this.genero = genero;
        if (dtNascimento != null) this.dtNascimento = dtNascimento;
        if (email != null) this.email = email;
        if (senha != null) this.senha = senha;
    }

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
