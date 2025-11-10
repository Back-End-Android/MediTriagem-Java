package pucpr.meditriagem.project.triagem;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.enfermeiro.Enfermeiro;
import pucpr.meditriagem.project.enfermeiro.EnfermeiroRepository;
import pucpr.meditriagem.project.paciente.Paciente;
import pucpr.meditriagem.project.paciente.PacienteRepository;
import pucpr.meditriagem.project.questionario.QuestionarioRepository;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;
import pucpr.meditriagem.project.triagem.dto.TriagemRequestDTO;
import pucpr.meditriagem.project.triagem.dto.TriagemResponseDTO;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final PacienteRepository pacienteRepository;
    private final QuestionarioRepository questionarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnfermeiroRepository enfermeiroRepository;

    // Construtor com injeção de dependências
    public TriagemService(TriagemRepository triagemRepository,
                          PacienteRepository pacienteRepository,
                          QuestionarioRepository questionarioRepository,
                          UsuarioRepository usuarioRepository,
                          EnfermeiroRepository enfermeiroRepository) {
        this.triagemRepository = triagemRepository;
        this.pacienteRepository = pacienteRepository;
        this.questionarioRepository = questionarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.enfermeiroRepository = enfermeiroRepository;
    }

    // Cria uma nova triagem realizada pelo enfermeiro
    public TriagemResponseDTO criarTriagem(TriagemRequestDTO dados) {
        // Valida se o enfermeiro existe diretamente na tabela enfermeiro
        Enfermeiro enfermeiro = enfermeiroRepository.findById(dados.getEnfermeiroId())
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        // Valida se o paciente existe
        Paciente paciente = pacienteRepository.findById(dados.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        // Valida se o questionário existe
        QuestionarioSintomas questionario = questionarioRepository.findById(dados.getQuestionarioId())
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

        // Valida se o questionário pertence ao paciente
        if (!questionario.getPacienteId().equals(dados.getPacienteId())) {
            throw new RuntimeException("Questionário não pertence ao paciente informado");
        }

        // Cria a triagem com os dados fornecidos
        Triagem triagem = new Triagem();
        triagem.setEnfermeiroId(dados.getEnfermeiroId());
        triagem.setPacienteId(dados.getPacienteId());
        triagem.setQuestionario(questionario);
        triagem.setPressaoArterial(dados.getPressaoArterial());
        triagem.setFrequenciaCardiaca(dados.getFrequenciaCardiaca());
        triagem.setSaturacao(dados.getSaturacao());
        triagem.setSinaisDesidratacao(dados.getSinaisDesidratacao());

        // Se o questionário indica febre, a temperatura deve ser medida
        if (questionario.getFebre() != null && questionario.getFebre() && dados.getTemperatura() == null) {
            throw new RuntimeException("Temperatura deve ser medida quando o paciente relata febre");
        }
        triagem.setTemperatura(dados.getTemperatura());

        triagem.setPreCondicao(dados.getPreCondicao());
        triagem.setTomaRemedioControlado(dados.getTomaRemedioControlado());
        triagem.setQualRemedio(dados.getQualRemedio());
        triagem.setHistoricoFamiliar(dados.getHistoricoFamiliar());
        triagem.setObservacao(dados.getObservacao());
        triagem.setNivelUrgencia(dados.getNivelUrgencia() != null ? dados.getNivelUrgencia() : NivelUrgencia.MEDIA);
        triagem.setIsActivated(true);
        triagem.setDataHoraCriacao(LocalDateTime.now());

        Triagem triagemSalva = triagemRepository.save(triagem);
        return new TriagemResponseDTO(triagemSalva);
    }

    // Busca todas as triagens (com controle de acesso)
    public List<TriagemResponseDTO> findAll() {
        Usuario usuarioLogado = getUsuarioLogado();

        List<Triagem> triagens;

        // Admin pode ver todas
        if (usuarioLogado.getCargo() == Cargo.ADMIN) {
            triagens = triagemRepository.findAll();
        }
        // Enfermeiro pode ver todas as triagens - verifica se existe na tabela enfermeiro
        else if (isEnfermeiro(usuarioLogado.getId())) {
            triagens = triagemRepository.findAll();
        }
        // Paciente pode ver apenas suas próprias triagens
        else if (usuarioLogado.getCargo() == Cargo.PACIENTE) {
            Paciente paciente = pacienteRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
            triagens = triagemRepository.findByPacienteId(paciente.getId());
        }
        // Outros usuários não podem ver
        else {
            throw new RuntimeException("Acesso negado");
        }

        return triagens.stream()
                .map(TriagemResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Busca triagem por ID (com controle de acesso)
    public TriagemResponseDTO findById(Long id) {
        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Triagem não encontrada"));

        // Verifica permissão de acesso
        verificarPermissaoAcesso(triagem);

        return new TriagemResponseDTO(triagem);
    }

    // Verifica se o usuário tem permissão para acessar a triagem
    private void verificarPermissaoAcesso(Triagem triagem) {
        Usuario usuarioLogado = getUsuarioLogado();

        // Admin pode acessar qualquer triagem
        if (usuarioLogado.getCargo() == Cargo.ADMIN) {
            return;
        }

        // Enfermeiro pode acessar qualquer triagem - verifica na tabela enfermeiro
        if (isEnfermeiro(usuarioLogado.getId())) {
            return;
        }

        // Paciente pode acessar apenas suas próprias triagens
        if (usuarioLogado.getCargo() == Cargo.PACIENTE) {
            Paciente paciente = pacienteRepository.findByUsuarioId(usuarioLogado.getId())
                    .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

            if (!triagem.getPacienteId().equals(paciente.getId())) {
                throw new RuntimeException("Acesso negado: você só pode visualizar suas próprias triagens");
            }
            return;
        }

        // Outros usuários não podem acessar
        throw new RuntimeException("Acesso negado");
    }

    // Altera uma triagem existente (apenas enfermeiro)
    public TriagemResponseDTO alterarTriagem(Long id, TriagemRequestDTO dados) {
        // Busca a triagem existente
        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Triagem não encontrada"));

        // Verifica se a triagem está ativa
        if (!triagem.getIsActivated()) {
            throw new RuntimeException("Não é possível alterar uma triagem desativada");
        }

        // Obtém o usuário logado e verifica se é enfermeiro na tabela enfermeiro
        Usuario usuarioLogado = getUsuarioLogado();
        if (!isEnfermeiro(usuarioLogado.getId())) {
            throw new RuntimeException("Apenas enfermeiros podem alterar triagens");
        }

        // Verifica se o enfermeiro logado é o mesmo que criou a triagem
        if (!triagem.getEnfermeiroId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Apenas o enfermeiro que criou a triagem pode alterá-la");
        }

        // Valida se o questionário existe (se foi fornecido um novo)
        if (dados.getQuestionarioId() != null) {
            QuestionarioSintomas questionario = questionarioRepository.findById(dados.getQuestionarioId())
                    .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));

            // Valida se o questionário pertence ao paciente
            if (!questionario.getPacienteId().equals(triagem.getPacienteId())) {
                throw new RuntimeException("Questionário não pertence ao paciente da triagem");
            }
            triagem.setQuestionario(questionario);
        }

        // Atualiza os dados vitais (apenas se fornecidos)
        if (dados.getPressaoArterial() != null) {
            triagem.setPressaoArterial(dados.getPressaoArterial());
        }
        if (dados.getFrequenciaCardiaca() != null) {
            triagem.setFrequenciaCardiaca(dados.getFrequenciaCardiaca());
        }
        if (dados.getSaturacao() != null) {
            triagem.setSaturacao(dados.getSaturacao());
        }
        if (dados.getSinaisDesidratacao() != null) {
            triagem.setSinaisDesidratacao(dados.getSinaisDesidratacao());
        }

        // Validação de temperatura se houver febre no questionário
        if (triagem.getQuestionario().getFebre() != null &&
                triagem.getQuestionario().getFebre() &&
                dados.getTemperatura() == null) {
            throw new RuntimeException("Temperatura deve ser medida quando o paciente relata febre");
        }
        if (dados.getTemperatura() != null) {
            triagem.setTemperatura(dados.getTemperatura());
        }

        // Atualiza perguntas adicionais
        if (dados.getPreCondicao() != null) {
            triagem.setPreCondicao(dados.getPreCondicao());
        }
        if (dados.getTomaRemedioControlado() != null) {
            triagem.setTomaRemedioControlado(dados.getTomaRemedioControlado());
        }
        if (dados.getQualRemedio() != null) {
            triagem.setQualRemedio(dados.getQualRemedio());
        }
        if (dados.getHistoricoFamiliar() != null) {
            triagem.setHistoricoFamiliar(dados.getHistoricoFamiliar());
        }
        if (dados.getObservacao() != null) {
            triagem.setObservacao(dados.getObservacao());
        }
        if (dados.getNivelUrgencia() != null) {
            triagem.setNivelUrgencia(dados.getNivelUrgencia());
        }

        Triagem triagemAtualizada = triagemRepository.save(triagem);
        return new TriagemResponseDTO(triagemAtualizada);
    }

    // Método auxiliar para verificar se um usuário é enfermeiro
    private boolean isEnfermeiro(Long usuarioId) {
        return enfermeiroRepository.existsById(usuarioId);
    }

    // Obtém o usuário logado
    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Salva uma triagem
    public Triagem save(Triagem triagem) {
        return triagemRepository.save(triagem);
    }

    // Deleta uma triagem
    public void deleteById(Long id) {
        if (!triagemRepository.existsById(id)) {
            throw new RuntimeException("Triagem não encontrada");
        }
        triagemRepository.deleteById(id);
    }
}