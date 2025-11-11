package pucpr.meditriagem.project.consulta;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.consulta.dto.ConsultaRequestDTO;
import pucpr.meditriagem.project.consulta.dto.ConsultaResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    // CREATE: POST /api/consultas
    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public ResponseEntity<ConsultaResponseDTO> criarConsulta(@RequestBody @Valid ConsultaRequestDTO dto) {
        var response = consultaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ ALL: GET /api/consultas
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ConsultaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(consultaService.listarTodos());
    }

    // READ ONE: GET /api/consultas/{id}
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConsultaResponseDTO> buscarConsultaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    // UPDATE: PUT /api/consultas/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO') or hasAuthority('ADMIN')")
    public ResponseEntity<ConsultaResponseDTO> atualizarConsulta(@PathVariable Long id, @RequestBody @Valid ConsultaRequestDTO dto) {
        return ResponseEntity.ok(consultaService.atualizar(id, dto));
    }

    // DELETE: DELETE /api/consultas/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> excluirConsulta(@PathVariable Long id) {
        consultaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}