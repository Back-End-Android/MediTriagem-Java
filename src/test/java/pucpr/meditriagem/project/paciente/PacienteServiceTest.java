package pucpr.meditriagem.project.paciente;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ForbiddenOperationException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.paciente.dto.PacienteRequestDTO;
import pucpr.meditriagem.project.paciente.dto.PacienteResponseDTO;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PacienteService")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PacienteService pacienteService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContext;
    @Mock
    private SecurityContext securityContextMock;
    @Mock
    private Authentication authenticationMock;

    private PacienteRequestDTO pacienteRequestDTO;
    private Paciente paciente;
    private Usuario usuario;
    private String emailDoDono = "joana@email.com";

    @BeforeEach
    void setUp() {
        // dados de entrada
        pacienteRequestDTO = new PacienteRequestDTO(
                "Joana Silva", "11122233344", "Feminino",
                LocalDate.of(1990, 1, 1), emailDoDono, "senha123"
        );

        usuario = new Usuario(1L, emailDoDono, "senha_hash_falsa", Cargo.PACIENTE);
        paciente = new Paciente(
                "Joana Silva", "11122233344", "Feminino",
                LocalDate.of(1990, 1, 1), usuario,
                emailDoDono, "senha_hash_falsa"
        );
        paciente.setId(1L);

        mockedSecurityContext = Mockito.mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContext.close();
    }


    @Test
    @DisplayName("Deve salvar paciente com sucesso")
    void deveSalvarPacienteComSucesso() {
        when(pacienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senha_hash_falsa");
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        PacienteResponseDTO resultado = pacienteService.salvar(pacienteRequestDTO);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção (Req. 8) ao salvar com CPF duplicado")
    void deveLancarExcecaoAoSalvarPacienteComCpfDuplicado() {
        when(pacienteRepository.findByCpf("11122233344")).thenReturn(Optional.of(paciente));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> pacienteService.salvar(pacienteRequestDTO)
        );
        assertEquals("paciente.cpf.duplicado", exception.getMessage());
    }


    @Test
    @DisplayName("Deve buscar paciente por ID com sucesso")
    void deveBuscarMedicoPorIdComSucesso() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        PacienteResponseDTO resultado = pacienteService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
    }

    @Test
    @DisplayName("Deve excluir paciente (Req. 8) quando o usuário é o dono")
    void deveExcluirPacienteQuandoUsuarioForODono() {

        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getName()).thenReturn(emailDoDono);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        doNothing().when(pacienteRepository).deleteById(1L);

        pacienteService.excluir(1L);

        verify(pacienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção (Req. 8) ao excluir quando o usuário NÃO é o dono")
    void deveLancarExcecaoAoExcluirQuandoUsuarioNaoForODono() {
        when(SecurityContextHolder.getContext()).thenReturn(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getName()).thenReturn("invasor@email.com");

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        ForbiddenOperationException exception = assertThrows(
                ForbiddenOperationException.class,
                () -> pacienteService.excluir(1L)
        );
        assertEquals("paciente.unauthorized", exception.getMessage());
        verify(pacienteRepository, never()).deleteById(anyLong());
    }

}