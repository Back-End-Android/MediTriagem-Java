package pucpr.meditriagem.project.triagem;

import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.dto.QuestionarioDTO;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.util.List;

@Service
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor com injeção de dependências
    public TriagemService(TriagemRepository triagemRepository, UsuarioRepository usuarioRepository) {
        this.triagemRepository = triagemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Cria uma nova triagem para um paciente
    public Triagem criarTriagem(Long pacienteId, QuestionarioDTO questionario) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Triagem triagem = new Triagem();
        return triagemRepository.save(triagem);
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