package pucpr.meditriagem.project.consulta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pucpr.meditriagem.project.agendamento.AgendamentoRepository;
import pucpr.meditriagem.project.agendamento.AgendamentoService;
import pucpr.meditriagem.project.consulta.dto.ConsultaRequestDTO;
import pucpr.meditriagem.project.consulta.dto.ConsultaResponseDTO;
import pucpr.meditriagem.project.medico.MedicoRepository; // NOVA INJEÇÃO
import pucpr.meditriagem.project.triagem.TriagemRepository;
import pucpr.meditriagem.project.triagem.dto.TriagemResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    // Repositórios e Serviços necessários
    @Autowired private ConsultaRepository consultaRepository;
    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private TriagemRepository triagemRepository;
    @Autowired private AgendamentoService agendamentoService;
    @Autowired private MedicoRepository medicoRepository; // INJEÇÃO ADICIONADA
    // O MedicoRepository é essencial para buscar o objeto Médico, que é obrigatório na Consulta.


    // --- CREATE ---
    @Transactional
    public ConsultaResponseDTO criar(ConsultaRequestDTO dto) {

        // 1. Busca e validação das entidades relacionadas
        var agendamento = agendamentoRepository.findById(dto.agendamentoId())
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        var triagem = triagemRepository.findById(dto.triagemId())
                .orElseThrow(() -> new RuntimeException("Triagem não encontrada."));

        // NOVO: Busca e validação do Médico (o medicoId é obrigatório pelo DTO)
        var medico = medicoRepository.findById(dto.medicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado."));


        // 2. Validação de Unicidade: Garante que um agendamento só tenha uma consulta
        if (consultaRepository.existsByAgendamento(agendamento)) {
            throw new RuntimeException("Já existe uma consulta registrada para este agendamento.");
        }

        // 3. Mapeamento do DTO para a Entidade usando o construtor completo
        Consulta consulta = new Consulta(
                agendamento,
                triagem,
                medico, // MÉDICO ADICIONADO AQUI
                dto.dataHoraInicio() != null ? dto.dataHoraInicio() : LocalDateTime.now(), // Usa hora atual se não fornecida
                dto.classificacaoFinal()
        );

        // Preenche campos opcionais/atualizáveis
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
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada."));
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
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada."));

        // CORRIGIDO: Usa o método de atualização da entidade (Consulta.java)
        consulta.atualizar(dto);

        Consulta consultaAtualizada = consultaRepository.save(consulta);
        return toResponseDTO(consultaAtualizada);
    }

    // --- DELETE ---
    @Transactional
    public void excluir(Long id) {
        if (!consultaRepository.existsById(id)) {
            throw new RuntimeException("Consulta não encontrada.");
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