package pucpr.meditriagem.project.usuario;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity(name = "Usuario")
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String senha;

    @Enumerated(EnumType.STRING)
    private Cargo cargo;

    // --- CONSTRUTORES MANUAIS ---
    // 1. Construtor vazio (obrigatório pelo JPA)
    public Usuario() {
    }

    // 2. Construtor completo (que o MedicoService usa)
    public Usuario(Long id, String email, String senha, Cargo cargo) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
    }

    // --- GETTERS E SETTERS MANUAIS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Este é o 'getEmail()' que faltava
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Este é o 'getCargo()'
    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public void setSenha(String senha) { this.senha = senha; }

    // --- MÉTODOS DO UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.cargo.name()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        // O login é o email
        return this.email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // --- Equals e HashCode (Boa prática) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}