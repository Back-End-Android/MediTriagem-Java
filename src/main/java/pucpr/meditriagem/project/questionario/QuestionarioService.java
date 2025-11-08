package pucpr.meditriagem.project.questionario;

import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.paciente.Paciente;
import pucpr.meditriagem.project.paciente.PacienteRepository;
import pucpr.meditriagem.project.questionario.dto.QuestionarioDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
public class QuestionarioService {

    private final QuestionarioRepository questionarioRepository;
    private final PacienteRepository pacienteRepository;

    // Construtor com injeção de dependências
    public QuestionarioService(QuestionarioRepository questionarioRepository, PacienteRepository pacienteRepository) {
        this.questionarioRepository = questionarioRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // Cria um novo questionário para um paciente
    public QuestionarioSintomas criarQuestionario(Long pacienteId, QuestionarioDTO questionarioDTO) {
        // Busca o paciente pelo ID
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        // Extrai os dados específicos do paciente
        String nomeCompleto = paciente.getNomeCompleto();
        String genero = paciente.getGenero();
        LocalDate dtNascimento = paciente.getDtNascimento();
        
        // Calcula a idade a partir da data de nascimento
        Integer idade = calcularIdade(dtNascimento);

        // Converte QuestionarioDTO para QuestionarioSintomas e adiciona dados do paciente
        QuestionarioSintomas questionario = converterDTOParaQuestionario(questionarioDTO, pacienteId, nomeCompleto, genero, idade);
        
        // Define a data e hora de criação
        questionario.setDataHoraCriacao(LocalDateTime.now());

        // Salva o questionário
        return questionarioRepository.save(questionario);
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
    public List<QuestionarioSintomas> findAll() {
        return questionarioRepository.findAll();
    }

    // Busca questionário por ID
    public QuestionarioSintomas findById(Long id) {
        return questionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Questionário não encontrado"));
    }

    // Busca questionários por paciente
    public List<QuestionarioSintomas> buscarPorPacienteId(Long pacienteId) {
        return questionarioRepository.buscarPorPacienteId(pacienteId);
    }

    // Salva um questionário
    public QuestionarioSintomas save(QuestionarioSintomas questionario) {
        return questionarioRepository.save(questionario);
    }

    // Deleta um questionário
    public void deleteById(Long id) {
        if (!questionarioRepository.existsById(id)) {
            throw new RuntimeException("Questionário não encontrado");
        }
        questionarioRepository.deleteById(id);
    }
}

