package pucpr.meditriagem.project.medico;

import pucpr.meditriagem.project.exceptions.BusinessRuleException;
import pucpr.meditriagem.project.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.especialidade.EspecialidadeRepository;
import pucpr.meditriagem.project.medico.dto.MedicoRequestDTO;
import pucpr.meditriagem.project.medico.dto.MedicoResponseDTO;
import pucpr.meditriagem.project.usuario.Cargo;
import pucpr.meditriagem.project.usuario.Usuario;
import pucpr.meditriagem.project.usuario.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public MedicoResponseDTO salvar(MedicoRequestDTO dados) {


        if (medicoRepository.findByCpf(dados.cpf()).isPresent()) {
            throw new BusinessRuleException("medico.cpf.duplicado");
        }
        if (medicoRepository.findByCrm(dados.crm()).isPresent()) {
            throw new BusinessRuleException("medico.crm.duplicado");
        }
        if (usuarioRepository.findByEmail(dados.email()).isPresent()) {
            throw new BusinessRuleException("medico.email.duplicado");
        }

        // 1 Buscar a Especialidade
        var especialidade = especialidadeRepository.findById(dados.especialidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("especialidade.not_found"));

        // 2 cria o Usuario
        var senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(
                null,
                dados.email(),
                senhaCriptografada,
                Cargo.MEDICO
        );

        // 3 criar o Medico
        var medico = new Medico(
                dados.nomeCompleto(),
                dados.cpf(),
                dados.crm(),
                dados.dtNascimento(),
                usuario,
                especialidade
        );

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
                .orElseThrow(() -> new ResourceNotFoundException("medico.not_found"));
        return new MedicoResponseDTO(medico);
    }

    // alterar
    public MedicoResponseDTO alterar(Long id, MedicoRequestDTO dados) {
        var medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("medico.not_found"));

        var especialidade = especialidadeRepository.findById(dados.especialidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("especialidade.not_found"));

        if (!medico.getCpf().equals(dados.cpf()) && medicoRepository.findByCpf(dados.cpf()).isPresent()) {
            throw new BusinessRuleException("medico.cpf.duplicado");
        }
        if (!medico.getCrm().equals(dados.crm()) && medicoRepository.findByCrm(dados.crm()).isPresent()) {
            throw new BusinessRuleException("medico.crm.duplicado");
        }

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
            throw new ResourceNotFoundException("medico.not_found");
        }

        medicoRepository.deleteById(id);
    }
}