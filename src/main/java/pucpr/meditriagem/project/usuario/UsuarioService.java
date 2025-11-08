package pucpr.meditriagem.project.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.usuario.dto.UserRequestDTO;
import pucpr.meditriagem.project.usuario.dto.UserResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método para o Admin criar um novo usuário (ex: outro Admin)
    public UserResponseDTO salvar(UserRequestDTO dados) {
        if (usuarioRepository.findByEmail(dados.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        var senhaCriptografada = passwordEncoder.encode(dados.getSenha());

        var usuario = new Usuario(
                null,
                dados.getEmail(),
                senhaCriptografada,
                dados.getCargo()
        );

        usuarioRepository.save(usuario);
        return new UserResponseDTO(usuario);
    }

    // Método para o Admin atualizar um usuário
    public UserResponseDTO atualizar(Long id, UserRequestDTO dados) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));


        if (dados.getEmail() != null) {
            usuario.setEmail(dados.getEmail());
        }
        if (dados.getCargo() != null) {
            usuario.setCargo(dados.getCargo());
        }
        // Se a senha foi enviada, atualiza
        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(dados.getSenha()));
        }

        usuarioRepository.save(usuario);
        return new UserResponseDTO(usuario);
    }

    // Método para o Admin listar todos os usuários
    public List<UserResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Método para o Admin buscar um usuário
    public UserResponseDTO buscarPorId(Long id) {
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new UserResponseDTO(usuario);
    }

    // Método para o Admin deletar um usuário
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}