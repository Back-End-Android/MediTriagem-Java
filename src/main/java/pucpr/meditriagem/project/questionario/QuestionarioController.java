package pucpr.meditriagem.project.questionario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucpr.meditriagem.project.questionario.dto.QuestionarioRequestDTO;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questionario")
public class QuestionarioController {

    @Autowired
    private QuestionarioService questionarioService;

    @PostMapping("/criar")
    public ResponseEntity<QuestionarioSintomas> criarQuestionario(@RequestBody QuestionarioRequestDTO questionarioRequestDTO) {
        QuestionarioSintomas novoQuestionario = questionarioService.criarQuestionario(
                questionarioRequestDTO.getPacienteId(), 
                questionarioRequestDTO.getQuestionario()
        );
        URI localizacao = URI.create("/api/questionario/" + novoQuestionario.getId());
        return ResponseEntity.created(localizacao).body(novoQuestionario);
    }

    @GetMapping("/get_all")
    public ResponseEntity<List<QuestionarioSintomas>> listarQuestionarios() {
        return ResponseEntity.ok(questionarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionarioSintomas> buscarPorId(@PathVariable Long id) {
        QuestionarioSintomas questionario = questionarioService.findById(id);
        return ResponseEntity.ok(questionario);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<QuestionarioSintomas>> buscarPorPaciente(@PathVariable Long pacienteId) {
        List<QuestionarioSintomas> questionarios = questionarioService.buscarPorPacienteId(pacienteId);
        return ResponseEntity.ok(questionarios);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        questionarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

