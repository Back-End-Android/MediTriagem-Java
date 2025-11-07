package pucpr.meditriagem.project.medico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.especialidade.EspecialidadeRepository; // IMPORTE
import pucpr.meditriagem.project.medico.dto.MedicoRequestDTO;
import pucpr.meditriagem.project.medico.dto.MedicoResponseDTO;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository; // IMPORTE

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository; // Precisa dele

    @Autowired
    private PasswordEncoder passwordEncoder;


    public MedicoResponseDTO salvar(MedicoRequestDTO dados) {

        // Validação (Req. 8)
        if (medicoRepository.findByCpf(dados.cpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado"); // TODO: Exceção customizada
        }
        if (medicoRepository.findByCrm(dados.crm()).isPresent()) {
            throw new RuntimeException("CRM já cadastrado"); // TODO: Exceção customizada
        }
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado"); // TODO: Exceção customizada
        }

        // 1 Buscar a Especialidade
        var especialidade = especialidadeRepository.findById(dados.especialidadeId())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        // 2 cria o Usuario
        var senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(
                null,
                dados.email(),
                senhaCriptografada,
                Cargo.MEDICO // <-- Define a permissão
        );

        // 3 criar o Medico
        var medico = new Medico(
                dados.nomeCompleto(),
                dados.cpf(),
                dados.crm(),
                dados.dtNascimento(),
                usuario, // 4. Ligar o Usuario
                especialidade // 5. Ligar a Especialidade
        );

        //  Salvar o Medico (Cascade salva o Usuario junto)
        medicoRepository.save(medico);

        return new MedicoResponseDTO(medico);
    }

    // listar
    public List<MedicoResponseDTO> listarTodos() {
        return medicoRepository.findAll()
                .stream()
                .map(MedicoResponseDTO::new)
                .collect(Collectors.toList());
    }

    // buscar por ID
    public MedicoResponseDTO buscarPorId(Long id) {
        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        return new MedicoResponseDTO(medico);
    }

    // alterar
    public MedicoResponseDTO alterar(Long id, MedicoRequestDTO dados) {
        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        var especialidade = especialidadeRepository.findById(dados.especialidadeId())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        // Atualiza  dados (sem ser email/senha)
        medico.atualizarDados(
                dados.nomeCompleto(),
                dados.crm(),
                dados.dtNascimento(),
                especialidade
        );

        medicoRepository.save(medico);
        return new MedicoResponseDTO(medico);
    }

    // excluir
    public void excluir(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new RuntimeException("Médico não encontrado");
        }
        // O @OneToOne com orphanRemoval=true deleta o Usuario junto
        medicoRepository.deleteById(id);
    }
}