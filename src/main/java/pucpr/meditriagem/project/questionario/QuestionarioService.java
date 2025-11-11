// No QuestionarioService.java
package pucpr.meditriagem.project.questionario;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.exceptions.ForbiddenOperationException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.paciente.Paciente;
import pucpr.meditriagem.project.paciente.PacienteRepository;
import pucpr.meditriagem.project.questionario.dto.QuestionarioDTO;
import pucpr.meditriagem.project.questionario.dto.QuestionarioResponseDTO;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionarioService {

    private final QuestionarioRepository questionarioRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor com injeção de dependências
    public QuestionarioService(QuestionarioRepository questionarioRepository,
                               PacienteRepository pacienteRepository,
                               UsuarioRepository usuarioRepository) {
        this.questionarioRepository = questionarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Cria um novo questionário para um paciente
    public QuestionarioResponseDTO criarQuestionario(Long pacienteId, QuestionarioDTO questionarioDTO) {
        // Busca o paciente pelo ID
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));

        // Verifica se o usuário logado é o próprio paciente
        verificarSePacienteLogado(pacienteId);

        // Extrai os dados específicos do paciente
        String nomeCompleto = paciente.getNomeCompleto();
        String genero = paciente.getGenero();
        LocalDate dtNascimento = paciente.getDtNascimento();

        Integer idade = calcularIdade(dtNascimento); // Calcula a idade a partir da data de nascimento

        // Converte QuestionarioDTO para QuestionarioSintomas e adiciona dados do paciente
        QuestionarioSintomas questionario = converterDTOParaQuestionario(questionarioDTO, pacienteId, nomeCompleto, genero, idade);

        questionario.setDataHoraCriacao(LocalDateTime.now()); // Define a data e hora de criação

        // Salva o questionário
        QuestionarioSintomas questionarioSalvo = questionarioRepository.save(questionario);
        return new QuestionarioResponseDTO(questionarioSalvo);
    }

    // Altera um questionário existente (apenas o próprio paciente)
    public QuestionarioResponseDTO alterarQuestionario(Long questionarioId, QuestionarioDTO questionarioDTO) {
        // Busca o questionário existente
        QuestionarioSintomas questionario = questionarioRepository.findById(questionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("questionario.not_found"));

        // Verifica se o usuário logado é o paciente dono do questionário
        verificarSePacienteLogado(questionario.getPacienteId());

        // Atualiza todos os campos do questionário
        questionario.setFebre(questionarioDTO.febre());
        questionario.setAlteracaoPressao(questionarioDTO.alteracaoPressao());
        questionario.setTontura(questionarioDTO.tontura());
        questionario.setFraqueza(questionarioDTO.fraqueza());
        questionario.setFaltaDeAr(questionarioDTO.faltaDeAr());
        questionario.setDiarreia(questionarioDTO.diarreia());
        questionario.setNausea(questionarioDTO.nausea());
        questionario.setVomito(questionarioDTO.vomito());
        questionario.setDor(questionarioDTO.dor());
        questionario.setLocalDor(questionarioDTO.localDor());
        questionario.setSangramento(questionarioDTO.sangramento());
        questionario.setLocalSangramento(questionarioDTO.localSangramento());
        questionario.setFratura(questionarioDTO.fratura());
        questionario.setLocalFratura(questionarioDTO.localFratura());
        questionario.setTosse(questionarioDTO.tosse());
        questionario.setTipoTosse(questionarioDTO.tipoTosse());

        QuestionarioSintomas questionarioAtualizado = questionarioRepository.save(questionario);
        return new QuestionarioResponseDTO(questionarioAtualizado);
    }

    // Verifica se o usuário logado é o paciente
    private void verificarSePacienteLogado(Long pacienteId) {
        Usuario usuarioLogado = getUsuarioLogado();

        // Busca o paciente pelo ID
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));

        // Buscar paciente pelo usuário logado e comparar IDs
        Paciente pacienteDoUsuario = pacienteRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));

        if (!pacienteId.equals(pacienteDoUsuario.getId())) {
            throw new ForbiddenOperationException("questionario.unauthorized.paciente");
        }
    }

    // MÉTODO AUXILIAR CORRIGIDO: Verifica permissão de acesso para um questionário específico
    private void verificarPermissaoAcesso(QuestionarioSintomas questionario) {
        Usuario usuarioLogado = getUsuarioLogado();

        // Admin e enfermeiro podem acessar qualquer questionário
        if (usuarioLogado.getCargo() == pucpr.meditriagem.project.usuario.Cargo.ADMIN ||
                usuarioLogado.getCargo() == pucpr.meditriagem.project.usuario.Cargo.ENFERMEIRO) {
            return;
        }

        // Paciente pode acessar apenas seus próprios questionários
        if (usuarioLogado.getCargo() == pucpr.meditriagem.project.usuario.Cargo.PACIENTE) {
            // Busca o paciente associado ao usuário logado
            Paciente paciente = pacienteRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));

            if (!questionario.getPacienteId().equals(paciente.getId())) {
                throw new ForbiddenOperationException("questionario.unauthorized.view");
            }
            return;
        }

        throw new ForbiddenOperationException("questionario.unauthorized");
    }

    // MÉTODO AUXILIAR CORRIGIDO: Verifica permissão de acesso para questionários de um paciente
    private void verificarPermissaoAcessoPaciente(Long pacienteId) {
        Usuario usuarioLogado = getUsuarioLogado();

        // Admin e enfermeiro podem acessar qualquer questionário
        if (usuarioLogado.getCargo() == pucpr.meditriagem.project.usuario.Cargo.ADMIN ||
                usuarioLogado.getCargo() == pucpr.meditriagem.project.usuario.Cargo.ENFERMEIRO) {
            return;
        }

        // Paciente pode acessar apenas seus próprios questionários
        if (usuarioLogado.getCargo() == pucpr.meditriagem.project.usuario.Cargo.PACIENTE) {
            // Busca o paciente associado ao usuário logado
            Paciente paciente = pacienteRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("paciente.not_found"));

            if (!pacienteId.equals(paciente.getId())) {
                throw new ForbiddenOperationException("questionario.unauthorized.view");
            }
            return;
        }

        throw new ForbiddenOperationException("questionario.unauthorized");
    }

    // Obtém o usuário logado
    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenOperationException("usuario.not_authenticated");
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("usuario.not_found"));
    }

    // Calcula a idade a partir da data de nascimento
    private Integer calcularIdade(LocalDate dtNascimento) {
        if (dtNascimento == null) {
            return null;
        }
        return Period.between(dtNascimento, LocalDate.now()).getYears();
    }

    // Converte QuestionarioDTO para QuestionarioSintomas e adiciona dados do paciente
    private QuestionarioSintomas converterDTOParaQuestionario(QuestionarioDTO dto, Long pacienteId, String nomeCompleto, String genero, Integer idade) {
        QuestionarioSintomas questionario = new QuestionarioSintomas();

        // Dados do paciente
        questionario.setPacienteId(pacienteId);
        questionario.setNomeCompleto(nomeCompleto);
        questionario.setGenero(genero);
        questionario.setIdade(idade);

        // Sintomas do questionário
        questionario.setFebre(dto.febre());
        questionario.setTontura(dto.tontura());
        questionario.setFraqueza(dto.fraqueza());
        questionario.setFaltaDeAr(dto.faltaDeAr());
        questionario.setDiarreia(dto.diarreia());
        questionario.setNausea(dto.nausea());
        questionario.setVomito(dto.vomito());
        questionario.setDor(dto.dor());
        questionario.setTosse(dto.tosse());
        questionario.setSangramento(dto.sangramento());
        questionario.setAlteracaoPressao(dto.alteracaoPressao());
        questionario.setFratura(dto.fratura());
        questionario.setLocalDor(dto.localDor());
        questionario.setLocalFratura(dto.localFratura());
        questionario.setLocalSangramento(dto.localSangramento());
        questionario.setTipoTosse(dto.tipoTosse());

        return questionario;
    }

    // Busca todos os questionários
    public List<QuestionarioResponseDTO> findAll() {
        List<QuestionarioSintomas> questionarios = questionarioRepository.findAll();
        return questionarios.stream()
                .map(QuestionarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Busca questionário por ID
    public QuestionarioResponseDTO findById(Long id) {
        QuestionarioSintomas questionario = questionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("questionario.not_found"));

        // Verifica permissão de acesso
        verificarPermissaoAcesso(questionario);

        return new QuestionarioResponseDTO(questionario);
    }

    // Busca questionários por paciente
    public List<QuestionarioResponseDTO> buscarPorPacienteId(Long pacienteId) {
        // Verifica se o usuário logado tem permissão
        verificarPermissaoAcessoPaciente(pacienteId);

        List<QuestionarioSintomas> questionarios = questionarioRepository.buscarPorPacienteId(pacienteId);
        return questionarios.stream()
                .map(QuestionarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Salva um questionário
    public QuestionarioSintomas save(QuestionarioSintomas questionario) {
        return questionarioRepository.save(questionario);
    }

    // Deleta um questionário
    public void deleteById(Long id) {
        if (!questionarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("questionario.not_found");
        }
        questionarioRepository.deleteById(id);
    }
}