package pucpr.meditriagem.project.paciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.paciente.dto.PacienteRequestDTO;
import pucpr.meditriagem.project.paciente.dto.PacienteResponseDTO;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PacienteResponseDTO salvar(PacienteRequestDTO dados) {

        // Validação
        if (pacienteRepository.findByCpf(dados.cpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado"); // TODO: Exceção customizada
        }
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado"); // TODO: Exceção customizada
        }

        // Cria o Usuario
        var senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(
                null,
                dados.email(),
                senhaCriptografada,
                Cargo.PACIENTE // Define a permissão
        );

        // Cria o Paciente
        var paciente = new Paciente(
                dados.nomeCompleto(),
                dados.cpf(),
                dados.genero(),
                dados.dtNascimento(),
                usuario, // Liga o Usuario
                dados.email(),
                senhaCriptografada
        );

        // Salva o Paciente (Cascade salva o Usuario junto)
        pacienteRepository.save(paciente);

        return new PacienteResponseDTO(paciente);
    }

    // listar
    public List<PacienteResponseDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(PacienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    // buscar por ID
    public PacienteResponseDTO buscarPorId(Long id) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        return new PacienteResponseDTO(paciente);
    }

    // alterar
    public PacienteResponseDTO alterar(Long id, PacienteRequestDTO dados) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        // Validação de CPF duplicado (se mudou o CPF)
        if (!paciente.getCpf().equals(dados.cpf())) {
            if (pacienteRepository.findByCpf(dados.cpf()).isPresent()) {
                throw new RuntimeException("CPF já cadastrado");
            }
        }

        // Validação de email duplicado (se mudou o email)
        if (!paciente.getUsuario().getEmail().equals(dados.email())) {
            if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
                throw new RuntimeException("Email já cadastrado");
            }
        }

        // Atualiza o CPF se mudou
        if (!paciente.getCpf().equals(dados.cpf())) {
            paciente.setCpf(dados.cpf());
        }

        // Atualiza os dados do paciente
        paciente.atualizarDados(
                dados.nomeCompleto(),
                dados.genero(),
                dados.dtNascimento(),
                dados.email(),
                dados.senha() != null && !dados.senha().isEmpty() ? passwordEncoder.encode(dados.senha()) : paciente.getSenha()
        );

        // Atualiza o email do usuário se mudou
        if (!paciente.getUsuario().getEmail().equals(dados.email())) {
            paciente.getUsuario().setEmail(dados.email());
        }

        // Atualiza a senha do usuário se foi fornecida
        if (dados.senha() != null && !dados.senha().isEmpty()) {
            paciente.getUsuario().setSenha(passwordEncoder.encode(dados.senha()));
        }

        pacienteRepository.save(paciente);
        return new PacienteResponseDTO(paciente);
    }

    // excluir
    public void excluir(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new RuntimeException("Paciente não encontrado");
        }
        // O @OneToOne com orphanRemoval=true deleta o Usuario junto
        pacienteRepository.deleteById(id);
    }
}

