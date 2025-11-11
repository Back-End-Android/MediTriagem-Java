package pucpr.meditriagem.project.consulta;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pucpr.meditriagem.project.agendamento.dto.AgendamentoDTO;
import pucpr.meditriagem.project.consulta.dto.ConsultaRequestDTO;
// --- AQUI ESTÁ A CORREÇÃO ---
import pucpr.meditriagem.project.consulta.dto.ConsultaResponseDTO;
// --- FIM DA CORREÇÃO ---
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.triagem.NivelUrgencia;
import pucpr.meditriagem.project.triagem.Triagem;
import pucpr.meditriagem.project.triagem.dto.TriagemResponseDTO;

// (Importações de Segurança - já que estamos usando @SpringBootTest)
// Não precisamos dos mocks de segurança individuais aqui

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do ConsultaController")
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultaService consultaService;

    private ConsultaRequestDTO requestDTO;
    private ConsultaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ConsultaRequestDTO(
                1L, 1L, 1L,
                LocalDateTime.now(), null, "Observações",
                "Encaminhamentos", ClassificacaoFinal.VERDE
        );

        AgendamentoDTO agendamentoDtoFalso = new AgendamentoDTO(1L, 1L, LocalDateTime.now(), null, null);

        Triagem triagemFalsa = new Triagem();
        triagemFalsa.setId_triagem(1L);
        triagemFalsa.setPacienteId(1L);
        triagemFalsa.setNivelUrgencia(NivelUrgencia.MEDIA);

        TriagemResponseDTO triagemDtoFalso = new TriagemResponseDTO(triagemFalsa);

        responseDTO = new ConsultaResponseDTO(
                1L, agendamentoDtoFalso, triagemDtoFalso, 1L,
                LocalDateTime.now(), null, "Observações",
                "Encaminhamentos", ClassificacaoFinal.VERDE
        );
    }

    @Test
    @DisplayName("POST /api/consultas - Deve criar consulta e retornar 201 (MEDICO logado)")
    @WithMockUser(authorities = "MEDICO") // Passa no @PreAuthorize
    void criarConsulta_DeveRetornar201_QuandoUsuarioForMedico() throws Exception {
        when(consultaService.criar(any(ConsultaRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/consultas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.observacoesMedicas", is("Observações")));
    }

    @Test
    @DisplayName("POST /api/consultas - Deve retornar 403 (Forbidden) se usuário for PACIENTE")
    @WithMockUser(authorities = "PACIENTE")
    void criarConsulta_DeveRetornar403_QuandoUsuarioForPaciente() throws Exception {

        mockMvc.perform(post("/api/consultas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem", is("ERR-005: Acesso proibido. Voce nao tem permissao para este recurso.")));
    }

    @Test
    @DisplayName("POST /api/consultas - Deve retornar 400 (Req. 8) quando Agendamento duplicado")
    @WithMockUser(authorities = "ADMIN")
    void criarConsulta_DeveRetornar400_QuandoAgendamentoDuplicado() throws Exception {
        when(consultaService.criar(any(ConsultaRequestDTO.class)))
                .thenThrow(new BusinessRuleException("consulta.agendamento.duplicado"));

        mockMvc.perform(post("/api/consultas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("CONSULTA-002: Ja existe uma consulta registrada para este agendamento")));
    }

    @Test
    @DisplayName("DELETE /api/consultas/{id} - Deve retornar 404 (Req. 8) quando ID não encontrado")
    @WithMockUser(authorities = "ADMIN")
    void excluir_DeveRetornar404_QuandoIdNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("consulta.not_found"))
                .when(consultaService).excluir(999L);

        mockMvc.perform(delete("/api/consultas/999")
                        .with(csrf()))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", is("CONSULTA-001: Consulta nao encontrada")));
    }
}