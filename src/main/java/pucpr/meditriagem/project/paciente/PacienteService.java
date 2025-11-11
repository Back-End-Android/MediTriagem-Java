package pucpr.meditriagem.project.paciente;

// --- MUDANÇA (REQ. 8): Importar as novas exceções ---
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ForbiddenOperationException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
// --- FIM DA MUDANÇA ---

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.access.AccessDeniedException; // <-- Não precisamos mais deste
import org.springframework.security.core.context.SecurityContextHolder;
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

        if (pacienteRepository.findByCpf(dados.cpf()).isPresent()) {
            throw new BusinessRuleException("paciente.cpf.duplicado");
        }
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new BusinessRuleException("paciente.email.duplicado");
        }

        // Cria o Usuario
        var senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(
                null,
                dados.email(),
                senhaCriptografada,
                Cargo.PACIENTE
        );

        var paciente = new Paciente(
                dados.nomeCompleto(),
                dados.cpf(),
                dados.genero(),
                dados.dtNascimento(),
                usuario,
                dados.email(),
                senhaCriptografada
        );

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
                .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));
        return new PacienteResponseDTO(paciente);
    }

    // alterar
    public PacienteResponseDTO alterar(Long id, PacienteRequestDTO dados) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));


        // Validação de CPF duplicado
        if (!paciente.getCpf().equals(dados.cpf())) {
            if (pacienteRepository.findByCpf(dados.cpf()).isPresent()) {
                throw new BusinessRuleException("paciente.cpf.duplicado");
            }
        }

        // Validação de email duplicado
        if (!paciente.getUsuario().getEmail().equals(dados.email())) {
            if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
                throw new BusinessRuleException("paciente.email.duplicado");
            }
        }

        var usuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!paciente.getUsuario().getEmail().equals(usuarioLogado)) {
            throw new ForbiddenOperationException("paciente.unauthorized");
        }

        // Atualiza os dados do paciente (COM A SUA LÓGICA DE DUPLICAÇÃO)
        paciente.atualizarDados(
                dados.nomeCompleto(),
                dados.genero(),
                dados.dtNascimento(),
                dados.email(),
                dados.senha() != null && !dados.senha().isEmpty() ? passwordEncoder.encode(dados.senha()) : paciente.getSenha()
        );

        // Atualiza o CPF se mudou
        if (!paciente.getCpf().equals(dados.cpf())) {
            paciente.setCpf(dados.cpf());
        }

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

    // Excluir Paciente
    public void excluir(Long id) {
        var paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));


        var usuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!paciente.getUsuario().getEmail().equals(usuarioLogado)) {
            throw new ForbiddenOperationException("paciente.unauthorized");
        }

        pacienteRepository.deleteById(id);
    }
}