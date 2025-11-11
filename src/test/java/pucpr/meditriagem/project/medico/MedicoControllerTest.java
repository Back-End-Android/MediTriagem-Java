package pucpr.meditriagem.project.medico;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
// --- 1. IMPORTAR A "MÁGICA" ---
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.medico.dto.MedicoRequestDTO;
import pucpr.meditriagem.project.medico.dto.MedicoResponseDTO;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
// --- 2. IMPORTAR O 'csrf()' ---
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // <-- Isto está correto
@DisplayName("Testes do MedicoController")
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicoService medicoService;

    private MedicoRequestDTO medicoRequestDTO;
    private MedicoResponseDTO medicoResponseDTO;

    @BeforeEach
    void setUp() {
        medicoRequestDTO = new MedicoRequestDTO(
                "Dr. Carlos Souza", "11122233344", "CRM12345",
                LocalDate.of(1980, 8, 10), 1L,
                "carlos@email.com", "senha123"
        );
        medicoResponseDTO = new MedicoResponseDTO(
                1L, "Dr. Carlos Souza", "CRM12345",
                LocalDate.of(1980, 8, 10), "carlos@email.com", "Cardiologia"
        );
    }

    @Test
    @DisplayName("POST /medicos - Deve salvar médico com sucesso")
    // --- 3. "FALSIFICAR" O LOGIN DO ADMIN ---
    @WithMockUser(authorities = "ADMIN")
    void deveSalvarMedicoComSucesso() throws Exception {
        when(medicoService.salvar(any(MedicoRequestDTO.class))).thenReturn(medicoResponseDTO);

        mockMvc.perform(post("/medicos")
                        .with(csrf()) // <-- Adiciona o 'csrf' (necessário para POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                .andExpect(status().isCreated()) // <-- Agora vai dar 201
                .andExpect(header().string("Location", "/medicos/1"));
    }

    @Test
    @DisplayName("POST /medicos - Deve retornar 400 quando CPF duplicado")
    // --- "FALSIFICAR" O LOGIN DO ADMIN ---
    @WithMockUser(authorities = "ADMIN")
    void deveLancarExcecaoAoSalvarComCpfDuplicado() throws Exception {
        when(medicoService.salvar(any(MedicoRequestDTO.class)))
                .thenThrow(new BusinessRuleException("medico.cpf.duplicado"));

        mockMvc.perform(post("/medicos")
                        .with(csrf()) // <-- Adiciona o 'csrf'
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                .andExpect(status().isBadRequest()); // <-- Agora vai dar 400
    }

    @Test
    @DisplayName("POST /medicos - Deve retornar 403 (Forbidden) se usuário não for ADMIN")
    // --- "FALSIFICAR" O LOGIN DE UM PACIENTE ---
    @WithMockUser(authorities = "PACIENTE")
    void deveRetornar403AoSalvarSeNaoForAdmin() throws Exception {

        // (Não precisa de 'when', a segurança barra antes)

        mockMvc.perform(post("/medicos")
                        .with(csrf()) // <-- Adiciona o 'csrf'
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicoRequestDTO)))
                .andExpect(status().isForbidden()); // <-- Agora vai dar 403
    }


    @Test
    @DisplayName("GET /medicos - Deve listar todos os médicos")
    // --- "FALSIFICAR" UM LOGIN BÁSICO (isAuthenticated) ---
    @WithMockUser
    void deveListarTodosMedicos() throws Exception {
        when(medicoService.listarTodos()).thenReturn(List.of(medicoResponseDTO));

        mockMvc.perform(get("/medicos"))
                .andExpect(status().isOk()) // <-- Agora vai dar 200
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /medicos/{id} - Deve buscar médico por ID")
    @WithMockUser // <-- Falsifica o login
    void deveBuscarMedicoPorId() throws Exception {
        when(medicoService.buscarPorId(1L)).thenReturn(medicoResponseDTO);

        mockMvc.perform(get("/medicos/1"))
                .andExpect(status().isOk()) // <-- Agora vai dar 200
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /medicos/{id} - Deve retornar 404 quando médico não encontrado")
    @WithMockUser // <-- Falsifica o login
    void deveLancarExcecaoAoBuscarMedicoInexistente() throws Exception {
        when(medicoService.buscarPorId(999L))
                .thenThrow(new ResourceNotFoundException("medico.not_found"));

        mockMvc.perform(get("/medicos/999"))
                .andExpect(status().isNotFound()); // <-- Agora vai dar 404
    }

    // (O mesmo para os testes de PUT e DELETE...)
}