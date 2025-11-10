package pucpr.meditriagem.project.paciente;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.paciente.dto.PacienteRequestDTO;
import pucpr.meditriagem.project.paciente.dto.PacienteResponseDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService service;

    // Salvar
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> salvar(@RequestBody @Valid PacienteRequestDTO dados) {
        var pacienteSalvo = service.salvar(dados);
        URI localizacao = URI.create("/api/pacientes/" + pacienteSalvo.id());
        return ResponseEntity.created(localizacao).body(pacienteSalvo);
    }

    // listar
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // buscar por ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // alterar
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PacienteResponseDTO> alterar(@PathVariable Long id, @RequestBody @Valid PacienteRequestDTO dados) {
        return ResponseEntity.ok(service.alterar(id, dados));
    }

    // excluir
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

