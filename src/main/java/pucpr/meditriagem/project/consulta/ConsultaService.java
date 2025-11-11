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
import pucpr.meditriagem.project.medico.MedicoRepository; // <-- MUDANÇA 1: Importar
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
    @Autowired private AgendamentoService agendamentoService;
    @Autowired private MedicoRepository medicoRepository; // <-- MUDANÇA 2: Injetar

    // --- CREATE ---
    @Transactional
    public ConsultaResponseDTO criar(ConsultaRequestDTO dto) {

        // 1. Busca e validação das entidades relacionadas
        var agendamento = agendamentoRepository.findById(dto.agendamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("agendamento.not_found"));

        var triagem = triagemRepository.findById(dto.triagemId())
                .orElseThrow(() -> new ResourceNotFoundException("triagem.not_found"));

        // --- MUDANÇA 3: Buscar o Médico ---
        var medico = medicoRepository.findById(dto.medicoId())
                .orElseThrow(() -> new ResourceNotFoundException("medico.not_found"));
        // --- FIM DA MUDANÇA ---

        // 2. Validação de Unicidade
        if (consultaRepository.existsByAgendamento(agendamento)) {
            throw new BusinessRuleException("consulta.agendamento.duplicado");
        }

        // --- MUDANÇA 4: Usar o construtor correto e settar tudo ---
        // 3. Mapeamento do DTO para a Entidade
        Consulta consulta = new Consulta(
                agendamento,
                triagem,
                medico, // <-- Passa o médico
                dto.dataHoraInicio(),
                dto.classificacaoFinal()
        );

        // Seta os campos restantes
        consulta.setObservacoesMedicas(dto.observacoesMedicas());
        consulta.setEncaminhamentos(dto.encaminhamentos());
        consulta.setDataHoraFim(dto.dataHoraFim());
        // --- FIM DA MUDANÇA ---

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

        // (O seu 'atualizar' não permite trocar o médico, agendamento ou triagem, o que está correto)

        // Atualiza campos (usando o método da entidade)
        consulta.atualizar(dto);

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
        // Busca e mapeia o Agendamento
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