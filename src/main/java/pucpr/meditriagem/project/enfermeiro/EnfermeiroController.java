package pucpr.meditriagem.project.enfermeiro;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroRequestDTO;
import pucpr.meditriagem.project.enfermeiro.dto.EnfermeiroResponseDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/enfermeiros")
public class EnfermeiroController {

    @Autowired
    private EnfermeiroService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EnfermeiroResponseDTO> cadastrar(@Valid @RequestBody EnfermeiroRequestDTO dto) {
        var resp = service.cadastrar(dto);
        return ResponseEntity
                .created(URI.create("/enfermeiros/" + resp.id()))
                .body(resp);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EnfermeiroResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnfermeiroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EnfermeiroResponseDTO> alterar(@PathVariable Long id,
                                                         @Valid @RequestBody EnfermeiroRequestDTO dto) {
        return ResponseEntity.ok(service.alterar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
