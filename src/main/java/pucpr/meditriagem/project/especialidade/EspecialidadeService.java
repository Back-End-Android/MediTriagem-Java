package pucpr.meditriagem.project.especialidade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.especialidade.dto.EspecialidadeRequestDTO;
import pucpr.meditriagem.project.especialidade.dto.EspecialidadeResponseDTO;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;

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

    public EspecialidadeResponseDTO buscarPorId(Long id) {
        var especialidade = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("especialidade.not_found"));
        return new EspecialidadeResponseDTO(especialidade);
    }

    public EspecialidadeResponseDTO salvar(EspecialidadeRequestDTO dados) {
        if (repository.findByNome(dados.nome()).isPresent()) {
            throw new BusinessRuleException("especialidade.nome.duplicado");
        }
        var especialidade = new Especialidade(dados.nome());
        repository.save(especialidade);
        return new EspecialidadeResponseDTO(especialidade);
    }

    public EspecialidadeResponseDTO atualizar(Long id, EspecialidadeRequestDTO dados) {
        var especialidade = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("especialidade.not_found"));

        var especialidadeExistente = repository.findByNome(dados.nome());
        if (especialidadeExistente.isPresent() && !Objects.equals(especialidadeExistente.get().getId(), id)) {
            throw new BusinessRuleException("especialidade.nome.duplicado");
        }

        especialidade.setNome(dados.nome());
        repository.save(especialidade);

        return new EspecialidadeResponseDTO(especialidade);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("especialidade.not_found");
        }

        repository.deleteById(id);
    }
}