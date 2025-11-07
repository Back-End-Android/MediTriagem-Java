package pucpr.meditriagem.project.especialidade;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.especialidade.dto.EspecialidadeRequestDTO;
import pucpr.meditriagem.project.especialidade.dto.EspecialidadeResponseDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService service;


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EspecialidadeResponseDTO> salvar(@RequestBody @Valid EspecialidadeRequestDTO dados) {
        var especialidade = service.salvar(dados);
        URI localizacao = URI.create("/especialidades/" + especialidade.id());
        return ResponseEntity.created(localizacao).body(especialidade);
    }

    // todos logados podem ver as especialidades
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EspecialidadeResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EspecialidadeResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EspecialidadeRequestDTO dados
    ) {
        var especialidadeAtualizada = service.atualizar(id, dados);
        return ResponseEntity.ok(especialidadeAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}