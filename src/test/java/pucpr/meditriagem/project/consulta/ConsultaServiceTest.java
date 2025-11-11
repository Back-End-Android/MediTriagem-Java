package pucpr.meditriagem.project.consulta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pucpr.meditriagem.project.agendamento.Agendamento;
import pucpr.meditriagem.project.agendamento.AgendamentoRepository;
import pucpr.meditriagem.project.agendamento.AgendamentoService;
import pucpr.meditriagem.project.agendamento.dto.AgendamentoDTO;
import pucpr.meditriagem.project.consulta.dto.ConsultaRequestDTO;
import pucpr.meditriagem.project.consulta.dto.ConsultaResponseDTO;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.medico.Medico;
import pucpr.meditriagem.project.medico.MedicoRepository;
import pucpr.meditriagem.project.questionario.QuestionarioSintomas;
import pucpr.meditriagem.project.triagem.NivelUrgencia;
import pucpr.meditriagem.project.triagem.Triagem;
import pucpr.meditriagem.project.triagem.TriagemRepository;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ConsultaService")
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private TriagemRepository triagemRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private AgendamentoService agendamentoService;


    @InjectMocks
    private ConsultaService consultaService;


    private ConsultaRequestDTO requestDTO;
    private Agendamento agendamentoFalso;
    private Triagem triagemFalsa;
    private Medico medicoFalso;
    private Consulta consultaFalsa;
    private AgendamentoDTO agendamentoDtoFalso;

    @BeforeEach
    void setUp() {

        requestDTO = new ConsultaRequestDTO(
                1L, // agendamentoId
                1L, // triagemId
                1L, // medicoId
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Paciente com dor de cabeça",
                "Repouso e medicação",
                ClassificacaoFinal.VERDE
        );

        agendamentoFalso = new Agendamento();
        agendamentoFalso.setId(1L);
        agendamentoFalso.setPacienteId(1L);
        agendamentoFalso.setMedicoId(1L);

        QuestionarioSintomas questionarioFalso = new QuestionarioSintomas();
        questionarioFalso.setId(1L);
        questionarioFalso.setPacienteId(1L);

        triagemFalsa = new Triagem();
        triagemFalsa.setId_triagem(1L);
        triagemFalsa.setQuestionario(questionarioFalso);
        triagemFalsa.setPacienteId(1L);
        triagemFalsa.setEnfermeiroId(1L);
        triagemFalsa.setNivelUrgencia(NivelUrgencia.MEDIA);
        triagemFalsa.setDataHoraCriacao(LocalDateTime.now());

        Usuario usuarioMedicoFalso = new Usuario(1L, "medico@email.com", "123", Cargo.MEDICO);
        medicoFalso = new Medico();
        medicoFalso.setId(1L);
        medicoFalso.setUsuario(usuarioMedicoFalso);

        consultaFalsa = new Consulta(
                agendamentoFalso,
                triagemFalsa,
                medicoFalso,
                requestDTO.dataHoraInicio(),
                requestDTO.classificacaoFinal()
        );
        consultaFalsa.setId(1L);

        agendamentoDtoFalso = new AgendamentoDTO(1L, 1L, LocalDateTime.now(), LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("Deve criar consulta com sucesso")
    void deveCriarConsultaComSucesso() {

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoFalso));
        when(triagemRepository.findById(1L)).thenReturn(Optional.of(triagemFalsa));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoFalso));

        when(consultaRepository.existsByAgendamento(any(Agendamento.class))).thenReturn(false);

        when(consultaRepository.save(any(Consulta.class))).thenReturn(consultaFalsa);

        when(agendamentoService.buscarPorId(anyLong())).thenReturn(agendamentoDtoFalso);

        ConsultaResponseDTO resultado = consultaService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id()); // Verifica se o ID da consulta é 1
        assertEquals(1L, resultado.agendamento().pacienteId()); // Verifica se o paciente do agendamento é 1
        assertEquals(1L, resultado.triagem().getPacienteId()); // Verifica se o paciente da triagem é 1
        assertEquals(1L, resultado.medicoId()); // Verifica se o ID do médico é 1

        verify(consultaRepository, times(1)).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção (Req. 8) ao criar consulta com agendamento duplicado")
    void deveLancarExcecaoAoCriarConsultaComAgendamentoDuplicado() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoFalso));
        when(triagemRepository.findById(1L)).thenReturn(Optional.of(triagemFalsa));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medicoFalso));

        when(consultaRepository.existsByAgendamento(any(Agendamento.class))).thenReturn(true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> consultaService.criar(requestDTO)
        );

        assertEquals("consulta.agendamento.duplicado", exception.getMessage());

        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção (Req. 8) ao criar consulta com triagem inexistente")
    void deveLancarExcecaoAoCriarConsultaComTriagemInexistente() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoFalso));

        when(triagemRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> consultaService.criar(requestDTO)
        );

        assertEquals("triagem.not_found", exception.getMessage());
        verify(consultaRepository, never()).save(any(Consulta.class));
    }
}