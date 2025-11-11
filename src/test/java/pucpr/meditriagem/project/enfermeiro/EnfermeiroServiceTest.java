package pucpr.meditriagem.project.enfermeiro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroRequestDTO;
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroResponseDTO;
import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.time.LocalDate;
import java.util.Optional;

// Importações estáticas essenciais
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EnfermeiroService")
class EnfermeiroServiceTest {

    @Mock
    private EnfermeiroRepository enfermeiroRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EnfermeiroService enfermeiroService;

    private EnfermeiroRequestDTO requestDTO;
    private Enfermeiro enfermeiro;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        requestDTO = new EnfermeiroRequestDTO(
                "Enf. Maria", "11122233344", "COREN123",
                LocalDate.of(1990, 5, 10),
                "maria@enf.com", "senha123"
        );

        usuario = new Usuario(1L, "maria@enf.com", "senha_hash_falsa", Cargo.ENFERMEIRO);

        enfermeiro = new Enfermeiro(
                "Enf. Maria", "11122233344", "COREN123",
                LocalDate.of(1990, 5, 10), usuario
        );


        enfermeiro.setId(1L);
    }

    @Test
    @DisplayName("Deve cadastrar enfermeiro com sucesso")
    void deveCadastrarEnfermeiroComSucesso() {

        when(enfermeiroRepository.existsByCpf(anyString())).thenReturn(false);
        when(enfermeiroRepository.existsByCoren(anyString())).thenReturn(false);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        when(enfermeiroRepository.save(any(Enfermeiro.class))).thenReturn(enfermeiro);

        when(passwordEncoder.encode(anyString())).thenReturn("senha_hash_falsa");

        EnfermeiroResponseDTO resultado = enfermeiroService.cadastrar(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Enf. Maria", resultado.nomeCompleto());
        assertEquals("maria@enf.com", resultado.email());

        // Verifica se o 'save' foi realmente chamado
        verify(enfermeiroRepository, times(1)).save(any(Enfermeiro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção (Req. 8) ao cadastrar com COREN duplicado")
    void deveLancarExcecaoAoCadastrarComCorenDuplicado() {

        when(enfermeiroRepository.existsByCpf(anyString())).thenReturn(false);


        when(enfermeiroRepository.existsByCoren("COREN123")).thenReturn(true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> enfermeiroService.cadastrar(requestDTO)
        );


        assertEquals("enfermeiro.coren.duplicado", exception.getMessage());

        verify(enfermeiroRepository, never()).save(any(Enfermeiro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção (Req. 8) ao excluir enfermeiro inexistente")
    void deveLancarExcecaoAoExcluirEnfermeiroInexistente() {

        when(enfermeiroRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> enfermeiroService.excluir(999L)
        );

        assertEquals("enfermeiro.not_found", exception.getMessage());

        verify(enfermeiroRepository, never()).delete(any(Enfermeiro.class));
    }
}