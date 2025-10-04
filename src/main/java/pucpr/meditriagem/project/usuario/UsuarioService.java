package pucpr.meditriagem.project.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pucpr.meditriagem.project.dto.UserDTO;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO save(UserDTO userDTO) {
        if (repository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("E-mail já cadastrado!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(userDTO.getNome());
        usuario.setEmail(userDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(userDTO.getSenha()));


        if (userDTO.getCargo() == null || userDTO.getCargo().isBlank()) {
            usuario.setCargo("USER");
        } else {
            usuario.setCargo(userDTO.getCargo().toUpperCase());
        }

        Usuario usuarioSalvo = repository.save(usuario);
        return toDTO(usuarioSalvo);
    }

    public List<UserDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Integer id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    public UserDTO update(Integer id, UserDTO userDTO) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        if (userDTO.getNome() != null) {
            usuario.setNome(userDTO.getNome());
        }
        if (userDTO.getEmail() != null) {
            usuario.setEmail(userDTO.getEmail());
        }
        if (userDTO.getCargo() != null) {
            usuario.setCargo(userDTO.getCargo().toUpperCase());
        }

        Usuario usuarioAtualizado = repository.save(usuario);
        return toDTO(usuarioAtualizado);
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado para exclusão!");
        }
        repository.deleteById(id);
    }

    private UserDTO toDTO(Usuario usuario) {
        return new UserDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                null,
                usuario.getCargo()
        );
    }
}