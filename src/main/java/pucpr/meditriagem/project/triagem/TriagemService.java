package pucpr.meditriagem.project.triagem;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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

    // Construtor com injeção de dependências
    public TriagemService(TriagemRepository triagemRepository, 
                         PacienteRepository pacienteRepository,
                         QuestionarioRepository questionarioRepository,
                         UsuarioRepository usuarioRepository) {
        this.triagemRepository = triagemRepository;
        this.pacienteRepository = pacienteRepository;
        this.questionarioRepository = questionarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Cria uma nova triagem realizada pelo enfermeiro
    public TriagemResponseDTO criarTriagem(TriagemRequestDTO dados) {
        // Valida se o enfermeiro existe e tem cargo ENFERMEIRO
        Usuario enfermeiro = usuarioRepository.findById(dados.getEnfermeiroId())
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));
        
        if (enfermeiro.getCargo() != Cargo.ENFERMEIRO) {
            throw new RuntimeException("Usuário não é um enfermeiro");
        }

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
        triagem.setNivelUrgencia(dados.getNivelUrgencia() != null ? dados.getNivelUrgencia() : NivelUrgencia.MEDIA); // Default MEDIA, será calculado depois
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
        // Enfermeiro pode ver todas as triagens
        else if (usuarioLogado.getCargo() == Cargo.ENFERMEIRO) {
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
        
        // Enfermeiro pode acessar qualquer triagem
        if (usuarioLogado.getCargo() == Cargo.ENFERMEIRO) {
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

    // Método para desativar triagem
//    public Boolean update() {
//        return Triagem.setIsActivated();
//    }


}