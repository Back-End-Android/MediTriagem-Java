package pucpr.meditriagem.project.consulta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pucpr.meditriagem.project.agendamento.AgendamentoRepository;
import pucpr.meditriagem.project.agendamento.AgendamentoService;
import pucpr.meditriagem.project.consulta.dto.ConsultaRequestDTO;
import pucpr.meditriagem.project.consulta.dto.ConsultaResponseDTO;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.triagem.TriagemRepository;
import pucpr.meditriagem.project.triagem.dto.TriagemResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    // Repositórios e Serviços necessários
    @Autowired private ConsultaRepository consultaRepository;
    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private TriagemRepository triagemRepository;
    @Autowired private AgendamentoService agendamentoService; // Assume-se que este serviço tem o método buscarPorId(Long id)

    // --- CREATE ---
    @Transactional
    public ConsultaResponseDTO criar(ConsultaRequestDTO dto) {

        // 1. Busca e validação das entidades relacionadas
        var agendamento = agendamentoRepository.findById(dto.agendamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("agendamento.not_found"));

        var triagem = triagemRepository.findById(dto.triagemId())
                .orElseThrow(() -> new ResourceNotFoundException("triagem.not_found"));

        // 2. Validação de Unicidade: Garante que um agendamento só tenha uma consulta
        if (consultaRepository.existsByAgendamento(agendamento)) {
            throw new BusinessRuleException("consulta.agendamento.duplicado");
        }

        // 3. Mapeamento do DTO para a Entidade
        Consulta consulta = new Consulta();
        consulta.setAgendamento(agendamento);
        consulta.setTriagem(triagem);
        consulta.setDataHoraInicio(dto.dataHoraInicio());
        consulta.setClassificacaoFinal(dto.classificacaoFinal());
        consulta.setObservacoesMedicas(dto.observacoesMedicas());
        consulta.setEncaminhamentos(dto.encaminhamentos());
        consulta.setDataHoraFim(dto.dataHoraFim());

        // 4. Salva e retorna o DTO de resposta
        Consulta consultaSalva = consultaRepository.save(consulta);
        return toResponseDTO(consultaSalva);
    }

    // --- READ (Busca por ID) ---
    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarPorId(Long id) {
        var consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("consulta.not_found"));
        return toResponseDTO(consulta);
    }

    // --- READ ALL ---
    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarTodos() {
        return consultaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --- UPDATE ---
    @Transactional
    public ConsultaResponseDTO atualizar(Long id, ConsultaRequestDTO dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("consulta.not_found"));

        // Atualiza campos
        consulta.setDataHoraInicio(dto.dataHoraInicio());
        consulta.setDataHoraFim(dto.dataHoraFim());
        consulta.setObservacoesMedicas(dto.observacoesMedicas());
        consulta.setEncaminhamentos(dto.encaminhamentos());
        consulta.setClassificacaoFinal(dto.classificacaoFinal());

        Consulta consultaAtualizada = consultaRepository.save(consulta);
        return toResponseDTO(consultaAtualizada);
    }

    // --- DELETE ---
    @Transactional
    public void excluir(Long id) {
        if (!consultaRepository.existsById(id)) {
            throw new ResourceNotFoundException("consulta.not_found");
        }
        consultaRepository.deleteById(id);
    }

    // --- Mapper Helper (Mapeia a Entidade para o DTO de Resposta) ---
    private ConsultaResponseDTO toResponseDTO(Consulta consulta) {
        // Busca e mapeia o Agendamento (usando o AgendamentoService)
        var agendamentoDTO = agendamentoService.buscarPorId(consulta.getAgendamento().getId());

        // Mapeia a Triagem
        var triagemDTO = new TriagemResponseDTO(consulta.getTriagem());

        return new ConsultaResponseDTO(
                consulta,
                agendamentoDTO,
                triagemDTO
        );
    }
}