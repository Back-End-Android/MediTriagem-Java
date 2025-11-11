package pucpr.meditriagem.project.enfermeiro;

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
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroRequestDTO;
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroResponseDTO;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do EnfermeiroController")
class EnfermeiroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnfermeiroService enfermeiroService;


    private EnfermeiroRequestDTO requestDTO;
    private EnfermeiroResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new EnfermeiroRequestDTO(
                "Enf. Maria", "11122233344", "COREN123",
                LocalDate.of(1990, 5, 10),
                "maria@enf.com", "senha123"
        );
        responseDTO = new EnfermeiroResponseDTO(
                1L, "Enf. Maria", "11122233344", "COREN123",
                LocalDate.of(1990, 5, 10), "maria@enf.com"
        );
    }


    @Test
    @DisplayName("POST /enfermeiros - Deve cadastrar e retornar 201 (ADMIN logado)")
    @WithMockUser(authorities = "ADMIN")
    void cadastrar_DeveRetornar201_QuandoUsuarioForAdmin() throws Exception {
        when(enfermeiroService.cadastrar(any(EnfermeiroRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/enfermeiros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /enfermeiros - Deve retornar 403 (Forbidden) se usuário NÃO for ADMIN")
    @WithMockUser(authorities = "PACIENTE") // <-- Falsifica o login de Paciente
    void cadastrar_DeveRetornar403_QuandoUsuarioNaoForAdmin() throws Exception {

        mockMvc.perform(post("/enfermeiros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /enfermeiros - Deve retornar 400 (Req. 8) quando COREN duplicado")
    @WithMockUser(authorities = "ADMIN")
    void cadastrar_DeveRetornar400_QuandoCorenDuplicado() throws Exception {
        when(enfermeiroService.cadastrar(any(EnfermeiroRequestDTO.class)))
                .thenThrow(new BusinessRuleException("enfermeiro.coren.duplicado"));

        mockMvc.perform(post("/enfermeiros")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", is("ENFERMEIRO-003: O COREN informado ja esta cadastrado para outro enfermeiro.")));
    }

    @Test
    @DisplayName("DELETE /enfermeiros/{id} - Deve retornar 404 (Req. 8) quando ID não encontrado")
    @WithMockUser(authorities = "ADMIN")
    void excluir_DeveRetornar404_QuandoIdNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("enfermeiro.not_found"))
                .when(enfermeiroService).excluir(999L);

        mockMvc.perform(delete("/enfermeiros/999")
                        .with(csrf()))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", is("ENFERMEIRO-001: Enfermeiro nao encontrado com o ID fornecido.")));
    }
}