package pucpr.meditriagem.project.agendamento;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pucpr.meditriagem.project.dto.AgendamentoDTO;

import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repo;

    public AgendamentoService(AgendamentoRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AgendamentoDTO criar(AgendamentoDTO dto) {
        Agendamento a = toEntity(dto);
        a.setObservacao(dto.observacao());
        Agendamento salvo = repo.save(a);
        return toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public AgendamentoDTO buscarPorId(Long id) {
        Agendamento a = repo.findById(id).orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        return toDTO(a);
    }

    @Transactional
    public AgendamentoDTO atualizar(Long id, AgendamentoDTO dto) {
        Agendamento a = repo.findById(id).orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        a.setMedicoId(dto.medicoId());
        a.setPacienteId(dto.pacienteId());
        a.setInicio(dto.inicio());
        a.setFim(dto.fim());
        a.setObservacao(dto.observacao());
        return toDTO(repo.save(a));
    }

    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Agendamento não encontrado");
        repo.deleteById(id);
    }

    // ---- helpers simples de mapeamento ----
    private AgendamentoDTO toDTO(Agendamento a) {
        return new AgendamentoDTO(
                a.getId(),
                a.getMedicoId(),
                a.getPacienteId(),
                a.getInicio(),
                a.getFim(),
                a.getObservacao()
        );
    }

    private Agendamento toEntity(AgendamentoDTO dto) {
        Agendamento a = new Agendamento();
        a.setMedicoId(dto.medicoId());
        a.setPacienteId(dto.pacienteId());
        a.setInicio(dto.inicio());
        a.setFim(dto.fim());
        a.setObservacao(dto.observacao());
        return a;
    }
}
