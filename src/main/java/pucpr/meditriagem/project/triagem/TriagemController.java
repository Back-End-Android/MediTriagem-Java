package pucpr.meditriagem.project.triagem;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.triagem.dto.TriagemRequestDTO;
import pucpr.meditriagem.project.triagem.dto.TriagemResponseDTO;
import pucpr.meditriagem.project.triagem.TriagemService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/triagem")
public class TriagemController {

    @Autowired
    private TriagemService triagemService;

    // Criar triagem (apenas enfermeiro pode criar)
    @PostMapping("/criar")
    @PreAuthorize("hasAuthority('ENFERMEIRO')")
    public ResponseEntity<TriagemResponseDTO> criarTriagem(@RequestBody @Valid TriagemRequestDTO triagemRequestDTO) {
        TriagemResponseDTO novaTriagem = triagemService.criarTriagem(triagemRequestDTO);
        URI localizacao = URI.create("/api/triagem/" + novaTriagem.getId_triagem());
        return ResponseEntity.created(localizacao).body(novaTriagem);
    }

    // Listar triagens (com controle de acesso: enfermeiro, paciente dono, admin)
    @GetMapping("/get_all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TriagemResponseDTO>> listarTriagens() {
        List<TriagemResponseDTO> triagens = triagemService.findAll();
        return ResponseEntity.ok(triagens);
    }

    // Buscar triagem por ID (com controle de acesso: enfermeiro, paciente dono, admin)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TriagemResponseDTO> buscarPorId(@PathVariable Long id) {
        TriagemResponseDTO triagem = triagemService.findById(id);
        return ResponseEntity.ok(triagem);
    }

    // Deletar triagem (apenas admin)
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        triagemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
