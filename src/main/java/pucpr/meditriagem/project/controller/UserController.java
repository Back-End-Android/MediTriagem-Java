package pucpr.meditriagem.project.controller;

import pucpr.meditriagem.project.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario")
@Tag(name = "Usuário", description = "APIs de gerenciamento de usuários")
public class UserController {

    private List<UserDTO> usuarios = new ArrayList<>();

    @PostMapping
    @Operation(summary = "Salva um usuário", description = "Salva um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário Salvo com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Os dados do usuário estão incorretos."),
    })
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(1);
        usuarios.add(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @GetMapping
    @Operation(summary = "Obter a lista de usuários", description = "Retorna a lista de usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recuperado com sucesso"),
    })
    public List<UserDTO> findAll() {
        return usuarios;
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable("id") Integer id, @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        return userDTO;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
    }


}
