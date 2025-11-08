package pucpr.meditriagem.project.triagem;

import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.paciente.Paciente;
import pucpr.meditriagem.project.paciente.PacienteRepository;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;
import pucpr.meditriagem.project.questionario.dto.QuestionarioDTO;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final PacienteRepository pacienteRepository;

    // Construtor com injeção de dependências
    public TriagemService(TriagemRepository triagemRepository, PacienteRepository pacienteRepository) {
        this.triagemRepository = triagemRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // Cria uma nova triagem para um paciente
    public Triagem criarTriagem(Long pacienteId, QuestionarioDTO questionarioDTO) {
        // Busca o paciente pelo ID
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        // Extrai os dados específicos do paciente
        String nomeCompleto = paciente.getNomeCompleto();
        String genero = paciente.getGenero();
        LocalDate dtNascimento = paciente.getDtNascimento();
        
        // Calcula a idade a partir da data de nascimento
        Integer idade = calcularIdade(dtNascimento);

        // Converte QuestionarioDTO para QuestionarioSintomas
        QuestionarioSintomas questionario = converterDTOParaQuestionario(questionarioDTO);

        // Cria a triagem com os dados específicos do paciente
        Triagem triagem = new Triagem();
        triagem.setPacienteId(pacienteId);
        triagem.setNomeCompleto(nomeCompleto);
        triagem.setGenero(genero);
        triagem.setIdade(idade);
        triagem.setQuestionario(questionario);
        triagem.setIsActivated(true);

        return triagemRepository.save(triagem);
    }

    // Calcula a idade a partir da data de nascimento
    private Integer calcularIdade(LocalDate dtNascimento) {
        if (dtNascimento == null) {
            return null;
        }
        return Period.between(dtNascimento, LocalDate.now()).getYears();
    }

    // Converte QuestionarioDTO para QuestionarioSintomas
    private QuestionarioSintomas converterDTOParaQuestionario(QuestionarioDTO dto) {
        QuestionarioSintomas questionario = new QuestionarioSintomas();
        questionario.setFebre(dto.febre());
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

    // Busca todas as triagens
    public List<Triagem> findAll() {
        return triagemRepository.findAll();
    }

    // Busca triagem por ID
    public Triagem findById(Long id) {
        return triagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Triagem não encontrada"));
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