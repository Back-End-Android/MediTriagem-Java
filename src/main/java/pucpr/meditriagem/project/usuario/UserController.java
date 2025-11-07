package pucpr.meditriagem.project.usuario;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.usuario.dto.UserRequestDTO;
import pucpr.meditriagem.project.usuario.dto.UserResponseDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UsuarioService service;

    // Criar um novo usuário (ex: Admin criando outro Admin)
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") // <-- MUDANÇA: Era hasCargo
    public ResponseEntity<UserResponseDTO> salvar(@RequestBody @Valid UserRequestDTO dados) {
        var usuarioSalvo = service.salvar(dados);
        URI localizacao = URI.create("/usuarios/" + usuarioSalvo.getId());
        return ResponseEntity.created(localizacao).body(usuarioSalvo);
    }

    // Listar todos os usuários
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") // <-- MÁXIMA ATENÇÃO AQUI
    public ResponseEntity<List<UserResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Buscar um usuário por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // <-- MUDANÇA: Era hasCargo
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Atualizar um usuário
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // <-- MUDANÇA: Era hasCargo
    public ResponseEntity<UserResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UserRequestDTO dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    // Deletar um usuário
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // <-- MUDANÇA: Era hasCargo
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}