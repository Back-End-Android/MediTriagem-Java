package pucpr.meditriagem.project.medico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pucpr.meditriagem.project.especialidade.Especialidade;
import pucpr.meditriagem.project.especialidade.EspecialidadeRepository;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.medico.dto.MedicoRequestDTO;
import pucpr.meditriagem.project.medico.dto.MedicoResponseDTO;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do MedicoService")
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MedicoService medicoService;

    private MedicoRequestDTO medicoRequestDTO;
    private Medico medico;
    private Especialidade especialidade;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade("Cardiologia");
        especialidade.setId(1L);

        medicoRequestDTO = new MedicoRequestDTO(
                "Dr. Carlos Souza",
                "11122233344",
                "CRM12345",
                LocalDate.of(1980, 8, 10),
                1L,
                "carlos@email.com",
                "senha123"
        );

        usuario = new Usuario(1L, "carlos@email.com", "senhaEncriptada", Cargo.MEDICO);

        medico = new Medico(
                "Dr. Carlos Souza",
                "11122233344",
                "CRM12345",
                LocalDate.of(1980, 8, 10),
                usuario,
                especialidade
        );
        medico.setId(1L);
    }

    @Test
    @DisplayName("Deve salvar médico com sucesso")
    void deveSalvarMedicoComSucesso() {
        // Arrange
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));
        when(medicoRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(medicoRepository.findByCrm(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senhaEncriptada");
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        // Act
        MedicoResponseDTO resultado = medicoService.salvar(medicoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Dr. Carlos Souza", resultado.nomeCompleto());
        assertEquals("CRM12345", resultado.crm());
        assertEquals("Cardiologia", resultado.especialidadeNome());
        verify(medicoRepository, times(1)).save(any(Medico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar médico com especialidade inexistente")
    void deveLancarExcecaoAoSalvarMedicoComEspecialidadeInexistente() {
        // Arrange
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> medicoService.salvar(medicoRequestDTO)
        );

        assertEquals("especialidade.not_found", exception.getMessage());
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar médico com CPF duplicado")
    void deveLancarExcecaoAoSalvarMedicoComCpfDuplicado() {
        // Arrange
        when(medicoRepository.findByCpf("11122233344")).thenReturn(Optional.of(medico));

        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> medicoService.salvar(medicoRequestDTO)
        );

        assertEquals("medico.cpf.duplicado", exception.getMessage());
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar médico com CRM duplicado")
    void deveLancarExcecaoAoSalvarMedicoComCrmDuplicado() {
        // Arrange
        when(medicoRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(medicoRepository.findByCrm("CRM12345")).thenReturn(Optional.of(medico));

        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> medicoService.salvar(medicoRequestDTO)
        );

        assertEquals("medico.crm.duplicado", exception.getMessage());
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    @DisplayName("Deve listar todos os médicos")
    void deveListarTodosMedicos() {
        // Arrange
        Medico medico2 = new Medico(
                "Dra. Ana Lima",
                "55566677788",
                "CRM67890",
                LocalDate.of(1985, 3, 15),
                new Usuario(2L, "ana@email.com", "senha", Cargo.MEDICO),
                especialidade
        );
        medico2.setId(2L);

        List<Medico> medicos = Arrays.asList(medico, medico2);
        when(medicoRepository.findAll()).thenReturn(medicos);

        // Act
        List<MedicoResponseDTO> resultado = medicoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Dr. Carlos Souza", resultado.get(0).nomeCompleto());
        assertEquals("Dra. Ana Lima", resultado.get(1).nomeCompleto());
        verify(medicoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar médico por ID com sucesso")
    void deveBuscarMedicoPorIdComSucesso() {
        // Arrange
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        // Act
        MedicoResponseDTO resultado = medicoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Dr. Carlos Souza", resultado.nomeCompleto());
        assertEquals("CRM12345", resultado.crm());
        verify(medicoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar médico inexistente")
    void deveLancarExcecaoAoBuscarMedicoInexistente() {
        // Arrange
        when(medicoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> medicoService.buscarPorId(999L)
        );

        assertEquals("medico.not_found", exception.getMessage());
    }

    @Test
    @DisplayName("Deve alterar médico com sucesso")
    void deveAlterarMedicoComSucesso() {
        // Arrange
        MedicoRequestDTO dadosAtualizados = new MedicoRequestDTO(
                "Dr. Carlos Souza Atualizado",
                "11122233344",  // Mesmo CPF, não verifica duplicidade
                "CRM99999",      // CRM diferente, verifica duplicidade
                LocalDate.of(1980, 8, 10),
                1L,
                "carlos.novo@email.com",
                null
        );

        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(especialidadeRepository.findById(1L)).thenReturn(Optional.of(especialidade));
        when(medicoRepository.findByCrm("CRM99999")).thenReturn(Optional.empty());  // Apenas CRM mudou
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        // Act
        MedicoResponseDTO resultado = medicoService.alterar(1L, dadosAtualizados);

        // Assert
        assertNotNull(resultado);
        verify(medicoRepository, times(1)).save(any(Medico.class));
    }

    @Test
    @DisplayName("Deve excluir médico com sucesso")
    void deveExcluirMedicoComSucesso() {
        // Arrange
        when(medicoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(medicoRepository).deleteById(1L);

        // Act
        medicoService.excluir(1L);

        // Assert
        verify(medicoRepository, times(1)).existsById(1L);
        verify(medicoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir médico inexistente")
    void deveLancarExcecaoAoExcluirMedicoInexistente() {
        // Arrange
        when(medicoRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> medicoService.excluir(999L)
        );

        assertEquals("medico.not_found", exception.getMessage());
        verify(medicoRepository, never()).deleteById(anyLong());
    }
}