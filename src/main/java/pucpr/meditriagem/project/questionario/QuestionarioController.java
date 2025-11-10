package pucpr.meditriagem.project.questionario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.questionario.dto.QuestionarioDTO;
import pucpr.meditriagem.project.questionario.dto.QuestionarioRequestDTO;
import pucpr.meditriagem.project.questionario.dto.QuestionarioResponseDTO;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questionario")
public class QuestionarioController {

    @Autowired
    private QuestionarioService questionarioService;

    @PostMapping("/criar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuestionarioResponseDTO> criarQuestionario(@RequestBody QuestionarioRequestDTO questionarioRequestDTO) {
        QuestionarioResponseDTO novoQuestionario = questionarioService.criarQuestionario(
                questionarioRequestDTO.getPacienteId(),
                questionarioRequestDTO.getQuestionario()
        );
        URI localizacao = URI.create("/api/questionario/" + novoQuestionario.getId());
        return ResponseEntity.created(localizacao).body(novoQuestionario);
    }

    @GetMapping("/get_all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuestionarioResponseDTO>> listarQuestionarios() {
        return ResponseEntity.ok(questionarioService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuestionarioResponseDTO> buscarPorId(@PathVariable Long id) {
        QuestionarioResponseDTO questionario = questionarioService.findById(id);
        return ResponseEntity.ok(questionario);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuestionarioResponseDTO>> buscarPorPaciente(@PathVariable Long pacienteId) {
        List<QuestionarioResponseDTO> questionarios = questionarioService.buscarPorPacienteId(pacienteId);
        return ResponseEntity.ok(questionarios);
    }

    // NOVO ENDPOINT: Alterar questionário (apenas o próprio paciente)
    @PutMapping("/alterar/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuestionarioResponseDTO> alterarQuestionario(
            @PathVariable Long id,
            @RequestBody QuestionarioDTO questionarioDTO) {
        QuestionarioResponseDTO questionarioAtualizado = questionarioService.alterarQuestionario(id, questionarioDTO);
        return ResponseEntity.ok(questionarioAtualizado);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ENFERMEIRO')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        questionarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}