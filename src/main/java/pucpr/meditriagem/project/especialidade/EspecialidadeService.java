package pucpr.meditriagem.project.especialidade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.especialidade.dto.EspecialidadeRequestDTO;
import pucpr.meditriagem.project.especialidade.dto.EspecialidadeResponseDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository repository;

    public List<EspecialidadeResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(EspecialidadeResponseDTO::new)
                .collect(Collectors.toList());
    }

    public EspecialidadeResponseDTO salvar(EspecialidadeRequestDTO dados) {
        if (repository.findByNome(dados.nome()).isPresent()) {
            throw new RuntimeException("Especialidade com este nome já existe");
        }
        var especialidade = new Especialidade(dados.nome());
        repository.save(especialidade);
        return new EspecialidadeResponseDTO(especialidade);
    }

    public EspecialidadeResponseDTO atualizar(Long id, EspecialidadeRequestDTO dados) {
        var especialidade = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID da especialidade não encontrado"));

        var especialidadeExistente = repository.findByNome(dados.nome());
        if (especialidadeExistente.isPresent() && !Objects.equals(especialidadeExistente.get().getId(), id)) {
            throw new RuntimeException("Já existe outra especialidade com este nome");
        }

        especialidade.setNome(dados.nome());
        repository.save(especialidade);

        return new EspecialidadeResponseDTO(especialidade);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ID da especialidade não encontrado");
        }

        // tem q fazer verificar se tem medico com essa especialidade antes de excluir

        repository.deleteById(id);
    }
}