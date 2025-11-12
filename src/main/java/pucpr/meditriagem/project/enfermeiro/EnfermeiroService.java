package pucpr.meditriagem.project.enfermeiro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroRequestDTO;
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroResponseDTO;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnfermeiroService {

    @Autowired private EnfermeiroRepository enfermeiroRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // CREATE
    public EnfermeiroResponseDTO cadastrar(EnfermeiroRequestDTO dados) {
        if (enfermeiroRepository.existsByCpf(dados.cpf()))
            throw new BusinessRuleException("enfermeiro.cpf.duplicado");
        if (enfermeiroRepository.existsByCoren(dados.coren()))
            throw new BusinessRuleException("enfermeiro.coren.duplicado");
        if (usuarioRepository.findByEmail(dados.email()).isPresent())
            throw new BusinessRuleException("enfermeiro.email.duplicado");

        var senhaHash = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(
                null,
                dados.email(),
                senhaHash,
                Cargo.ENFERMEIRO
        );

        var enfermeiro = new Enfermeiro(
                dados.nomeCompleto(),
                dados.cpf(),
                dados.coren(),
                dados.dtNascimento(),
                usuario
        );

        // Mock
        Enfermeiro enfermeiroSalvo = enfermeiroRepository.save(enfermeiro);
        return new EnfermeiroResponseDTO(enfermeiroSalvo);
    }

    // READ (lista)
    public List<EnfermeiroResponseDTO> listarTodos() {
        return enfermeiroRepository.findAll()
                .stream().map(EnfermeiroResponseDTO::new).collect(Collectors.toList());
    }

    // READ (por id)
    public EnfermeiroResponseDTO buscarPorId(Long id) {
        var e = enfermeiroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("enfermeiro.not_found"));
        return new EnfermeiroResponseDTO(e);
    }

    // UPDATE
    public EnfermeiroResponseDTO alterar(Long id, EnfermeiroRequestDTO dados) {
        var e = enfermeiroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("enfermeiro.not_found"));

        // se trocou COREN, valida duplicidade
        if (dados.coren() != null && !dados.coren().equals(e.getCoren())
                && enfermeiroRepository.existsByCoren(dados.coren())) {
            throw new BusinessRuleException("enfermeiro.coren.duplicado");
        }

        e.atualizar(dados.nomeCompleto(), dados.coren(), dados.dtNascimento());

        // opcional: trocar email/senha do usuário
        if (dados.email() != null && !dados.email().equals(e.getUsuario().getUsername())) {
            if (usuarioRepository.findByEmail(dados.email()).isPresent())
                throw new BusinessRuleException("enfermeiro.email.duplicado");
            e.getUsuario().setEmail(dados.email());
        }
        if (dados.senha() != null && !dados.senha().isBlank()) {
            e.getUsuario().setSenha(passwordEncoder.encode(dados.senha()));
        }

        enfermeiroRepository.save(e);
        return new EnfermeiroResponseDTO(e);
    }

    // DELETE
    public void excluir(Long id) {
        var e = enfermeiroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("enfermeiro.not_found"));
        enfermeiroRepository.delete(e);
    }
}