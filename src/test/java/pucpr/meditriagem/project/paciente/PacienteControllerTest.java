package pucpr.meditriagem.project.paciente;

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
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ForbiddenOperationException;
import pucpr.meditriagem.project.paciente.dto.PacienteRequestDTO;
import pucpr.meditriagem.project.paciente.dto.PacienteResponseDTO;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do PacienteController")
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PacienteService pacienteService;
    private PacienteRequestDTO pacienteRequestDTO;
    private PacienteResponseDTO pacienteResponseDTO;

    @BeforeEach
    void setUp() {
        pacienteRequestDTO = new PacienteRequestDTO(
                "Joana Silva", "11122233344", "Feminino",
                LocalDate.of(1990, 1, 1), "joana@email.com", "senha123"
        );
        pacienteResponseDTO = new PacienteResponseDTO(
                1L, "Joana Silva", "11122233344", "Feminino",
                LocalDate.of(1990, 1, 1), "joana@email.com"
        );
    }

    @Test
    @DisplayName("POST /api/pacientes - Deve criar paciente (endpoint público) e retornar 201")

    void salvar_DeveRetornar201_QuandoEndpointPublico() throws Exception {
        when(pacienteService.salvar(any(PacienteRequestDTO.class))).thenReturn(pacienteResponseDTO);
        mockMvc.perform(post("/api/pacientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteRequestDTO)))
                .andExpect(status().isCreated());
    }


    @Test
    @DisplayName("POST /api/pacientes - Deve retornar 400 quando CPF duplicado")

    void salvar_DeveRetornar400_QuandoCpfDuplicado() throws Exception {

        when(pacienteService.salvar(any(PacienteRequestDTO.class)))
                .thenThrow(new BusinessRuleException("paciente.cpf.duplicado"));


        mockMvc.perform(post("/api/pacientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("GET /api/pacientes - Deve listar pacientes e retornar 200 (se autenticado)")

    @WithMockUser(authorities = "ADMIN")
    void listarTodos_DeveRetornar200_QuandoAutenticado() throws Exception {

        when(pacienteService.listarTodos()).thenReturn(List.of(pacienteResponseDTO));


        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk()) // <-- Agora vai dar 200!
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/pacientes/{id} - Deve retornar 403 quando usuário não for o dono")
    @WithMockUser
    void excluir_DeveRetornar403_QuandoUsuarioNaoForDono() throws Exception {

        doThrow(new ForbiddenOperationException("paciente.unauthorized"))
                .when(pacienteService).excluir(1L);

        mockMvc.perform(delete("/api/pacientes/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}