package pucpr.meditriagem.project.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.dto.UserDTO;
import pucpr.meditriagem.project.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario")
@Tag(name = "Usuário", description = "APIs de gerenciamento de usuários")
public class UserController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO userDTO) {
        UserDTO usuarioSalvo = service.save(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuário pelo ID")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody @Valid UserDTO userDTO) {
        UserDTO usuarioAtualizado = service.update(id, userDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um usuário pelo ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}