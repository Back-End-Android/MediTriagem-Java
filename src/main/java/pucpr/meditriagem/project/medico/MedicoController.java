package pucpr.meditriagem.project.medico;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importe
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.medico.dto.MedicoRequestDTO;
import pucpr.meditriagem.project.medico.dto.MedicoResponseDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService service;

    // Salvar
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MedicoResponseDTO> salvar(@RequestBody @Valid MedicoRequestDTO dados) {
        var medicoSalvo = service.salvar(dados);
        URI localizacao = URI.create("/medicos/" + medicoSalvo.id());
        return ResponseEntity.created(localizacao).body(medicoSalvo);
    }

    // listar
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MedicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // buscar por ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MedicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // alterar
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MedicoResponseDTO> alterar(@PathVariable Long id, @RequestBody @Valid MedicoRequestDTO dados) {
        return ResponseEntity.ok(service.alterar(id, dados));
    }

    // excluir
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}